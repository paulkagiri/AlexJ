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
    private ActListFactory factory;
    private Hsqldb db;
    private int fileId;
    private FieldLayout fields;
    private int rowCount;
    private String name;

    protected DbActList(ActListFactory factory, Hsqldb db,
			int fileId, FieldLayout fields, String name)
	throws SQLException, IOException {
	this.db = db;
	this.fileId = fileId;
	this.fields = fields;
	this.rowCount = computeRowCount();
	this.name = name;
	this.factory = factory;
    }

    public ActListFactory getFactory() {
	return factory;
    }

    public String getName() {
	return name;
    }

    public FieldLayout getFields() {
	return fields;
    }

    public int computeRowCount() throws SQLException {
	synchronized(db.getConnection()) {
	    PreparedStatement st
		= db.getConnection().prepareStatement("SELECT entries.row " +
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

    // often, a same row is asked many times in a row
    private int lastRowReturned = -1;
    private Act lastActReturned = null;

    public Act getAct(int row) {
	synchronized(db.getConnection()) {
	    if (row == lastRowReturned && lastActReturned != null) {
		return lastActReturned;
	    }

	    try {
		Act act = new Act(db.getConnection(), this, fileId, fields, row);
		this.lastActReturned = act;
		this.lastRowReturned = row;
		return act;
	    } catch(SQLException e) {
		throw new RuntimeException("SQLException", e);
	    }
	}
    }

    public List<Act> getAllActs() {
	java.util.Date start, stop;
	start = new java.util.Date();

	Vector<Act> acts = new Vector<Act>(getRowCount());
	Map<Integer, ActField> idToFields = new HashMap<Integer, ActField>();

	synchronized(db.getConnection()) {
	    try {
		PreparedStatement st;
		ResultSet set;

		st = db.getConnection().prepareStatement("SELECT fields.id, fields.name FROM fields WHERE fields.file = ?");
		st.setInt(1, fileId);
		set = st.executeQuery();
		try {
		    while(set.next()) {
			Integer i = set.getInt(1);
			String name = set.getString(2);
			ActField f = fields.getField(name);
			Util.check(f != null);
			idToFields.put(i, f);
		    }
		} finally {
		    set.close();
		}

		st = db.getConnection().prepareStatement("SELECT fields.id, entries.row, entries.value"
							 + " FROM fields INNER JOIN entries ON fields.id = entries.field"
							 + " WHERE fields.file = ? ORDER by entries.row");
		st.setInt(1, fileId);
		set = st.executeQuery();
		try {
		    Map<ActField, String> entries = null;
		    int currentRow = -1;

		    while(set.next()) {
			ActField field = idToFields.get(set.getInt(1));
			int row = set.getInt(2);
			String value = set.getString(3);

			if ( row != currentRow ) {
			    if ( entries != null ) {
				acts.add(new Act(db.getConnection(), this, fileId, fields, entries));
			    }
			    entries = new HashMap<ActField, String>();
			    currentRow = row;
			}
			assert(!entries.containsKey(field));
			entries.put(field, value);
		    }

		    if ( entries != null ) {
			acts.add(new Act(db.getConnection(), this, fileId, fields, entries));
		    }
		} finally {
		    set.close();
		}
	    } catch (SQLException e) {
		throw new RuntimeException("SQLException", e);
	    }
	}

	stop = new java.util.Date();
	System.out.println("Took " + Long.toString((stop.getTime() - start.getTime()) / 1000) +
			   " seconds to load " + Integer.toString(getRowCount()) + " elements");
	return acts;
    }

    public void insert(Act act) {
	insert(act, rowCount);
    }

    public void insert(Act act, int row) {
	synchronized(db.getConnection()) {
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
	synchronized(db.getConnection()) {
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
	return new GenericActListIterator(db.getConnection(), this);
    }

    private void shiftAfter(int position, int shift) throws SQLException {
	PreparedStatement selectFields
	    = db.getConnection().prepareStatement("SELECT fields.id FROM fields WHERE fields.file = ?");
	selectFields.setInt(1, fileId);
	ResultSet set = selectFields.executeQuery();

	PreparedStatement update
	    = db.getConnection().prepareStatement("UPDATE entries SET row = row + ? " +
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
	return new Act(db.getConnection(), this, fileId, fields);
    }

    protected int getFileId() {
	return fileId;
    }

    public void refresh() {
	synchronized(db.getConnection()) {
	    lastRowReturned = -1;
	    lastActReturned = null;
	    try {
		computeRowCount();
	    } catch (SQLException e) {
		throw new RuntimeException("SQLException", e);
	    }
	}
    }

    protected class SortedActList implements ActList {
	private boolean desc;
	private int sortingFieldId = -1;

	public SortedActList(String sortedBy, boolean desc) {
	    this.desc = desc;

	    synchronized(db.getConnection()) {
		if (sortedBy != null) {
		    try {
			PreparedStatement fieldGetter
			    = db.getConnection().prepareStatement("SELECT fields.id FROM fields WHERE fields.name = ? AND fields.file = ? LIMIT 1");
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

	public ActListFactory getFactory() {
	    return DbActList.this.getFactory();
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

	private int lastPositionReturned = -1;
	private Act lastActReturned = null;

	public Act getAct(int position) {
	    synchronized(db.getConnection()) {
		if (position == lastPositionReturned && lastActReturned != null) {
		    return lastActReturned;
		}

		int row = -1;

		if (sortingFieldId >= 0) {
		    try {
			PreparedStatement rowGetter
			    = db.getConnection().prepareStatement("SELECT row FROM entries WHERE field = ? ORDER BY LOWER(value)"
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

		Act a = DbActList.this.getAct(row);
		lastPositionReturned = position;
		lastActReturned = a;
		return a;
	    }
	}

	public List<Act> getAllActs()
	{
	    Vector<Act> acts;
	    synchronized(db.getConnection()) {
		try {
		    PreparedStatement rowGetter
			= db.getConnection().prepareStatement("SELECT row from entries WHERE field = ? ORDER BY LOWER(value)"
							      + (desc ? " DESC" : ""));
		    rowGetter.setInt(1, sortingFieldId);
		    ResultSet set = rowGetter.executeQuery();
		    acts = new Vector<Act>();
		    while(set.next()) {
			int row = set.getInt(1);
			Act a = DbActList.this.getAct(row);
			acts.add(a);
		    }
		} catch (SQLException e) {
		    throw new RuntimeException("SQLException", e);
		}
	    }
	    return acts;
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
	    return new GenericActListIterator(db.getConnection(), SortedActList.this);
	}

	public Act createAct() {
	    return DbActList.this.createAct();
	}

	public void refresh() {
	    synchronized(db.getConnection()) {
		lastActReturned = null;
		lastPositionReturned = -1;
		DbActList.this.refresh();
	    }
	}
    }

    public ActList getSortedActList(String sortedBy, boolean desc) {
	return new SortedActList(sortedBy, desc);
    }
}
