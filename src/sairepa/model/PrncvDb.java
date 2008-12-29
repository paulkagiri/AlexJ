package sairepa.model;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import sairepa.model.fields.Sex;

import org.xBaseJ.micro.DBF;
import org.xBaseJ.micro.xBaseJException;

public class PrncvDb
{
  public static final String UNKNOWN = "????????";
  private Map[] prncvs = new Map[2];

  public PrncvDb() {
    this(new File("prncv.dbf"));
  }

  public PrncvDb(File dbf) {
    loadDbf(dbf);
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
	String in = dbfFile.getField("PRN_TT").get().trim().toLowerCase();
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
    if ("-".equals(lu.trim())) return "-";
    String cv = (String)prncvs[sex.toInteger()].get(lu.trim().toLowerCase());
    return ((cv == null) ? UNKNOWN : cv);
  }
}
