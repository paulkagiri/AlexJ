package sairepa.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import sairepa.model.Hsqldb;

public interface AutoCompleter
{
	public final static int DEFAULT_MAX_DISTANCE = 10;
	public final static int DEFAULT_MIN_SRC_LENGTH = 2;

	public List<String> getSuggestions(ActEntry entry, String initialString);

	/**
	 * DefaultAutoCompleter looks in every similar field of the DB
	 */
	public static class DefaultAutoCompleter implements AutoCompleter
	{
		private final String fieldName;

		public DefaultAutoCompleter(String fieldName) {
			this.fieldName = fieldName;
		}

		private Collection<String> getSuggestions(Hsqldb db, ActListFactory factory, String initialString)
			throws SQLException {

			LinkedList<String> suggestions = new LinkedList<String>();
			int fieldId;

			synchronized(db.getConnection()) {
				if ( (fieldId = factory.getFieldId(fieldName)) < 0 ) {
					return suggestions;
				}

				PreparedStatement st =
					db.getConnection().prepareStatement("SELECT DISTINCT value FROM entries WHERE field = ?"
							+ " AND LOWER(LTRIM(SUBSTR(value, 0, ?))) = ?"
							+ " AND LENGTH(LTRIM(value)) <= ?");
				initialString = initialString.toLowerCase();
				initialString = initialString.trim();
				st.setInt(1, fieldId);
				st.setInt(2, initialString.length());
				st.setString(3, initialString);
				st.setInt(4, initialString.length() + DEFAULT_MAX_DISTANCE);

				ResultSet set = st.executeQuery();

				while (set.next()) {
					suggestions.add(set.getString(1));
				}
			}

			return suggestions;
		}

		private void removeDoubles(ArrayList<String> list) {
			for (int i = list.size() - 1; i > 0 ; i-- ) {
				if (list.get(i).equals(list.get(i-1))) {
					list.remove(i);
					i--;
				}
			}
		}

		public List<String> getSuggestions(ActEntry entry, String initialString) {
			if (initialString == null || initialString.trim().length() < DEFAULT_MIN_SRC_LENGTH)
				return new ArrayList<String>();

			ArrayList<String> suggestions = new ArrayList<String>();
			Model model = entry.getAct().getActList().getFactory().getModel();
			Hsqldb db = model.getDb();

			try {
				for (ActListFactory factory : model.getFactories()) {
					suggestions.addAll(getSuggestions(db, factory, initialString));
					Collections.sort(suggestions);
					removeDoubles(suggestions);
				}
			} catch (SQLException e) {
				System.err.println("AutoCompleter: SQLException: " + e.toString());
			}

			return suggestions;
		}
	}
}
