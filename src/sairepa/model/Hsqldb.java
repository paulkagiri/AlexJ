package sairepa.model;

import java.io.File;
import java.io.IOException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Hsqldb {
  public static final Object dbLock = new Object();
  private Connection connection = null;
  private String project;

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

  public void connect(String projectName) throws java.sql.SQLException {
    synchronized(dbLock) {
      if (connection != null) {
	disconnect();
      }

      project = projectName;
      lockProject(project); // throw a runtime exception if can't

      connection = DriverManager.getConnection(
          "jdbc:hsqldb:file:sairepa_" + projectName + ".db;shutdown=true", "sa", "");
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
	unlockProject(project);
      }
    }
  }

  private static void lockProject(String project) {
    File f = new File(project + ".lock");
    boolean b;

    try {
      b = f.createNewFile();
    } catch (IOException e) {
      throw new RuntimeException("Erreur lors de la verification du verrou du projet '" + project + "'.", e);
    }

    if (!b) {
      throw new RuntimeException("Ce projet semble etre deja utilise par une autre instance de Sairepa. " +
				 "Si ce n'est pas le cas, veuillez effacer le fichier '" + project + ".lock' " +
				 "du repertoire Sairepa");
    } else {
      f.deleteOnExit();
    }
  }

  private static void unlockProject(String project) {
    File f = new File(project + ".lock");
    f.delete();
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
