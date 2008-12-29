package sairepa.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class Act
{
  private int row = -1;
  private Connection db;
  private ActList actList;
  private FieldLayout fields;
  private Map<ActField, ActEntry> entries;
  private int fileId = -1;

  /**
   * Create a brand new and empty act
   */
  protected Act(Connection db, ActList actList, int fileId, FieldLayout fields) {
    Util.check(db != null);
    Util.check(actList != null);
    Util.check(fields != null);

    this.db = db;
    this.actList = actList;
    this.fields = fields;
    this.fileId = fileId;

    entries = new HashMap<ActField, ActEntry>();
    for (ActField field : fields) {
      entries.put(field, field.createEntry(this));
    }
  }

  /**
   * Already existing act loaded from the db
   */
  public Act(Connection db, ActList actList, int fileId, FieldLayout fields, int row) throws SQLException {
    Util.check(db != null);
    Util.check(actList != null);
    Util.check(fields != null);
    Util.check(row >= 0);

    this.db = db;
    this.fields = fields;
    this.row = row;
    this.actList = actList;
    this.fileId = fileId;

    reload();
  }

  public Collection<ActEntry> getEntries() {
    return entries.values();
  }

  public ActEntry getEntry(ActField field) {
    return entries.get(field);
  }

  public void setRow(int row) {
    setRow(row, true);
  }

  protected void setRow(int row, boolean reload) {
    this.row = row;

    if (reload) {
      reload();
    }
  }

  public int getRow() {
    return row;
  }

  /**
   * reload according to the row. may have some side effects
   */
  public void reload() {
    Util.check(fields != null);

    synchronized(db) {
      try {
        entries = new HashMap<ActField, ActEntry>();

	PreparedStatement fieldGetter = db.prepareStatement(
	    "SELECT fields.id FROM fields WHERE fields.name = ? AND fields.file = ? LIMIT 1");
	PreparedStatement st = db.prepareStatement(
	    "SELECT value FROM entries WHERE field = ? AND row = ? LIMIT 1");

	for (ActField field : fields) {
	  fieldGetter.setString(1, field.getName());
	  fieldGetter.setInt(2, fileId);
	  ResultSet set = fieldGetter.executeQuery();
	  Util.check(set.next());
	  int fieldId = set.getInt(1);
	  set.close();

	  st.setInt(1, fieldId);
	  st.setInt(2, row);

	  set = st.executeQuery();
	  Util.check(set.next());

	  String value = set.getString(1);
	  entries.put(field, new ActEntry(this, field, value));
	}
      } catch (SQLException e) {
	throw new RuntimeException("SQLException", e);
      }
    }
  }

  public void update() {
    synchronized(db) {
      Util.check(row >= 0);
      Util.check(entries != null);
      delete();

      try {
	PreparedStatement fieldIdGetter = db.prepareStatement(
	    "SELECT id FROM fields WHERE name = ? AND file = ? LIMIT 1");
	PreparedStatement insert = db.prepareStatement(
	    "INSERT INTO entries (field, row, value) VALUES (?, ?, ?)");

	for (ActEntry entry : entries.values()) {
	  fieldIdGetter.setString(1, entry.getField().getName());
	  fieldIdGetter.setInt(2, fileId);
	  ResultSet set = fieldIdGetter.executeQuery();
	  Util.check(set.next());
	  int fieldId = set.getInt(1);
	  set.close();

	  insert.setInt(1, fieldId);
	  insert.setInt(2, row);
	  insert.setString(3, entry.getValue());
	  insert.execute();
	}
      } catch (SQLException e) {
	throw new RuntimeException("SQLException", e);
      }
    }
  }

  protected void delete() {
    synchronized(db) {
      try {
	Util.check(row >= 0);
	Util.check(fields != null);

	PreparedStatement fieldIdGetter = db.prepareStatement(
	    "SELECT id FROM fields WHERE name = ? AND file = ? LIMIT 1");
	PreparedStatement delete = db.prepareStatement(
	    "DELETE FROM entries WHERE field = ? AND ROW = ?");

	for (ActField field : fields) {
	  fieldIdGetter.setString(1, field.getName());
	  fieldIdGetter.setInt(2, fileId);
	  ResultSet set = fieldIdGetter.executeQuery();
	  Util.check(set.next(),
		     "Can't find the field '" + field.getName() + "' / '" + fileId + "'");
	  int fieldId = set.getInt(1);
	  set.close();

	  delete.setInt(1, fieldId);
	  delete.setInt(2, row);
	  delete.execute();
	}
      } catch (SQLException e) {
	throw new RuntimeException("SQLException", e);
      }
    }
  }

  public boolean validate() {
    return fields.validate(this);
  }
}
