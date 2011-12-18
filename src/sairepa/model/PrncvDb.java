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

	public static final String[] TRUNCATE_EXCEPTIONS = {
		"Christ"
	};

	private static class PrefixName {
		private String prefix;
		private Sex sex;

		public PrefixName(String prefix, Sex sex) {
			this.prefix = prefix;
			this.sex = sex;
		}

		public String getPrefix() {
			return prefix;
		}

		public Sex getSex() {
			return sex;
		}

		public boolean matchName(String name, Sex sex) {
			if (this.sex != Sex.UNKNOWN && this.sex != sex)
				return false;
			return name.toLowerCase().startsWith(this.prefix.toLowerCase());
		}
	}

	public static final PrefixName[] PREFIX_NAME_TO_IGNORE = {
		new PrefixName("Joan", Sex.MALE),
		new PrefixName("Johan", Sex.MALE),
		new PrefixName("Hans", Sex.MALE),
		new PrefixName("Hanns", Sex.MALE),
		new PrefixName("Anne", Sex.FEMALE),
		new PrefixName("Anna", Sex.FEMALE),
		new PrefixName("Marie", Sex.FEMALE),
		new PrefixName("Maria", Sex.FEMALE),
	};

	public static final String NAME_SEPARATORS = "[ :.-]";
	public static final String UNKNOWN = "????????";

	private Map[] prncvs = new Map[2];

	public PrncvDb() {
		this(new File("prncv.dbf"));
	}

	public PrncvDb(File dbf) {
		loadDbf(dbf);
	}

	public String truncate(String str) {
		for (String exception : TRUNCATE_EXCEPTIONS) {
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

	/**
	 * @brief get a Prncv for a given single prn (first name).
	 */
	private String getSinglePrncv(String singleName, Sex sex) {
		Util.check(sex != Sex.UNKNOWN);
		if ("-".equals(singleName.trim())) return "-";
		System.out.println("(1) Prncv: '" + singleName + "' / " +sex.toString());
		singleName = Util.trim(singleName);
		singleName = truncate(singleName);
		System.out.println("(2) Prncv: '" + singleName + "' / " +sex.toString());
		String cv = (String)prncvs[sex.toInteger()].get(singleName);
		return ((cv == null) ? UNKNOWN : cv);
	}

	/**
	 * @brief get the prncv corresponding to the prn (first name) (taking into account
	 *  that a person can have multiple first names)
	 */
	public String getPrncv(String prn, Sex sex) {
		Util.check(sex != Sex.UNKNOWN);
		if ("-".equals(prn.trim())) return "-";
		System.out.println("(0) Prvncv: '" + prn + "' / " + sex.toString());
		prn = Util.trim(prn);
		
		String[] prns = prn.split(NAME_SEPARATORS);

		/* we have to identify the 3 first names, however, between
		 * 2 names, there may be many separators --> we have to remake
		 * the array
		 */
		String[] names = new String[3];
		int src = 0;
		int dst = 0;
		for (src = 0, dst = 0 ; src < prns.length && dst < names.length ; src++) {
			if ("".equals(prns[src].trim()))
				continue;
			names[dst] = prns[src];
			dst++;
		}

		if (dst <= 0)
			return "-";
		if (dst <= 1)
			return getSinglePrncv(names[0], sex);

		int offset = 0;
		for (PrefixName prefix : PREFIX_NAME_TO_IGNORE) {
			if (prefix.matchName(names[0], sex)) {
				offset = 1;
				break;
			}
		}

		if (names[offset + 1] != null)
			return getSinglePrncv(names[offset + 1], sex);
		return getSinglePrncv(names[offset], sex);
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
