package sairepa.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Vector;

import sairepa.model.structs.*;

public class Model
{
  private File projectDir;
  private Hsqldb db;
  private ClientFile clientFile;
  private ActListFactoryLayout factories;

  // dirty singleton
  private static final PrncvDb prncvDb = new PrncvDb();

  protected Model(File projectDir, ClientFile clientFile) throws SQLException, FileNotFoundException {
    this.projectDir = projectDir;
    this.clientFile = clientFile;

    db = new Hsqldb();
    db.connect(projectDir.getName());

    factories = new ActListFactoryLayout(this,
	new String[] {
	  "Pr\351-r\351volution",
	  "Etat-civil",
	  "Actes notari\351s",
	},
	new ActListFactory[][] {
	  new ActListFactory[] {
	    new BaptismListFactory(this, projectDir),
	    new WeddingListFactory(this, projectDir),
	    new SepulchreListFactory(this, projectDir),
	    new ConfirmationListFactory(this, projectDir),
	  },
	  new ActListFactory[] {
	    new BirthListFactory(this, projectDir),
	    new UnionListFactory(this, projectDir),
	    new DeceaseListFactory(this, projectDir),
	  },
	  new ActListFactory[] {
	    new WeddingContractListFactory(this, projectDir),
	    new NotarialDeceaseListFactory(this, projectDir),
	  },
	});
  }

  public ClientFile getClientFile() {
    return clientFile;
  }

  public static PrncvDb getPrncvDb() {
    return prncvDb;
  }

  public ActListFactoryLayout getFactories() {
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

  /**
   * @param obs can't be null ; pass a dumb object if you need to
   */
  public void init(ProgressionObserver obs) throws SQLException, IOException {
    obs.setProgression(0, "Initialisation de la base de donn\351es ...");
    createTables();

    int nmb = factories.getNumberOfFactories() + 1;
    int i = 1;

    for (ActListFactory factory : factories) {
      obs.setProgression(i * 100 / nmb,
          "Chargement de '" + factory.getDbf().getName() + "' ...");
      factory.init(db.getConnection());
      i++;
    }
  }

  /**
   * @param obs can't be null ; pass a dumb object if you need to
   */
  public void save(ProgressionObserver obs) throws SQLException, IOException {
    int nmb = factories.getNumberOfFactories() + 1;
    int i = 0;

    for (ActListFactory factory : factories) {
      obs.setProgression(i * 98 / nmb,
          "Ecriture de '" + factory.getDbf().getName() + "' ...");
      factory.save();
      i++;
    }
  }

  /**
   * @param obs can't be null ; pass a dumb object if you need to
   */
  public void close(ProgressionObserver obs) throws SQLException {
    obs.setProgression(99, "Fermeture de la base de donn\351es");
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
