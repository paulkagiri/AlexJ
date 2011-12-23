package sairepa.model;

import java.io.File;
import java.io.IOException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DbHandler {
  public static final Object dbLock = new Object();
  private Connection connection = null;
  private String project;

  public DbHandler() throws SQLException {
    try {
      Class.forName("org.sqlite.JDBC");
    } catch (ClassNotFoundException e) {
      System.err.println("ClassNotFoundException: " + e.toString());
      e.printStackTrace(System.err);
      throw new SQLException(
	  "Cannot init database (ClassNotFoundException)");
    }
  }

  private String getDbFilename() {
    return sairepa.Main.APPLICATION_NAME + "_" + this.project + ".db";
  }

  public void connect(String projectName) throws java.sql.SQLException {
    synchronized(dbLock) {
      if (connection != null) {
	disconnect();
      }

      project = projectName;
      lockProject(project); // throw a runtime exception if can't

      connection = DriverManager.getConnection("jdbc:sqlite:" + getDbFilename());
      connection.setAutoCommit(true);
    }
  }

  public void delete() {
	new File(getDbFilename()).delete();
  }

  public void disconnect() throws java.sql.SQLException {
    if (connection == null)
      return;

    synchronized(dbLock) {
      synchronized (connection) {
	connection.close();
	connection = null;
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
      /* TODO(Jflesch): l10n */
      throw new RuntimeException("Erreur lors de la verification du verrou du projet '" + project + "'.", e);
    }

    if (!b) {
      /* TODO(Jflesch): l10n */
      throw new RuntimeException("Ce projet semble etre deja utilise par une autre instance de " + sairepa.Main.APPLICATION_NAME + ". " +
	  "Si ce n'est pas le cas, veuillez effacer le fichier '" + project + ".lock' " +
	  "du repertoire " + sairepa.Main.APPLICATION_NAME);
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
