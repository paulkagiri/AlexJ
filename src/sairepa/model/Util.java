package sairepa.model;

import sairepa.model.fields.Sex;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

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

  public static String extractMalePart(String in) {
    char[] chars = in.toCharArray();
    int i;
    for (i = chars.length-1; Character.isLowerCase(chars[i]) && i > 0 ; i--);
    if (i < 0) return in;
    return in.substring(0, i+1);
  }

  private final static Map<Character, Character> accents = new HashMap<Character, Character>();

  static {
    accents.put('\344' /* ä */, 'e'); // no mistake here
    accents.put('\340' /* à */, 'a');
    accents.put('\342' /* â */, 'a');
    accents.put('\353' /* ë */, 'e');
    accents.put('\351' /* é */, 'e');
    accents.put('\350' /* è */, 'e');
    accents.put('\352' /* ê */, 'e');
    accents.put('\357' /* ï */, 'i');
    accents.put('\356' /* î */, 'i');
    accents.put('\366' /* ö */, 'e'); // no mistake here
    accents.put('\364' /* ô */, 'o');
    accents.put('\374' /* ü */, 'u');
    accents.put('\371' /* ù */, 'u');
    accents.put('\373' /* û */, 'u');
    accents.put('y'    /* y */, 'i'); // no mistake here
    accents.put('\377' /* ÿ */, 'i');
  }

  private static String conventionalizeAccents(String in) {
    char[] chars = in.toCharArray();
    for (int i = 0 ; i < chars.length ; i++) {
      if (accents.containsKey(chars[i])) {
	chars[i] = accents.get(chars[i]);
      }
    }
    return new String(chars);
  }

  private static String conventionalizeDoubles(String in) {
    StringBuilder builder = new StringBuilder();
    char previousChar = (char)-1;
    char[] chars = in.toCharArray();

    for (int i = 0 ; i < chars.length ; i++) {
      if (previousChar == chars[i]) {
	previousChar = (char)-1;
	continue;
      }
      builder.append(chars[i]);
      previousChar = chars[i];
    }

    return builder.toString();
  }

  private static String conventionalizeReplacements(String in) {
    in = in.replaceAll("ae", "e");
    in = in.replaceAll("oe", "e");
    return in;
  }

  public static String upperCase(String str, boolean lastName, Sex sex) {
    char[] chars = str.toCharArray();
    int max = chars.length;

    if (lastName && sex != Sex.MALE && str.toLowerCase().endsWith("in")) {
      max -= 2;
    }

    int i;

    for (i = 0 ; i < max ; i++) {
      if ( (chars[i] >= 'a' && chars[i] <= 'z')
	   || (chars[i] >= 'A' && chars[i] <= 'Z') ) { // exclude the accents
	chars[i] = Character.toUpperCase(chars[i]);
      } else {
	chars[i] = Character.toLowerCase(chars[i]);
      }

      if (lastName && chars[i] == '\377') chars[i] = 'Y';
    }

    for (; i < chars.length ; i++) {
      chars[i] = Character.toLowerCase(chars[i]);
    }

    return new String(chars);
  }

  public static String conventionalizeLastName(String in, Sex sex) {
    if (sex != Sex.FEMALE) {
      in = extractMalePart(in);
    }
    in = conventionalizeAccents(in.toLowerCase());
    in = conventionalizeDoubles(in);
    in = conventionalizeReplacements(in);
    return upperCase(in, true, sex);
  }

  public static String conventionalizeFirstName(String in) {
    in = in.replaceAll("-", " ");
    String[] split = in.split(" ");
    if (split.length <= 0) return "";
    in = split[split.length-1];
    in = conventionalizeAccents(in);
    in = conventionalizeDoubles(in);
    in = conventionalizeReplacements(in);
    return in;
  }
}
