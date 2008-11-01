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

public class ActList implements Iterable<Act>
{
  private Connection db;
  private int fileId;
  private ActField[] fields;
  private int rowCount;

  public ActList(Connection db, int fileId, ActField[] fields)
      throws SQLException, IOException {
    this.db = db;
    this.fileId = fileId;
    this.fields = fields;
    this.rowCount = computeRowCount();
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

  private class ActListIterator implements ListIterator<Act> {
    private int currentRow = 0;
    private Act lastActReturned = null;

    public ActListIterator() { }

    @Override
    public void add(Act a) {
      insert(a, currentRow + 1);
    }

    @Override
    public boolean hasNext() {
      return (currentRow < rowCount - 1);
    }

    @Override
    public boolean hasPrevious() {
      return (currentRow > 0);
    }

    @Override
    public Act next() {
      synchronized(db) {
	if (!hasNext()) {
	  Util.check(false);
	}
	currentRow++;
	return (lastActReturned = getAct(currentRow));
      }
    }

    @Override
    public int nextIndex() {
      return currentRow + 1;
    }

    @Override
    public Act previous() {
      synchronized(db) {
	if (!hasPrevious()) {
	  Util.check(false);
	}
	currentRow--;
	return (lastActReturned = getAct(currentRow));
      }
    }

    public Act seek(int position) {
      synchronized(db) {
	currentRow = position;
	return (lastActReturned = getAct(position));
      }
    }

    @Override
    public int previousIndex() {
      return currentRow - 1;
    }

    @Override
    public void remove() {
      delete(lastActReturned);
    }

    @Override
    public void	set(Act a) {
      synchronized(db) {
	a.setRow(currentRow, false);
	a.update();
      }
    }
  }

  public Act getAct(int row) {
    synchronized(db) {
      try {
	Act act = new Act(db, this, fields, row);
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

  public ListIterator<Act> iterator() {
    return new ActListIterator();
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
    return new Act(db, this, fields);
  }

  protected int getFileId() {
    return fileId;
  }
}
