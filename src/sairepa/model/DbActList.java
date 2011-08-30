package sairepa.model;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Vector;

public class DbActList implements ActList
{
	private ActListFactory factory;
	private DbHandler db;
	private int fileId;
	private FieldLayout fields;
	private int rowCount;
	private String name;
	private ActList.ActListDbObserver dbObserver = new DumbDbObserver();

	protected DbActList(ActListFactory factory, DbHandler db,
			int fileId, FieldLayout fields, String name)
		throws SQLException, IOException {
		this.db = db;
		this.fileId = fileId;
		this.fields = fields;
		this.name = name;
		this.factory = factory;
		computeRowCount();
	}

	public ActListFactory getFactory() {
		return factory;
	}

	public String getName() {
		return name;
	}

	public FieldLayout getFields() {
		return fields;
	}

	public void setActListDbObserver(ActList.ActListDbObserver obs) {
		this.dbObserver = obs;
	}

	public void computeRowCount() throws SQLException {
		synchronized(db.getConnection()) {
			PreparedStatement st
				= db.getConnection().prepareStatement("SELECT entries.row " +
						"FROM ENTRIES INNER JOIN FIELDS ON entries.field = fields.id " +
						"WHERE fields.file = ? ORDER BY entries.row DESC LIMIT 1");
			st.setInt(1, fileId);

			ResultSet set = st.executeQuery();
			try {
				this.rowCount = ((set.next()) ? set.getInt(1) + 1 : 0);
				System.out.println("computeRowCount(): row count: " + this.rowCount);
			} finally {
				set.close();
			}
		}
	}

	public int getRowCount() {
		return rowCount;
	}

	// often, a same row is asked many times in a row
	private int lastRowReturned = -1;
	private Act lastActReturned = null;

	public Act getAct(int row) {
		synchronized(db.getConnection()) {
			if (row == lastRowReturned && lastActReturned != null) {
				return lastActReturned;
			}

			try {
				Act act = new Act(db.getConnection(), this, fileId, fields, row);
				this.lastActReturned = act;
				this.lastRowReturned = row;
				return act;
			} catch(SQLException e) {
				throw new RuntimeException("SQLException", e);
			}
		}
	}

	public int getActVisualRow(Act a) {
		/* not sorted, so no problem here */
		return a.getRow();
	}

	public List<Act> getAllActs() {
		java.util.Date start, stop;
		start = new java.util.Date();

		Vector<Act> acts = new Vector<Act>(getRowCount());
		Map<Integer, ActField> idToFields = new HashMap<Integer, ActField>();

		synchronized(db.getConnection()) {
			try {
				PreparedStatement st;

				dbObserver.startOfJobBatch(2);
				dbObserver.jobUpdate(ActList.DbOp.DB_QUERY, 0, 1);

				st = db.getConnection().prepareStatement("SELECT fields.id, fields.name FROM fields WHERE fields.file = ?");
				st.setInt(1, fileId);
				ResultSet set = st.executeQuery();

				try {
					while(set.next()) {
						Integer i = set.getInt(1);
						String name = set.getString(2);
						ActField f = fields.getField(name);
						Util.check(f != null);
						idToFields.put(i, f);
					}
				} finally {
					set.close();
				}

				st = db.getConnection().prepareStatement("SELECT fields.id, entries.row, entries.value"
						+ " FROM fields INNER JOIN entries ON fields.id = entries.field"
						+ " WHERE fields.file = ? ORDER by entries.row");
				st.setInt(1, fileId);
				set = st.executeQuery();

				dbObserver.jobUpdate(ActList.DbOp.DB_QUERY, 1, 1);

				try {
					Map<ActField, String> entries = null;
					int currentRow = -1;
					int total = getRowCount();

					while(set.next()) {
						ActField field = idToFields.get(set.getInt(1));
						int row = set.getInt(2);
						String value = set.getString(3);

						if ( row != currentRow ) {
							if ( row % (total > 3000 ? 1000 : 100) == 0 ) {
								dbObserver.jobUpdate(ActList.DbOp.DB_FETCH, row, total);
							}

							Util.check( row == currentRow + 1 );
							if ( entries != null ) {
								acts.add(new Act(db.getConnection(), this, fileId, fields, entries, currentRow));
							}
							entries = new HashMap<ActField, String>();
							currentRow = row;
						}
						assert(!entries.containsKey(field));
						entries.put(field, value);
					}

					if ( entries != null ) {
						acts.add(new Act(db.getConnection(), this, fileId, fields, entries, currentRow));
					}
				} finally {
					dbObserver.endOfJobBatch();
					set.close();
				}
			} catch (SQLException e) {
				throw new RuntimeException("SQLException", e);
			}
		}

		stop = new java.util.Date();
		System.out.println("Took " + Long.toString((stop.getTime() - start.getTime()) / 1000) +
				" seconds to load " + Integer.toString(getRowCount()) + " elements");
		return acts;
	}

	public void insert(Act act) {
		insert(act, rowCount);
	}

	public void insert(Act act, int row) {
		synchronized(db.getConnection()) {
			try {
				act.setRow(row, false);
				shiftAfter(row, 1);
				act.update();
			} catch (SQLException e) {
				throw new RuntimeException("SQLException", e);
			} finally {
				try {
					db.getConnection().setAutoCommit(true);
				} catch (SQLException e) {
					throw new RuntimeException("SQLException", e);
				}
			}
		}
	}

	public void delete(Act act) {
		synchronized(db.getConnection()) {
			try {
				act.delete();
				shiftAfter(act.getRow() + 1, -1);
			} catch (SQLException e) {
				throw new RuntimeException("SQLException", e);
			}
		}
	}

	public ActListIterator iterator() {
		return new GenericActListIterator(db.getConnection(), this);
	}

	private void shiftAfter(int position, int shift) throws SQLException {
		PreparedStatement selectFields
			= db.getConnection().prepareStatement("SELECT fields.id FROM fields WHERE fields.file = ?");
		selectFields.setInt(1, fileId);

		PreparedStatement selectRows
			= db.getConnection().prepareStatement("SELECT row FROM entries WHERE field = ? ORDER BY row DESC LIMIT 1");

		PreparedStatement update
			= db.getConnection().prepareStatement("UPDATE entries SET row = ? " +
					"WHERE row = ? AND field = ?");

		ResultSet fieldSet = selectFields.executeQuery();

		try {
			while(fieldSet.next()) {
				int fieldId = fieldSet.getInt(1);

				selectRows.setInt(1, fieldId);
				ResultSet rowSet = selectRows.executeQuery();
				int lastRow;
				try {
					if (rowSet.next())
						lastRow = rowSet.getInt(1);
					else
						lastRow = position-1;
				} finally {
					rowSet.close();
				}

				int start = (shift >= 0 ? lastRow : position);
				int move = (shift >= 0 ? -1 : 1);

				for ( int row = start ; row <= lastRow && row >= position ; row += move ) {
					System.out.println("Shifting " + row + " to " + (row+shift));
					update.setInt(1, row + shift);
					update.setInt(2, row);
					update.setInt(3, fieldId);
					update.execute();
				}
			}
		} finally {
			fieldSet.close();
		}
		
		computeRowCount();
	}

	/**
	 * this act is not stored until added to the list
	 */
	public Act createAct() {
		return new Act(db.getConnection(), this, fileId, fields);
	}

	protected int getFileId() {
		return fileId;
	}

	public void refresh() {
		synchronized(db.getConnection()) {
			lastRowReturned = -1;
			lastActReturned = null;
			try {
				computeRowCount();
			} catch (SQLException e) {
				throw new RuntimeException("SQLException", e);
			}
		}
	}

	public void refresh(Act a) {
		synchronized(db.getConnection()) {
			if ( a.getRow() == lastRowReturned ) {
				lastRowReturned = -1;
				lastActReturned = null;
			}
		}
	}

	public ActList getSortedActList(List<ActSorting> sortingRule) {
		if ( sortingRule.get(0).getField() == null && !sortingRule.get(0).getOrder() )
			return this;
		else if ( sortingRule.get(0).getField() == null )
			return new ReverseActList(db, this);
		return new SortedActList(db, this, sortingRule);
	}
}
