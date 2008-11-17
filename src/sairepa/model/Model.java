package sairepa.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Vector;

public class Model
{
  private File projectDir;
  private Hsqldb db;

  private ActListFactory[] factories;

  protected Model(File projectDir) throws SQLException, FileNotFoundException {
    this.projectDir = projectDir;

    db = new Hsqldb();
    db.connect(projectDir.getName());

    factories = new ActListFactory[] {
      new BaptismListFactory(projectDir),
      new ConfirmationListFactory(projectDir),
      new WeddingListFactory(projectDir),
      new SepulchreListFactory(projectDir)
    };
  }

  public ActListFactory[] getFactories() {
    return factories;
  }

  public static Vector<Project> locateProjects(File baseDir) {
    Vector<Project> projects = new Vector<Project>();

    for (File file : baseDir.listFiles(new Util.ProjectFileFilter())) {
      try {
	Project p = new Project(file);
	projects.add(p);
      } catch (ClientFile.InvalidClientFileException e) {
	System.err.println("WARNING - Invalid project: " + file.getPath());
	System.err.println("Reason: " + e.toString());
	e.printStackTrace(System.err);
      } catch (FileNotFoundException e) {
	System.err.println("WARNING - Invalid project: " + file.getPath());
	System.err.println("Reason: " + e.toString());
	e.printStackTrace(System.err);
      }
    }

    return projects;
  }

  public void init() throws SQLException, IOException {
    createTables();

    for (ActListFactory factory : factories) {
      factory.init(db.getConnection());
    }
  }

  public void save() throws SQLException, IOException {
    for (ActListFactory factory : factories) {
      factory.save();
    }
  }

  public void close() throws SQLException {
    db.disconnect();
  }

  private void createTables() throws SQLException {
    try {
      executeQuery("CREATE CACHED TABLE files ("
		   + "id INTEGER NOT NULL IDENTITY, "
		   + "file VARCHAR NOT NULL, " // "dir/file.dbf"
		   + "lastDbfSync TIMESTAMP NULL, "
		   + "PRIMARY KEY (id), UNIQUE (file)"
		   + ");");
      executeQuery("CREATE CACHED TABLE fields ("
		   + "id INTEGER NOT NULL IDENTITY, "
		   + "file INTEGER NOT NULL, "
		   + "name VARCHAR NOT NULL, "
		   + "PRIMARY KEY (id), "
		   + "UNIQUE (file, name), "
		   + "FOREIGN KEY (file) REFERENCES files (id)"
		   + ");");
      executeQuery("CREATE CACHED TABLE entries ("
		   + "id INTEGER NOT NULL IDENTITY, "
		   + "field INTEGER NOT NULL, "
		   + "row INTEGER NOT NULL, "
		   + "value VARCHAR NOT NULL, "
		   + "PRIMARY KEY (id), "
		   + "UNIQUE (field, row), "
		   + "FOREIGN KEY (field) REFERENCES fields (id)"
		   + ");");
    } catch(SQLException e) {
      // Probably a "table already exists", no worry on
      // this point.
      System.out.println("SQLException : " + e.toString());
    }
  }

  private void executeQuery(final String query) throws java.sql.SQLException {
    final Statement stmt = db.getConnection().createStatement();
    stmt.execute(query);
    stmt.close();
  }
}
