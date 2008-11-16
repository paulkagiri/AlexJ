package sairepa.model;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;

public class Util
{
  private Util() { }

  private static class FileFilterIgnoringCase implements FileFilter {
    private String filename;

    public FileFilterIgnoringCase(String filename) {
      this.filename = filename;
    }

    public boolean accept(File file) {
      return (file.getName().equalsIgnoreCase(filename));
    }
  }

  public static class ProjectFileFilter implements FileFilter {
    public ProjectFileFilter() { }

    public boolean accept(File file) {
      return (file.isDirectory() && file.getName().length() == 3);
    }
  }

  /**
   * Try to find a file matching the filename (without the case sensitivity).
   * Returns dirPath + File.separator + filename if none exists
   * @throws FileNotFoundException if many files with the same name
   * but a different case exist
   */
  public static File getFile(File dirPath, String filename)
      throws FileNotFoundException {
    File[] files = dirPath.listFiles(
	new FileFilterIgnoringCase(filename));

    if (files.length < 1) {
      return new File(dirPath, filename);
    }

    if (files.length > 1) {
      throw new FileNotFoundException("Many files with differents cases for: "
          + new File(dirPath, filename).getPath());
    }

    return files[0];
  }

  /**
   * Assertion
   */
  public static void check(boolean b) {
    if (!b) {
      throw new AssertionError();
    }
  }
}
