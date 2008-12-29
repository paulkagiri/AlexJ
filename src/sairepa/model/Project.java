package sairepa.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;

public class Project
{
  private File projectDir;
  private ClientFile clientFile;

  public Project(File projectDir) throws ClientFile.InvalidClientFileException,
      FileNotFoundException {
    this.projectDir = projectDir;
    this.clientFile = new ClientFile(projectDir,
				     ClientFile.DEFAULT_CLIENT_FILE_NAME);
  }

  public File getProjectDir() {
    return projectDir;
  }

  public ClientFile getClientFile() {
    return clientFile;
  }

  public Model createModel() throws SQLException, FileNotFoundException {
    return new Model(projectDir, clientFile);
  }

  public String toString() {
    return clientFile.getZipCode() + " - " + clientFile.getCommune();
  }
}
