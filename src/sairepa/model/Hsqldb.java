package sairepa.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Hsqldb {
  public static final Object dbLock = new Object();
  private Connection connection = null;

  public Hsqldb() throws SQLException {
    try {
      Class.forName("org.hsqldb.jdbcDriver");
    } catch (ClassNotFoundException e) {
      System.err.println("ClassNotFoundException: " + e.toString());
      e.printStackTrace(System.err);
      throw new SQLException(
           "Cannot init database (ClassNotFoundException)");
    }
  }

  public void connect() throws java.sql.SQLException {
    synchronized(dbLock) {
      if (connection != null) {
	disconnect();
      }

      connection = DriverManager.getConnection("jdbc:hsqldb:file:sairepa.db;shutdown=true",
					       "sa", "");
      synchronized(connection) {
	executeQuery("SET LOGSIZE 50;");
	executeQuery("SET CHECKPOINT DEFRAG 50;");
	executeQuery("SET PROPERTY \"hsqldb.nio_data_file\" FALSE");
      }
    }
  }

  public void disconnect() throws java.sql.SQLException {
    synchronized(dbLock) {
      synchronized (connection) {
	connection.commit();
	executeQuery("SHUTDOWN");
	connection.close();
      }
    }
  }

  /**
   * don't synchronize -&gt; do it yourself
   */
  private void executeQuery(final String query) throws java.sql.SQLException {
    final Statement stmt = connection.createStatement();
    stmt.execute(query);
    stmt.close();
  }

  public Connection getConnection() {
    return connection;
  }
}
