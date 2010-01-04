package sairepa.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Client files are used to make sure that the user is allowed to use
 * this application and to know what he is allowed to do.
 */
public class ClientFile {
  public final static String DEFAULT_CLIENT_FILE_NAME = "client.dat";
  private String userName;
  private String zipCode;
  private String commune;

  public ClientFile(File dirPath, String filename)
      throws InvalidClientFileException, FileNotFoundException {
    load(Util.getFile(dirPath, filename));
  }

  public class InvalidClientFileException extends Exception {
    public static final long serialVersionUID = 1;

    protected InvalidClientFileException(String msg) {
      super(msg);
    }

    protected InvalidClientFileException(String msg, Throwable cause) {
      super(msg, cause);
    }
  }

  protected void load(File file) throws InvalidClientFileException {
    if (!file.exists()) {
      throw new InvalidClientFileException("Client file doesn't exist.");
    }

    BufferedReader reader;

    try {
      reader = new BufferedReader(new FileReader(file));
    } catch (IOException e) {
      throw new InvalidClientFileException(
	  "IOException when opening the client file", e);
    }

    long communeAndZipCode = -1;
    String communeAndZip = null;
    long userNameCode = -1;

    try {
      userName = reader.readLine();
      communeAndZipCode = Long.parseLong(reader.readLine());
      communeAndZip = reader.readLine();
      userNameCode = Long.parseLong(reader.readLine());
    } catch(NumberFormatException e) {
      throw new InvalidClientFileException(
          "Unable to read one of the codes", e);
    } catch (IOException e) {
      throw new InvalidClientFileException("IOException while reading", e);
    } finally {
      try {
	reader.close();
      } catch(IOException e) {
	System.err.println("WARNING - Can't close the client file properly because: "
			   + e.toString() + " : " + e.getMessage());
	e.printStackTrace();
      }
    }

    if ( userName == null || communeAndZip == null ) {
	throw new InvalidClientFileException("Error while parsing client field. Missing field");
    }

    checkCode(userName, userNameCode, 49724, 28971);
    checkCode(communeAndZip, communeAndZipCode, 64939, 62656);

    String[] split = communeAndZip.split(" ");
    if ( split.length != 2 )
	throw new InvalidClientFileException("Error while parsing client field: Can't separate properly commune and zip code");
    this.zipCode = split[0];
    this.commune = split[1];
  }

  protected void checkCode(String string, long code, final long a, final long b)
      throws InvalidClientFileException {
    long computed = getCode(string, a, b);

    if (computed != code) {
      throw new InvalidClientFileException("Invalid code.\n" +
          "File: " + Long.toString(code) +
          ", Computed: " + Long.toString(computed));
    }
  }

  /**
   * Weak DRM system.
   */
  protected long getCode(String string, final long a, final long b) {
    string = string.trim();

    long code = 0;

    for (int i = 0 ; i < string.length() ; i++) {
      code += string.charAt(i);
    }

    return (code * a) - b;
  }

  public String getUserName() {
    return userName;
  }

  public String getZipCode() {
    return zipCode;
  }

  public String getCommune() {
    return commune;
  }

  public String toString() {
    return ("  User: " + getUserName() + "\n"
	    + "  Commune: " + getCommune() + ", " + getZipCode());
  }
}
