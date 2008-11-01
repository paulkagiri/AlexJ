package sairepa.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.sql.SQLException;
import java.sql.Statement;

public class Model
{
  private File projectDir;
  private Hsqldb db;

  private ActListFactory[] factories;

  public Model(File projectDir) {
    this.projectDir = projectDir;
  }

  public void init() throws SQLException, FileNotFoundException, IOException {
    db = new Hsqldb();
    db.connect();

    createTables();

    factories = new ActListFactory[] {
      new BaptismListFactory(db.getConnection(), projectDir),
      new WeddingListFactory(db.getConnection(), projectDir),
      new SepulchreListFactory(db.getConnection(), projectDir)
    };

    for (ActListFactory factory : factories) {
      factory.init();
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
