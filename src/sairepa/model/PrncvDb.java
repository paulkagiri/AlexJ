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

import net.kwain.fxie.XBaseException;
import net.kwain.fxie.XBaseImport;
import net.kwain.fxie.XBaseValue;

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
				return str;
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

		XBaseImport imp;
		try {
			imp = new XBaseImport(dbf, null);
		} catch (IOException e) {
			System.err.println("Warning : Can't load prncv database ! (IOException)");
			System.err.println(e.toString());
			return;
		} catch (XBaseException e) {
			System.err.println("Warning : Can't load prncv database ! (XBaseException)");
			System.err.println(e.toString());
			return;
		}

		try {
			Map shortTtToFullTt[] = new HashMap[2];  /* for debug purpose */
			for (int i = 0 ; i < shortTtToFullTt.length ; i++) {
				shortTtToFullTt[i] = new HashMap();
			}

			while (imp.available() > 0) {
				List<XBaseValue> values = imp.read();
				if (values == null)
					break;
				Sex sex = Sex.UNKNOWN;
				String realIn = null;
				String in = null;
				String out = null;
				for (XBaseValue value : values) {
					if ( "MFA".equals(value.getField().getName()) )
						sex = Sex.getSex(value.getHumanReadableValue());
					else if ( "PRN_TT".equals(value.getField().getName()) ) {
						realIn = Util.trim(value.getHumanReadableValue());
						in = truncate(realIn);
					}
					else if ( "PRN_CV".equals(value.getField().getName()) )
						out = Util.trim(value.getHumanReadableValue());
				}

				Util.check(sex != Sex.UNKNOWN);
				Util.check(in != null);
				Util.check(out != null);
				if ( prncvs[sex.toInteger()].containsKey(in) ) {
					System.out.println("---");
					System.out.println("Integrity error in PrncvDb");
					System.out.println("Got PRN_TT = '" + realIn + "' (short version: '" + in + "')");
					System.out.println("With PRN_CV = " + out);
					String otherRealIn = (String)shortTtToFullTt[sex.toInteger()].get(in);
					System.out.println("But got also PRN_TT = '" + otherRealIn + "' (short version: '" + in + "')");
					System.out.println("With PRN_CV = " + prncvs[sex.toInteger()].get(in));
				}
				shortTtToFullTt[sex.toInteger()].put(in, realIn);
				prncvs[sex.toInteger()].put(in, out);
			}
			System.out.println("---");
		} catch(XBaseException e) {
			throw new RuntimeException("Invalid Prncvdb file !", e);
		} finally {
			imp.close();
		}
	}

	public String getPrncv(String lu, Sex sex) {
		Util.check(sex != Sex.UNKNOWN);
		if ("-".equals(lu.trim())) return "-";
		System.out.println("(1) Prncv: '" + lu + "' / " +sex.toString());
		lu = Util.trim(lu);
		lu = truncate(lu);
		System.out.println("(2) Prncv: '" + lu + "' / " +sex.toString());
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
