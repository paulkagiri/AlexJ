package sairepa.model;

import java.sql.*;

public class ActSorting {
	private final String field;
	private final boolean desc;

	private int fieldId; // -2 = unresolved ; -1 = by row ; else == field id in the db

	public ActSorting(String sortedBy, boolean desc) {
		this.field = sortedBy;
		this.desc = desc;
		this.fieldId = -2;
	}

	public String getField() {
		return field;
	}

	public boolean getOrder() {
		return desc;
	}

	public void resolveFieldId(DbHandler db, ActList actList) {
		if (fieldId >= -1) // already resolved
			return;
		if (field == null) {
			fieldId = -1;
			return;
		}

		synchronized(db.getConnection()) {
			try {
				PreparedStatement fieldGetter = db.getConnection().prepareStatement(
						"SELECT fields.id FROM fields WHERE fields.name = ? AND fields.file = ? LIMIT 1");
				fieldGetter.setString(1, field);
				fieldGetter.setInt(2, actList.getFileId());
				ResultSet set = fieldGetter.executeQuery();
				try {
					Util.check(set.next());
					this.fieldId = set.getInt(1);
				} finally {
					set.close();
				}
			} catch (SQLException e) {
				throw new RuntimeException("SQLException", e);
			}
		}
	}

	public int getFieldId() {
		if (fieldId == -2)
			throw new AssertionError("Field id not yet resolved ! Call resolveFieldId() first !");
		return fieldId;
	}

	public String toString() {
		String str = field;
		if (desc)
			str += " (desc)";
		if (fieldId >= -1)
			str += " (fieldId = " + Integer.toString(fieldId) + ")";
		return str;
	}
}

