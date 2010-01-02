package sairepa.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
     * Instanciate fully an act (no db call)
     */
    public Act(Connection db, ActList actList, int fileId, FieldLayout fields,
	       Map<ActField, String> entryValues, int row) {
	Util.check(db != null);
	Util.check(actList != null);
	Util.check(fields != null);

	this.db = db;
	this.actList = actList;
	this.fields = fields;
	this.fileId = fileId;
	this.entries = new HashMap<ActField, ActEntry>();
	this.row = row;

	for ( Map.Entry<ActField, String> entryVal : entryValues.entrySet() ) {
	    this.entries.put(entryVal.getKey(),
			     new ActEntry(this, entryVal.getKey(), entryVal.getValue()));
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

    public ActList getActList() {
	return actList;
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

		PreparedStatement st
		    = db.prepareStatement("SELECT fields.name, entries.value"
					  + " FROM fields INNER JOIN entries ON fields.id = entries.field"
					  + " WHERE fields.file = ? AND entries.row = ?");
		st.setInt(1, fileId);
		st.setInt(2, row);
		ResultSet set = st.executeQuery();
		try {
		    while (set.next()) {
			ActField field = fields.getField(set.getString(1));
			ActEntry entry = new ActEntry(this, field, set.getString(2));
			entries.put(field, entry);
		    }
		} finally {
		    set.close();
		}
	    } catch (SQLException e) {
		throw new RuntimeException("SQLException", e);
	    }
	}
    }

    public void update() {
	synchronized(db) {
	    try {
		db.setAutoCommit(false);

		Util.check(row >= 0);
		Util.check(entries != null);
		delete();

		Map<ActEntry, Integer> fieldIds = new HashMap<ActEntry, Integer>();

		PreparedStatement fieldIdGetter
		    = db.prepareStatement("SELECT id, name FROM fields WHERE file = ?");
		fieldIdGetter.setInt(1, fileId);
		ResultSet set = fieldIdGetter.executeQuery();
		try {
		    while(set.next()) {
			Integer fieldId = set.getInt(1);
			String fieldName = set.getString(2);
			for (ActEntry entry : entries.values()) {
			    if ( entry.getField().getName().equals(fieldName) ) {
				fieldIds.put(entry, fieldId);
				break;
			    }
			}
		    }
		} finally {
		    set.close();
		}

		PreparedStatement insert
		    = db.prepareStatement("INSERT INTO entries (field, row, value) VALUES (?, ?, ?)");

		for (ActEntry entry : entries.values()) {
		    insert.setInt(1, fieldIds.get(entry));
		    insert.setInt(2, row);
		    insert.setString(3, entry.getValue());
		    insert.execute();
		}

		db.commit();
	    } catch (SQLException e) {
		throw new RuntimeException("SQLException", e);
	    } finally {
		try {
		    db.setAutoCommit(true);
		} catch(SQLException e) {
		    throw new RuntimeException("SQLExeption", e);
		}
	    }
	}
    }

    protected void delete() {
	synchronized(db) {
	    try {
		Util.check(row >= 0);

		Vector<Integer> fieldIds = new Vector<Integer>();

		PreparedStatement fieldIdGetter
		    = db.prepareStatement("SELECT id FROM fields WHERE file = ?");
		fieldIdGetter.setInt(1, fileId);
		ResultSet set = fieldIdGetter.executeQuery();
		try {
		    while(set.next()) {
			Integer fieldId = set.getInt(1);
			fieldIds.add(fieldId);
		    }
		} finally {
		    set.close();
		}

		for ( Integer fieldId : fieldIds ) {
		    PreparedStatement delete
			= db.prepareStatement("DELETE FROM entries"
					      + " WHERE field = ? AND row = ?");
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
