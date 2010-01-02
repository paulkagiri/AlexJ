package sairepa.model;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import net.kwain.fxie.*;

/**
 * Do the work about importing/exporting DBF files into Hsqldb
 */
public abstract class ActListFactory
{
    private FieldLayout fields;
    private ActList actList;
    private Model model;

    private File dbf;
    private File dbt;
    private int fileId; // db specific

    private Hsqldb db;

    public ActListFactory(Model m, File dbf, File dbt, FieldLayout fields) {
	this.fields = fields;
	this.dbf = dbf;
	this.dbt = dbt;
	this.model = m;
    }

    public Model getModel() {
	return model;
    }

    public File getDbf() {
	return dbf;
    }

    public File getDbt() {
	return dbt;
    }

    public void init(Hsqldb db) throws SQLException, IOException {
	this.db = db;

	synchronized(db.getConnection()) {
	    if ((fileId = getFileId()) < 0) {
		createFileEntry();
		Util.check((fileId = getFileId()) >= 0);
	    }

	    if (!dbf.exists()) {

		if (mustRereadDbf()) {
		    // nothing in the db, nothing in the dbf
		    // => a brand new database
		    purgeFieldsAndEntries();
		    updateFieldTable();
		}

		// else:
		// dbf not existing, but something in the db
		// ==> nothing to do

	    } else if (/* (dbf exists) && */ mustRereadDbf()) {

		System.out.println("Loading '" + dbf.getPath() + "' ...");
		purgeFieldsAndEntries();
		rereadDbf();
		updateDbfSyncTimestamp();

	    }
	}

	actList = new DbActList(this, db, fileId, fields, toString());
    }

    public void save() throws SQLException, IOException {
	synchronized(db.getConnection()) {
	    if (!dbf.exists() || mustRewriteDbf()) {
		System.out.println("Writing '" + dbf.getPath() + "' ...");
		if(rewriteDbf()) {
		    updateDbfSyncTimestamp();
		}
	    } else {
		System.out.println("DBF file '" + dbf.getPath() + "' already up-to-date");
	    }
	}
    }

    /**
     * @return -1 if must read, +1 if must rewrite
     */
    private int mustSyncDbf() throws SQLException {
	PreparedStatement st
	    = db.getConnection().prepareStatement("SELECT lastDbfSync FROM files WHERE LOWER(file) = ? LIMIT 1");
	st.setString(1, dbf.getPath().toLowerCase());
	ResultSet set = st.executeQuery();

	if (!set.next()) {
	    // we never wrote this file and know nothing about it
	    set.close();
	    return -1;
	}

	Timestamp lastSync = set.getTimestamp(1);

	set.close();

	if (lastSync == null) {
	    // we know this file, but we never sync with it
	    // time to do it
	    return -1;
	}

	if (lastSync.getTime() < dbf.lastModified()) {
	    return -1;
	} else {
	    return 1;
	}
    }

    private boolean mustRereadDbf() throws SQLException {
	return (mustSyncDbf() < 0);
    }

    private boolean mustRewriteDbf() throws SQLException {
	return (mustSyncDbf() > 0);
    }

    private void purgeFieldsAndEntries() throws SQLException {
	PreparedStatement st
	    = db.getConnection().prepareStatement("SELECT id FROM fields WHERE file = ?");
	st.setInt(1, fileId);

	ResultSet set = st.executeQuery();
	while(set.next()) {
	    st = db.getConnection().prepareStatement("DELETE FROM entries WHERE field = ?");
	    st.setInt(1, set.getInt(1));
	    st.executeUpdate();

	    st = db.getConnection().prepareStatement("DELETE FROM fields WHERE id = ?");
	    st.setInt(1, set.getInt(1));
	    st.executeUpdate();
	}
	set.close();
    }

    private int getFileId() throws SQLException {
	PreparedStatement st
	    = db.getConnection().prepareStatement("SELECT id FROM files WHERE LOWER(file) = ? LIMIT 1");
	st.setString(1, dbf.getPath().toLowerCase());
	ResultSet set = st.executeQuery();
	int id;

	id = ((set.next()) ? set.getInt(1) : -1);

	set.close();
	return id;
    }

    private void createFileEntry() throws SQLException {
	PreparedStatement st
	    = db.getConnection().prepareStatement("INSERT INTO files (file, lastDbfSync) VALUES (?, ?)");
	st.setString(1, dbf.getPath());
	st.setNull(2, Types.TIMESTAMP);
	st.execute();
    }

    private void updateFieldTable() throws SQLException {
	PreparedStatement st;

	st = db.getConnection().prepareStatement("DELETE FROM fields WHERE file = ?");
	st.setInt(1, fileId);
	st.execute();

	for (ActField field : fields) {
	    createField(field.getName());
	}
    }

    private Map<String, Integer> _fieldNameToId = null;
    private void _loadFileId() {
	try {
	    _fieldNameToId = new HashMap<String, Integer>();
	    PreparedStatement st =
		db.getConnection().prepareStatement("SELECT id, name FROM fields WHERE file = ?");
	    st.setInt(1, fileId);
	    ResultSet set = st.executeQuery();
	    while(set.next()) {
		int id = set.getInt(1);
		String name = set.getString(2);
		_fieldNameToId.put(name, id);
	    }
	} catch (SQLException e) {
	    throw new RuntimeException("SQLException", e);
	}
    }

    protected int getFieldId(String name) {
	if ( _fieldNameToId == null )
	    _loadFileId();
	return _fieldNameToId.get(name);
    }

    private void createField(String name) throws SQLException {
	try {
	    PreparedStatement st =
		db.getConnection().prepareStatement("INSERT INTO fields (file, name) VALUES (?, ?)");
	    st.setInt(1, fileId);
	    st.setString(2, name);
	    st.execute();
	} catch (SQLException e) {
	    System.err.println("SQLException when inserting field: "
			       + Integer.toString(fileId) + "/'" + name + "'");
	    throw e;
	}
    }

    private void rereadDbf() throws SQLException, IOException {
	java.util.Date start, stop;
	start = new java.util.Date();

	Util.check(dbf.exists());

	updateFieldTable();

	try {
	    XBaseImport dbfImport = new XBaseImport(dbf, dbt);

	    PreparedStatement st =
	    db.getConnection().prepareStatement("INSERT INTO entries (field, row, value) " +
						"VALUES (?, ?, ?)");

	    int row = 0;
	    while(dbfImport.available() > 0) {
		if ( row % 100 == 0 )
		    System.out.println(toString() + ": DBF reading: " + Integer.toString(row)
				       + " / " + Integer.toString(row + dbfImport.available()));

		List<XBaseValue> record = dbfImport.read();
		if ( record == null )
		    break;
		for (XBaseValue xValue : record) {
		    String value;
		    Util.check( (value = xValue.getHumanReadableValue()) != null );
		    String fieldName;
		    Util.check( (fieldName = xValue.getField().getName()) != null );
		    int fieldId = getFieldId(fieldName);
		    Util.check(fieldId != -1);

		    st.setInt(1, fieldId);
		    st.setInt(2, row);
		    st.setString(3, value);
		    st.execute();
		}
		row++;
	    }

	    dbfImport.close();
	} catch(XBaseException e) {
	    throw new RuntimeException("Fichiers DBT/DBF invalides !", e);
	}

	stop = new java.util.Date();
	System.out.println("Took " + Long.toString((stop.getTime() - start.getTime()) / 1000) + " seconds to read '" + toString() + "'");
    }

    private void updateDbfSyncTimestamp() throws SQLException {
	// Use as input value the lastModified() value of the DBF file
	Util.check(dbf.exists());

	PreparedStatement st
	    = db.getConnection().prepareStatement("UPDATE files SET lastDbfSync = ? WHERE id = ?");
	st.setTimestamp(1, new Timestamp(dbf.lastModified()));
	st.setInt(2, fileId);
	st.execute();
    }

    private class XBaseProvider implements XBaseExport.DataProvider {
	private int nmbRow;
	private ResultSet data;
	private Map<String, Integer> dbfFields; /* name -> id */

	public XBaseProvider() throws SQLException, XBaseException {
	    this.nmbRow = actList.getRowCount();

	    PreparedStatement st;

	    st = db.getConnection().prepareStatement("SELECT fields.id, fields.name " +
						     "FROM fields " +
						     "WHERE fields.file = ?");
	    st.setInt(1, fileId);

	    ResultSet set = st.executeQuery();
	    dbfFields = new HashMap<String, Integer>();
	    while(set.next()) {
		int fieldId = set.getInt(1);
		String name = set.getString(2);
		dbfFields.put(name, fieldId);
	    }

	    st = db.getConnection().prepareStatement("SELECT entries.field, entries.row, entries.value " +
						     "FROM fields INNER JOIN entries ON fields.id = entries.field " +
						     "WHERE fields.file = ? ORDER BY entries.row");
	    st.setInt(1, fileId);
	    data = st.executeQuery();
	}

	public boolean hasRow(int row) {
	    if ( row % 100 == 0 )
		System.out.println("Writing: " + Integer.toString(row) + "/" + Integer.toString(nmbRow));
	    return (row < nmbRow);
	}

	private int lastRow = -1;
	private Map<Integer, String> rowValues = null; /* fieldId -> String */

	/* DIRTY: (but avoid a call to data.previous()) */
	private int _previousDataRow = -1;
	private int _previousFieldId = -1;
	private String _previousValue = null;

	public String getValue(int row, XBaseHeader.XBaseField field) {
	    if ( row != lastRow || rowValues == null ) {
		lastRow = row;
		/* reading next results */
		rowValues = new HashMap<Integer, String>();
		try {
		    if ( _previousDataRow == row ) {
			rowValues.put(_previousFieldId, _previousValue);
		    }
		    while(data.next()) {
			int dataRow = data.getInt(2);
			int fieldId = data.getInt(1);
			String value = data.getString(3);
			if ( dataRow != row ) {
			    _previousDataRow = dataRow;
			    _previousFieldId = fieldId;
			    _previousValue = value;
			    break;
			}
			rowValues.put(fieldId, value);
		    }
		} catch(SQLException e) {
		    throw new RuntimeException("SQLException", e);
		}
	    }

	    int fieldId = dbfFields.get(field.getName());
	    String value = rowValues.get(fieldId);

	    Util.check(value != null);

	    if (field.getFieldType() instanceof XBaseFieldType.XBaseFieldTypeMemo
		&& ("".equals(value.trim())
		    || "-".equals(value.trim())))
		value = "";
	    return value;
	}
    }

    private List<XBaseHeader.XBaseField> getDbfFields() throws XBaseException {
	List<XBaseHeader.XBaseField> dbfFields = new Vector<XBaseHeader.XBaseField>();
	for (ActField field : fields) {
	    dbfFields.add(field.createDBFField());
	}
	return dbfFields;
    }

    private boolean rewriteDbf() throws SQLException, IOException {
	java.util.Date start, stop;
	start = new java.util.Date();

	actList.refresh();
	dbf.delete();
	dbt.delete();
	if ( actList.getRowCount() <= 0 ) {
	    System.out.println("Nothing to write in '" + dbf.getPath() + "'");
	    return false;
	}
	try {
	    XBaseProvider provider = new XBaseProvider();
	    XBaseExport export = new XBaseExport(dbf, dbt,
						 XBaseHeader.XBaseVersion.XBASE_VERSION_DBASE_IIIP_MEMO,
						 XBaseHeader.XBaseCharset.CHARSET_DOS_USA,
						 getDbfFields(),
						 provider, ' ');
	    export.write();
	} catch (XBaseException e) {
	    throw new RuntimeException("XBaseException while writing the dbf file: " + e.toString(), e);
	}

	stop = new java.util.Date();
	System.out.println("Took " + Long.toString((stop.getTime() - start.getTime()) / 1000) + " seconds to rewrite '" + toString() + "'");

	return true;
    }

    /**
     * Returns the default act list type, always hitting the DB
     */
    public ActList getActList() {
	return actList;
    }

    public abstract String toString();
}
