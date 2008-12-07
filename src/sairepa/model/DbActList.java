package sairepa.model;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Vector;

public class DbActList implements ActList
{
  private Connection db;
  private int fileId;
  private FieldLayout fields;
  private int rowCount;
  private String name;

  protected DbActList(Connection db, int fileId, FieldLayout fields, String name)
      throws SQLException, IOException {
    this.db = db;
    this.fileId = fileId;
    this.fields = fields;
    this.rowCount = computeRowCount();
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public FieldLayout getFields() {
    return fields;
  }

  public int computeRowCount() throws SQLException {
    synchronized(db) {
      PreparedStatement st = db.prepareStatement(
          "SELECT entries.row " +
          "FROM ENTRIES INNER JOIN FIELDS ON entries.field = fields.id " +
	  "WHERE fields.file = ? ORDER BY entries.row DESC LIMIT 1");
      st.setInt(1, fileId);

      ResultSet set = st.executeQuery();
      rowCount = ((set.next()) ? set.getInt(1) + 1 : 0);
      set.close();
      return rowCount;
    }
  }

  public int getRowCount() {
    return rowCount;
  }

  public Act getAct(int row) {
    synchronized(db) {
      try {
	Act act = new Act(db, this, fileId, fields, row);
	return act;
      } catch(SQLException e) {
	throw new RuntimeException("SQLException", e);
      }
    }
  }

  public void insert(Act act) {
    insert(act, rowCount);
  }

  public void insert(Act act, int row) {
    synchronized(db) {
      try {
	act.setRow(row, false);
	shiftAfter(row, 1);
	act.update();
	rowCount++;
      } catch (SQLException e) {
	throw new RuntimeException("SQLException", e);
      }
    }
  }

  public void delete(Act act) {
    synchronized(db) {
      try {
	act.delete();
	shiftAfter(act.getRow(), -1);
	rowCount--;
      } catch (SQLException e) {
	throw new RuntimeException("SQLException", e);
      }
    }
  }

  public ActListIterator iterator() {
    return new GenericActListIterator(db, this);
  }

  private void shiftAfter(int position, int shift) throws SQLException {
    PreparedStatement selectFields = db.prepareStatement(
        "SELECT fields.id FROM fields WHERE fields.file = ?");
    selectFields.setInt(1, fileId);
    ResultSet set = selectFields.executeQuery();

    PreparedStatement update = db.prepareStatement(
        "UPDATE entries SET row = row + ? " +
	"WHERE row >= ? AND field = ?");

    while(set.next()) {
      update.setInt(1, shift);
      update.setInt(2, position);
      update.setInt(3, set.getInt(1));
      update.execute();
    }
  }

  /**
   * this act is not stored until added to the list
   */
  public Act createAct() {
    return new Act(db, this, fileId, fields);
  }

  protected int getFileId() {
    return fileId;
  }

  public void refresh() {

  }

  protected class SortedActList implements ActList {
    private boolean desc;
    private int sortingFieldId = -1;

    public SortedActList(String sortedBy, boolean desc) {
      this.desc = desc;

      synchronized(db) {
	if (sortedBy != null) {
	  try {
	    PreparedStatement fieldGetter = db.prepareStatement(
                "SELECT fields.id FROM fields WHERE fields.name = ? AND fields.file = ? LIMIT 1");
	    fieldGetter.setString(1, sortedBy);
	    fieldGetter.setInt(2, fileId);
	    ResultSet set = fieldGetter.executeQuery();
	    Util.check(set.next());
	    sortingFieldId = set.getInt(1);
	  } catch (SQLException e) {
	    throw new RuntimeException("SQLException", e);
	  }
	} else {
	  sortingFieldId = -1;
	}
      }
    }

    public String getName() {
      return DbActList.this.getName();
    }

    public FieldLayout getFields() {
      return DbActList.this.getFields();
    }

    public int getRowCount() {
      return DbActList.this.getRowCount();
    }

    public Act getAct(int position) {
      synchronized(db) {
	int row = -1;

	if (sortingFieldId >= 0) {
	  try {
	    PreparedStatement rowGetter = db.prepareStatement(
                "SELECT row FROM entries WHERE field = ? ORDER BY value"
	        + (desc ? " DESC" : "") + " LIMIT 1 OFFSET ?");
	    rowGetter.setInt(1, sortingFieldId);
	    rowGetter.setInt(2, position);
	    ResultSet set = rowGetter.executeQuery();
	    Util.check(set.next());
	    row = set.getInt(1);
	  } catch(SQLException e) {
	    throw new RuntimeException("SQLException", e);
	  }
	} else if (desc) {
	  row = (getRowCount()-1) - position;
	} else {
	  row = position;
	}

	Util.check(row >= 0);
	return DbActList.this.getAct(row);
      }
    }

    /**
     * @return beware: can return this !
     */
    public ActList getSortedActList(String sortedBy, boolean desc) {
      return DbActList.this.getSortedActList(sortedBy, desc);
    }

    public void insert(Act act) {
      DbActList.this.insert(act);
    }

    public void insert(Act act, int row) {
      throw new UnsupportedOperationException("Can't do");
    }

    public void delete(Act act) {
      DbActList.this.delete(act);
    }

    public ActListIterator iterator() {
      return new GenericActListIterator(db, SortedActList.this);
    }

    public Act createAct() {
      return DbActList.this.createAct();
    }

    public void refresh() {
      DbActList.this.refresh();
    }
  }

  public ActList getSortedActList(String sortedBy, boolean desc) {
    return new SortedActList(sortedBy, desc);
  }
}
