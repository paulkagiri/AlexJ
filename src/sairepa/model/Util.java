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
	    return (file.isDirectory() && !file.getName().startsWith("."));
	}
    }

    public static class ClientFileFilter implements FileFilter {
	public ClientFileFilter() { }

	public boolean accept(File file) {
	    return file.isFile() && file.getName().toLowerCase().startsWith("cli_");
	}
    }

    public static class NonDirectoryFilter implements FileFilter {
	public NonDirectoryFilter() { }

	public boolean accept(File file) {
	    return !file.isDirectory()
		&& !".".equals(file.getName())
		&& !"..".equals(file.getName());
	}
    }

    public static class DirectoryFilter implements FileFilter {
	public DirectoryFilter() { }

	public boolean accept(File file) {
	    return file.isDirectory();
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

    public static void check(boolean b, String msg) {
	if (!b) {
	    throw new AssertionError(msg);
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
	accents.put('\377' /* ÿ */, 'i'); // or here
	accents.put(':',            '.');
	accents.put(';',            '.');
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
	in = in.replaceAll("Ae", "E");
	in = in.replaceAll("AE", "E");

	in = in.replaceAll("oe", "e");
	in = in.replaceAll("Oe", "E");
	in = in.replaceAll("OE", "E");

	in = in.replaceAll("ue", "u");
	in = in.replaceAll("Ue", "U");
	in = in.replaceAll("UE", "U");

	in = in.replaceAll("uo", "u");
	in = in.replaceAll("Uo", "U");
	in = in.replaceAll("UO", "U");

	in = in.replaceAll("dt", "d");
	in = in.replaceAll("Dt", "D");
	in = in.replaceAll("DT", "D");

	in = in.replaceAll("tz", "z");
	in = in.replaceAll("Tz", "Z");
	in = in.replaceAll("TZ", "Z");

	in = in.replaceAll("th", "t");
	in = in.replaceAll("Th", "T");
	in = in.replaceAll("TH", "T");

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

    private static String dropEndIfItsThisOne(String in, String end) {
	if (in.endsWith(end)) {
	    in = in.substring(0, in.length() - end.length());
	}
	return in;
    }

    public static String conventionalizeLastName(String in, Sex sex) {
	in = trim(in);
	//if (sex != Sex.FEMALE) { // MALE
	in = extractMalePart(in);
	//}
	in = conventionalizeAccents(in.toLowerCase());
	return upperCase(in, true, sex);
    }

    public static String conventionalizeFirstName(String in, Sex sex) {
	in = trim(in);
	in = in.replaceAll("-", " ");
	in = dropEndIfItsThisOne(in, "+");
	in = dropEndIfItsThisOne(in, "?");
	in = trim(in);

	String[] split = in.split(" ");
	if (split.length <= 0) return "";
	in = split[split.length-1];
	in = trim(in);

	if (in.startsWith("Chris")) in = in.replaceFirst("Chris", "Cris");

	in = conventionalizeAccents(in);
	in = conventionalizeDoubles(in);

	if (sex != Sex.MALE) { // FEMALE || UNKNOWN
	    if (in.endsWith("ae") || in.endsWith("am")) {
		in = in.substring(0, in.length() - 1); // we drop the 'e' or the 'm'
	    }
	} else { // MALE || UNKNOWN
	    if (in.endsWith("ae")) {
		in = in.substring(0, in.length() - 1); // we drop the 'e'
	    } else if (in.endsWith("um")) {
		in = in.substring(0, in.length() - 1) + "s"; // we drop the 'm' and put a 's'
	    } else if (in.endsWith("em")) {
		in = in.substring(0, in.length() - 2) + "is"; // we drop the 'em' and put a 'is'
	    } else
		in = dropEndIfItsThisOne(in, "iu");
	}

	if (sex != Sex.FEMALE) { // MALE || UNKNOWN
	    if (!(in.equals("Eloi") || in.equals("Elio"))) {
		in = dropEndIfItsThisOne(in, "o");
		in = dropEndIfItsThisOne(in, "i");
	    }
	} else if (sex != Sex.MALE) { // FEMALE || UNKNOWN
	    in = dropEndIfItsThisOne(in, "ius");
	}

	in = conventionalizeReplacements(in);

	return in;
    }

    /**
     * Custom implementation of String.trim():
     * Trim trailing white spaces, but not leading ones
     */
    public static String trim(String in) {
	int out;
	char[] chars = in.toCharArray();

	for (out = chars.length;
	     out >= 1
		 && (chars[out-1] == ' '
		     || chars[out-1] == '\t'
		     || chars[out-1] == '\n');
	     out--);

	return new String(chars, 0, out);
    }

    /* Levenshtein distance */
    public static int distance(String a, String b, final int maxDistance) {
	int distance;

	distance = Math.abs(b.length() - a.length());
	if (distance >= maxDistance)
	    return distance;

	a = a.toLowerCase();
	b = b.toLowerCase();
	final char[] ar = a.toCharArray();
	final char[] br = b.toCharArray();

	for (int ai = 0, bi = 0 ;
	     ai < ar.length && bi < br.length ;
	     ai++, bi++) {
	    if (ar[ai] != br[ai])
		distance++;
	    if (distance >= maxDistance)
		return distance;
	}

	return distance;
    }
}
