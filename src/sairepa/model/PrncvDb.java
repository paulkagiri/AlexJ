package sairepa.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import sairepa.model.fields.Sex;

import org.xBaseJ.DBF;
import org.xBaseJ.xBaseJException;

public class PrncvDb
{
  public static final int NMB_CARS = 6;

  public static final String[] EXCEPTIONS = {
    "Christ"
  };

  public static final String UNKNOWN = "????????";
  private Map[] prncvs = new Map[2];

  public PrncvDb() {
    this(new File("prncv.dbf"));
  }

  public PrncvDb(File dbf) {
    loadDbf(dbf);
  }

  public String truncate(String str) {
    for (String exception : EXCEPTIONS) {
      if (str.startsWith(exception)) {
	if (str.length() == exception.length()) return str;
	return str.substring(0, exception.length() + 1);
      }
    }
    if (str.length() > NMB_CARS)
      return str.substring(0, NMB_CARS);
    else
      return str;
  }

  private void loadDbf(File dbf) {
    System.out.println("Loading '" + dbf.getPath() + "' ...");

    for (int i = 0 ; i < prncvs.length ; i++) {
      prncvs[i] = new HashMap();
    }

    DBF dbfFile;

    try {
      dbfFile = new DBF(dbf.getPath(), DBF.READ_ONLY, "CP850");
    } catch (IOException e) {
      System.err.println("Warning : Can't load prncv database ! (IOException)");
      System.err.println(e.toString());
      return;
    } catch (xBaseJException e) {
      System.err.println("Warning : Can't load prncv database ! (xBaseJExcaption)");
      System.err.println(e.toString());
      return;
    }

    try {
      while (true) {
	dbfFile.read();
	Sex sex = Sex.getSex(dbfFile.getField("MFA").get());
	Util.check(sex != Sex.UNKNOWN);
	String in = Util.trim(dbfFile.getField("PRN_TT").get());
	in = truncate(in);
	String out = Util.trim(dbfFile.getField("PRN_CV").get());

	prncvs[sex.toInteger()].put(in, out);
      }
    } catch (IOException e) {
      System.err.println("Warning: Error while reading prncv ! (IOException)");
      System.err.println(e.toString());
      return;
    } catch (xBaseJException e) {
      // exception used to signal EOF .......
    } finally {
      try {
	dbfFile.close();
      } catch (IOException e) {
	// nobody cares.
      }
    }
  }

  public String getPrncv(String lu, Sex sex) {
    Util.check(sex != Sex.UNKNOWN);
    System.out.println("Prncv: '" + lu + "' / " +sex.toString());
    if ("-".equals(lu.trim())) return "-";
    lu = Util.trim(lu);
    lu = truncate(lu);
    String cv = (String)prncvs[sex.toInteger()].get(lu);
    return ((cv == null) ? UNKNOWN : cv);
  }

  public final static int MIN_SRC_LENGTH = 1;

  public List<String> getClosest(String src, Sex sex) {
    ArrayList<String> rs = new ArrayList<String>();

    if (src == null || src.trim().length() < MIN_SRC_LENGTH)
      return rs;

    HashSet<String> set = new HashSet<String>();

    src = src.trim().toLowerCase();

    for (String s : (((Collection<String>)prncvs[sex.toInteger()].values()))) {
	String simple = s.trim().toLowerCase();
	if ( simple.startsWith(src) )
	    set.add(s);
    }

    for (String s : set)
      rs.add(s);
    Collections.sort(rs);
    return rs;
  }

  public AutoCompleter createAutoCompleter(Sex sex) {
    return new PrncvDbAutoCompleter(sex);
  }

  private class PrncvDbAutoCompleter implements AutoCompleter {
    private Sex sex;

    public PrncvDbAutoCompleter(Sex sex) {
      this.sex = sex;
    }

    public List<String> getSuggestions(ActEntry entry, String initialString) {
      return getClosest(initialString, sex);
    }
  }
}
