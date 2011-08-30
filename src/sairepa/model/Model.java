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
  private final File projectDir;
  private final DbHandler db;
  private final ClientFile clientFile;
  private final ActListFactoryLayout factories;
  private final BackupManager backupManager;

  // dirty singleton
  private static final PrncvDb prncvDb = new PrncvDb();

  protected Model(File projectDir, ClientFile clientFile) throws SQLException, FileNotFoundException {
    this.projectDir = projectDir;
    this.clientFile = clientFile;

    db = new DbHandler();

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

    backupManager = new BackupManager(projectDir);
  }

  public File getProjectDir() {
    return projectDir;
  }

  public ClientFile getClientFile() {
    return clientFile;
  }

  public static PrncvDb getPrncvDb() {
    return prncvDb;
  }

  public DbHandler getDb() {
    return db;
  }

  public ActListFactoryLayout getFactories() {
    return factories;
  }

  public BackupManager getBackupManager() {
    return backupManager;
  }

  /**
   * @param obs can't be null ; pass a dumb object if you need to
   */
  public void init(ProgressionObserver obs) throws SQLException, IOException {
    obs.setProgression(0, "Initialisation de la base de donn\351es ...");
    db.connect(projectDir.getName());
    createTables();

    int nmb = factories.getNumberOfFactories() + 1;
    int i = 1;

    db.getConnection().setAutoCommit(false);
    for (ActListFactory factory : factories) {
      obs.setProgression(i * 90 / nmb,
          "Chargement de '" + factory.getDbf().getName() + "' ...");
      factory.init(db);
      i++;
    }
    db.getConnection().commit();
    db.getConnection().setAutoCommit(true);

    obs.setProgression(95, "Recherche de sauvegardes ...");
    backupManager.init();
  }

  /**
   * @param obs can't be null ; pass a dumb object if you need to
   */
  public void save(ProgressionObserver obs) throws SQLException, IOException {
    int nmb = factories.getNumberOfFactories() + 1;
    int i = 0;

    for (ActListFactory factory : factories) {
      obs.setProgression(i * 90 / nmb,
          "Ecriture de '" + factory.getDbf().getName() + "' ...");
      factory.save();
      i++;
    }

    obs.setProgression(95, "Sauvegarde ...");
    backupManager.doBackup();
  }

  /**
   * @param obs can't be null ; pass a dumb object if you need to
   */
  public void close(ProgressionObserver obs) throws SQLException {
    obs.setProgression(99, "Fermeture de la base de donn\351es ...");
    db.disconnect();
  }

  private void createTables() throws SQLException {
    executeQuery("CREATE TABLE IF NOT EXISTS files ("
	+ "id INTEGER NOT NULL PRIMARY KEY ASC AUTOINCREMENT, "
	+ "file VARCHAR NOT NULL, " // "dir/file.dbf"
	+ "lastDbfSync TIMESTAMP NULL, "
	+ "UNIQUE (file)"
	+ ");");
    executeQuery("CREATE TABLE IF NOT EXISTS fields ("
	+ "id INTEGER NOT NULL PRIMARY KEY ASC AUTOINCREMENT, "
	+ "file INTEGER NOT NULL, "
	+ "name VARCHAR NOT NULL, "
	+ "UNIQUE (file, name), "
	+ "FOREIGN KEY (file) REFERENCES files (id)"
	+ ");");
    executeQuery("CREATE TABLE IF NOT EXISTS entries ("
	+ "id INTEGER NOT NULL PRIMARY KEY ASC AUTOINCREMENT, "
	+ "field INTEGER NOT NULL, "
	+ "row INTEGER NOT NULL, "
	+ "value VARCHAR NOT NULL, "
	+ "UNIQUE (field, row), "
	+ "FOREIGN KEY (field) REFERENCES fields (id)"
	+ ");");
  }

  private void executeQuery(final String query) throws java.sql.SQLException {
    final Statement stmt = db.getConnection().createStatement();
    stmt.execute(query);
    stmt.close();
  }
}
