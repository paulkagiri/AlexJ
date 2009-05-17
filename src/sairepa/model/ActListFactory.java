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
import java.util.Map;

import org.xBaseJ.DBF;
import org.xBaseJ.xBaseJException;
import org.xBaseJ.fields.Field;
import org.xBaseJ.fields.MemoField;

import sairepa.model.*;

/**
 * Do the work about importing/exporting DBF files into Hsqldb
 */
public abstract class ActListFactory
{
  private FieldLayout fields;
  private ActList actList;
  private Model model;

  private File dbf;
  private int fileId; // db specific

  private Hsqldb db;

  public ActListFactory(Model m, File dbf, FieldLayout fields) {
    this.fields = fields;
    this.dbf = dbf;
    this.model = m;
  }

  public Model getModel() {
    return model;
  }

  public File getDbf() {
    return dbf;
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
    PreparedStatement st = db.getConnection().prepareStatement(
        "SELECT lastDbfSync FROM files WHERE LOWER(file) = ? LIMIT 1");
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
    PreparedStatement st = db.getConnection().prepareStatement(
        "SELECT id FROM fields WHERE file = ?");
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
    PreparedStatement st = db.getConnection().prepareStatement(
        "SELECT id FROM files WHERE LOWER(file) = ? LIMIT 1");
    st.setString(1, dbf.getPath().toLowerCase());
    ResultSet set = st.executeQuery();
    int id;

    id = ((set.next()) ? set.getInt(1) : -1);

    set.close();
    return id;
  }

  private void createFileEntry() throws SQLException {
    PreparedStatement st = db.getConnection().prepareStatement(
        "INSERT INTO files (file, lastDbfSync) VALUES (?, ?)");
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

  protected int getFieldId(String name) throws SQLException {
    PreparedStatement st =
      db.getConnection().prepareStatement("SELECT id FROM fields WHERE file = ? AND name = ? LIMIT 1");
    st.setInt(1, fileId);
    st.setString(2, name);

    ResultSet set = st.executeQuery();

    int fieldId = (set.next()) ? set.getInt(1) : -1;

    set.close();

    return fieldId;
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

  private void insertEntry(int fieldId, int row, String value)
      throws SQLException {
    PreparedStatement st =
      db.getConnection().prepareStatement("INSERT INTO entries (field, row, value) " +
			    "VALUES (?, ?, ?)");
    st.setInt(1, fieldId);
    st.setInt(2, row);
    st.setString(3, value);
    st.execute();
  }

  private void rereadDbf() throws SQLException, IOException {
    Util.check(dbf.exists());

    updateFieldTable();

    DBF dbfFile;

    try {
      dbfFile = new DBF(dbf.getPath(), DBF.READ_ONLY, "CP850");
    } catch (xBaseJException e) {
	throw new IOException("xBaseJException while opening the DBF file: " + e.toString());
    }

    int row = 0;

    try {
      while(true) {
	dbfFile.read();
	for (int i = 1 ; i <= dbfFile.getFieldCount() ; i++) {
	  Field field = dbfFile.getField(i);

	  String value;
	  Util.check((value = field.get()) != null);

	  int fieldId = getFieldId(field.getName());
	  Util.check(fieldId != -1);
	  insertEntry(fieldId, row, Util.trim(value));
	}
	row++;
      }
    } catch (xBaseJException e) {
      // most ugly way ever to signal EOF
    }

    dbfFile.close();
  }

  private void updateDbfSyncTimestamp() throws SQLException {
    // Use as input value the lastModified() value of the DBF file
    Util.check(dbf.exists());

    PreparedStatement st = db.getConnection().prepareStatement(
        "UPDATE files SET lastDbfSync = ? WHERE id = ?");
    st.setTimestamp(1, new Timestamp(dbf.lastModified()));
    st.setInt(2, fileId);
    st.execute();
  }

  private boolean rewriteDbf() throws SQLException, IOException {
    try {
      actList.refresh();
      if (actList.getRowCount() <= 0) {
	System.out.println("NOTICE: '" + dbf.getPath() + "' is empty ; not rewritten");
	dbf.delete();
	return false;
      }

      Map<String, Field> dbfFields = new HashMap<String, Field>();

      for (ActField field : fields) {
	dbfFields.put(field.getName(), field.createDBFField());
      }

      DBF dbfFile = new DBF(dbf.getPath(), (int)DBF.DBASEIII_WITH_MEMO, true, "CP850");

      try {
	for (Field field : dbfFields.values()) {
	  dbfFile.addField(field);
	}

	PreparedStatement st = db.getConnection().prepareStatement(
	    "SELECT fields.name, entries.row, entries.value " +
	    "FROM fields INNER JOIN entries ON fields.id = entries.field " +
	    "WHERE fields.file = ? ORDER BY entries.row");
	st.setInt(1, fileId);

	ResultSet set = st.executeQuery();

	try {
	  int currentRow = 0;

	  while(set.next()) {
	    String fieldName = set.getString(1);
	    int row = set.getInt(2);
	    String value = set.getString(3);

	    Field field = dbfFields.get(fieldName);
	    Util.check(field != null);

	    if (row != currentRow) {
	      dbfFile.write();
	      currentRow = row;
	    }

	    if ((!(field instanceof MemoField)) && value.length() > field.Length) {
	      System.err.println("VALUE TOO LONG : "+ field.getClass().getName() + " : " +
				 field.getName() + " : " +
				 Integer.toString(field.Length) +" : '"+ value+"' : " +
				 Integer.toString(value.length()));
	    }

	    if (field instanceof MemoField
		&& ("".equals(value.trim())
		  || "-".equals(value.trim())))
	      {
		value = "";
	      }

	    if (!(field instanceof MemoField)) {
	      int lng = ActField.getMaxLength(field);
	      value = ActField.pad(value, ' ', lng);
	    }

	    field.put(value);
	  }
	} finally {
	  set.close();
	}

	dbfFile.write();
      } finally {
	dbfFile.close();
      }
    } catch (xBaseJException e) {
	throw new RuntimeException("xBaseJException while writing the dbf file: " + e.toString());
    }

    return true;
  }

  /**
   * Returns the default act list type, always hitting the DB
   */
  public ActList getActList() {
    return actList;
  }

  public ActList getInMemoryActList() {
    try {
      return new InMemoryActList(actList);
    } catch (OutOfMemoryError e) {
      System.err.println("OutOfMemoryError ; Falling back on direct DB access");
      return actList;
    }
  }

  public abstract String toString();
}
