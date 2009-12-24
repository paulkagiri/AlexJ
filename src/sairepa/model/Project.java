package sairepa.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.Vector;

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

    public static void initProjectDirs(File baseDir)
    {
	for (File file : baseDir.listFiles(new Util.ClientFileFilter())) {
	    String dirName = file.getName().toLowerCase().replaceFirst("cli_", "").replaceAll(".dat", "");
	    File dir;
	    try {
		dir = Util.getFile(baseDir, dirName);
	    } catch (FileNotFoundException e) {
		dir = new File(baseDir, dirName);
	    }
	    if (dir.exists()) {
		if (!dir.isDirectory())
		    throw new RuntimeException("'" + dir.getPath() + "' already exists but is not a directory");
	    } else {
		if (!dir.mkdir())
		    throw new RuntimeException("Can't create '" + dir.getPath() + "'");
	    }
	    File nClient = new File(dir, ClientFile.DEFAULT_CLIENT_FILE_NAME);
	    nClient.delete();
	    if (!file.renameTo(nClient))
		throw new RuntimeException("Can't move '" + file.getPath() + "' to '" + nClient.getPath() + "'");
	}
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
}
