package sairepa.model;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
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
	String in = dbfFile.getField("PRN_TT").get().trim();
	in = truncate(in);
	String out = dbfFile.getField("PRN_CV").get().trim();

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
    lu = lu.trim();
    lu = truncate(lu);
    String cv = (String)prncvs[sex.toInteger()].get(lu);
    return ((cv == null) ? UNKNOWN : cv);
  }
}
