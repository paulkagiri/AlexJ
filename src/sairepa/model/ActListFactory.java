package sairepa.model;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

import org.xBaseJ.micro.DBF;
import org.xBaseJ.micro.xBaseJException;
import org.xBaseJ.micro.fields.Field;

public abstract class ActListFactory
{
  private ActField[] fields;
  private ActList actList = null;

  private File dbf;
  private int fileId; // db specific

  private Connection db;

  public ActListFactory(Connection db, File dbf,
			ActField[] fields) {
    this.fields = fields;
    this.db = db;
    this.dbf = dbf;
  }

  public void init() throws SQLException, IOException {
    synchronized(db) {
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

	purgeFieldsAndEntries();
	reloadDbf();
	updateDbfSyncTimestamp();

      }
    }
  }

  public void save() throws SQLException, IOException {
    synchronized(db) {
      if (mustRewriteDbf()) {
	rewriteDbf();
	updateDbfSyncTimestamp();
      }
    }
  }

  /**
   * @return -1 if must read, +1 if must rewrite
   */
  private int mustSyncDbf() throws SQLException {
    PreparedStatement st = db.prepareStatement(
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
    PreparedStatement st = db.prepareStatement(
        "SELECT id FROM fields WHERE file = ?");
    st.setInt(1, fileId);

    ResultSet set = st.executeQuery();
    while(set.next()) {
      st = db.prepareStatement("DELETE FROM entries WHERE field = ?");
      st.setInt(1, set.getInt(1));
      st.executeUpdate();

      st = db.prepareStatement("DELETE FROM fields WHERE id = ?");
      st.setInt(1, set.getInt(1));
      st.executeUpdate();
    }
    set.close();
  }

  private int getFileId() throws SQLException {
    PreparedStatement st = db.prepareStatement(
        "SELECT id FROM files WHERE LOWER(file) = ? LIMIT 1");
    st.setString(1, dbf.getPath().toLowerCase());
    ResultSet set = st.executeQuery();
    int id;

    id = ((set.next()) ? set.getInt(1) : -1);

    set.close();
    return id;
  }

  private void createFileEntry() throws SQLException {
    PreparedStatement st = db.prepareStatement(
        "INSERT INTO files (file, lastDbfSync) VALUES (?, ?)");
    st.setString(1, dbf.getPath());
    st.setNull(2, Types.TIMESTAMP);
    st.execute();
  }

  private void updateFieldTable() throws SQLException {
    PreparedStatement st;

    st = db.prepareStatement("DELETE FROM fields WHERE file = ?");
    st.setInt(1, fileId);
    st.execute();

    for (ActField field : fields) {
      createField(field.getName());
    }
  }

  private int getFieldId(String name) throws SQLException {
    PreparedStatement st =
        db.prepareStatement("SELECT id FROM fields WHERE file = ? AND name = ? LIMIT 1");
    st.setInt(1, fileId);
    st.setString(2, name);

    ResultSet set = st.executeQuery();

    int fieldId = (set.next()) ? set.getInt(1) : -1;

    set.close();

    return fieldId;
  }

  private void createField(String name) throws SQLException {
    PreparedStatement st =
        db.prepareStatement("INSERT INTO fields (file, name) VALUES (?, ?)");
    st.setInt(1, fileId);
    st.setString(2, name);
    st.execute();
  }

  private int getOrCreateFieldId(String name) throws SQLException {
    int fieldId;

    if ( (fieldId = getFieldId(name)) < 0) {
      System.out.println("WARNING: Field '" + name + "' from file '" +
			 dbf.getPath() + "' didn't exist");
      createField(name);
      Util.check( (fieldId = getFieldId(name)) >= 0);
    }

    return fieldId;
  }

  private void insertEntry(int fieldId, int row, String value)
      throws SQLException {
    PreparedStatement st =
        db.prepareStatement("INSERT INTO entries (field, row, value) " +
			    "VALUES (?, ?, ?)");
    st.setInt(1, fieldId);
    st.setInt(2, row);
    st.setString(3, value);
    st.execute();
  }

  private void reloadDbf() throws SQLException, IOException {
    Util.check(dbf.exists());

    updateFieldTable();

    DBF dbfFile;

    try {
      dbfFile = new DBF(dbf.getPath(), DBF.READ_ONLY);
    } catch (xBaseJException e) {
      throw new IOException(e);
    }

    int row = 0;

    try {
      while(true) {
	for (int i = 1 ; i <= dbfFile.getFieldCount() ; i++) {
	  Field field = dbfFile.getField(i);
	  Util.check(field.get() != null);
	  int fieldId = getOrCreateFieldId(field.getName());
	  insertEntry(fieldId, row, field.get());
	}
	dbfFile.read();
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

    PreparedStatement st = db.prepareStatement(
        "UPDATE files SET lastDbfSync = ? WHERE id = ?");
    st.setTimestamp(1, new Timestamp(dbf.lastModified()));
    st.setInt(2, fileId);
    //st.execute();
  }

  private void rewriteDbf() {
    // TODO
  }

  public ActList getList() throws IOException {
    // TODO
    return null;
  }
}
