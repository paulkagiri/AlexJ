package sairepa.model;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.ResultSet;

public abstract class ActListFactory
{
  private ActField[] fields;
  private ActList actList = null;
  private File dbf;
  private Connection db;

  public ActListFactory(Connection db, File dbf,
			ActField[] fields) {
    this.fields = fields;
    this.db = db;
    this.dbf = dbf;
  }

  public void init() throws SQLException {
    if (!dbf.exists()) {
      if (!hasFileEntry()) {
	updateFieldTable();
	createFileEntry();
      }
    } else if (mustRereadDbf()) {
      purgeFieldsAndEntries();
      reloadDbf();
      updateDbfSyncTimestamp();
    }
  }

  public void save() throws SQLException {
    if (mustRewriteDbf()) {
      rewriteDbf();
      updateDbfSyncTimestamp();
    }
  }

  /**
   * @return -1 if must read, +1 if must rewrite
   */
  private int mustSyncDbf() throws SQLException {
    synchronized(db) {
      PreparedStatement st = db.prepareStatement(
          "SELECT lastDbfSync FROM files WHERE LOWER(file) = ? LIMIT 1");
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
  }

  private boolean mustRereadDbf() throws SQLException {
    return (mustSyncDbf() < 0);
  }

  private boolean mustRewriteDbf() throws SQLException {
    return (mustSyncDbf() > 0);
  }

  private void purgeFieldsAndEntries() throws SQLException {
    synchronized(db) {
      PreparedStatement st = db.prepareStatement(
          "SELECT fields.id FROM fields INNER JOIN files " +
          "ON fields.file = files.id WHERE files.file = ?");
      st.setString(1, dbf.getPath());

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
  }

  private boolean hasFileEntry() throws SQLException {
    synchronized(db) {
      PreparedStatement st = db.prepareStatement(
          "SELECT id FROM files WHERE file = ? LIMIT 1");
      ResultSet set = st.executeQuery();
      boolean has = set.next();
      set.close();
      return has;
    }
  }

  private void updateFieldTable() {
    // TODO
  }

  private void createFileEntry() {
    // TODO
  }

  private void reloadDbf() {
    // TODO
  }

  private void updateDbfSyncTimestamp() {
    // Use as input value the lastModified() value of the DBF file
  }

  private void rewriteDbf() {
    // TODO
  }

  public ActList getList() throws IOException {
    // TODO
    return null;
  }
}
