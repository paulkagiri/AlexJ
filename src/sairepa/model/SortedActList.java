package sairepa.model;

import java.sql.*;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

public class SortedActList implements ActList {
	private final DbHandler db;
	private final ActList masterActList;
	private final List<ActSorting> sortingRule;

	private ActList.ActListDbObserver dbObserver = new DumbDbObserver();

	private List<Integer> positionToActRow; // position = position once sorted
	private List<Integer> actRowToPosition; // position = position once sorted

	protected SortedActList(DbHandler db,
			ActList masterActList,
			List<ActSorting> sortingRule) {
		this.db = db;
		this.masterActList = masterActList;
		this.sortingRule = sortingRule;

		System.out.println("Sorting by: ");
		for (ActSorting as : sortingRule) {
			as.resolveFieldId(db, this);
			System.out.println(as.toString());
			if (as.getFieldId() < 0) {
				throw new UnsupportedOperationException(
						"Don't know how to do multiple sorts with sorting on the row id");
			}
		}
		System.out.println("--");

		refresh();
	}

	public void setActListDbObserver(ActList.ActListDbObserver obs) {
		masterActList.setActListDbObserver(obs);
		this.dbObserver = obs;
	}

	public ActListFactory getFactory() {
		return masterActList.getFactory();
	}

	public String getName() {
		return masterActList.getName();
	}

	public FieldLayout getFields() {
		return masterActList.getFields();
	}

	public int getRowCount() {
		return masterActList.getRowCount();
	}

	private int lastPositionReturned = -1;
	private Act lastActReturned = null;

	public int getActVisualRow(Act a) {
		return actRowToPosition.get(a.getRow());
	}

	public Act getAct(int position) {
		int row = positionToActRow.get(position);
		return masterActList.getAct(row);
	}

	public List<Act> getAllActs()
	{
		Vector<Act> sortedActs = new Vector<Act>(masterActList.getRowCount());
		sortedActs.setSize(masterActList.getRowCount());

		for (Act a : masterActList.getAllActs()) {
			sortedActs.set(actRowToPosition.get(a.getRow()), a);
		}

		return sortedActs;
	}

	/**
	 * @return beware: can return this !
	 */
	public ActList getSortedActList(List<ActSorting> sortingRule) {
		return masterActList.getSortedActList(sortingRule);
	}

	public void insert(Act act) {
		masterActList.insert(act);
	}

	public void insert(Act act, int row) {
		throw new UnsupportedOperationException("Can't do");
	}

	public void delete(Act act) {
		masterActList.delete(act);
	}

	public ActListIterator iterator() {
		return new GenericActListIterator(db.getConnection(), this);
	}

	public Act createAct() {
		return masterActList.createAct();
	}

	protected class SortingResult implements Comparable<SortingResult> {
		private String value;
		private int actRow;

		public SortingResult(String value, int actRow) {
			this.value = value;
			this.actRow = actRow;
		}

		/**
		 * Called once the position of the object in the list is sure
		 */
		public void validate() {
			value = null; // let the garbage collector take the value
		}

		public String getValue() {
			return value;
		}

		public int getActRow() {
			return actRow;
		}

		public int compareTo(SortingResult r) {
			return new Integer(actRow).compareTo(new Integer(r.getActRow()));
		}
	}

	/**
	 * Return the whole act list, sorted as requested by the given sorting rule element
	 * @return a list: index = visual row ; value = act row + value
	 */
	private List<SortingResult> getSortedList(ActSorting sorting) {
		int i = 0, total = masterActList.getRowCount();

		Util.check(sorting.getFieldId() >= 0);

		Vector<SortingResult> results = new Vector<SortingResult>(masterActList.getRowCount());

		System.out.println("Sorting on field '" + sorting.getField() + "'");

		synchronized(db.getConnection()) {
			try {
				dbObserver.jobUpdate(DbOp.DB_QUERY, 0, 1);
				PreparedStatement rowGetter
					= db.getConnection().prepareStatement(
							"SELECT value, row FROM entries WHERE field = ? ORDER BY LOWER(LTRIM(value))"
							+ (sorting.getOrder() ? " DESC" : ""));
				rowGetter.setInt(1, sorting.getFieldId());
				ResultSet set = rowGetter.executeQuery();
				try {
					i = 0;
					while(set.next()) {
						if ( (i % 500) == 0 )
							dbObserver.jobUpdate(DbOp.DB_FETCH, i, total);
						String value = set.getString(1);
						int row = set.getInt(2);
						results.add(new SortingResult(value, row));
						i++;
					}
				} finally {
					set.close();
				}
			} catch (SQLException e) {
				throw new RuntimeException("SQLException", e);
			}
		}

		return results;
	}

	private List<Integer[]> getRowIntervals(List<SortingResult> results) {
		Vector<Integer[]> out = new Vector<Integer[]>();
		Integer[] interval = null;

		Collections.sort(results);

		int startRow, endRow;

		for (SortingResult result : results) {
			if (interval == null) {
				interval = new Integer[2];
				interval[0] = new Integer(result.getActRow());
				interval[1] = new Integer(result.getActRow());
			} else if ( interval[1].equals(result.getActRow()-1) ) {
				interval[1] = new Integer(result.getActRow());
			} else {
				out.add(interval);
				interval = new Integer[2];
				interval[0] = new Integer(result.getActRow());
				interval[1] = new Integer(result.getActRow());
			}
		}
		if (interval != null)
			out.add(interval);

		return out;
	}

	private void sortResults(List<SortingResult> results,
			List<ActSorting> nextSortingCriteria,
			int depth) {
		ActSorting sorting = nextSortingCriteria.get(0);
		List<Integer[]> rowIntervals = getRowIntervals(results);

		Util.check(sorting.getFieldId() >= 0);

		System.out.println("Sorting on field '" + sorting.getField() + "'");

		boolean first = true;
		String query = "SELECT value, row FROM entries WHERE field = ? AND (";
		for (Integer[] interval : rowIntervals) {
			if (!first)
				query += " OR ";
			if (interval[0] == interval[1])
				query += "row = "+ Integer.toString(interval[0]);
			else
				query += "(" + Integer.toString(interval[0]) + " <= row"
					+ " AND row <= " + Integer.toString(interval[1]) + ")";
			first = false;
		}
		query += ") ORDER BY LOWER(LTRIM(value))";
		if (sorting.getOrder())
			query += " DESC";

		int i = 0;
		synchronized(db.getConnection()) {
			try {
				PreparedStatement rowGetter
					= db.getConnection().prepareStatement(query);
				rowGetter.setInt(1, sorting.getFieldId());
				ResultSet set = rowGetter.executeQuery();
				try {
					while(set.next()) {
						String value = set.getString(1);
						int row = set.getInt(2);
						results.set(i, new SortingResult(value, row));
						i++;
					}
				} finally {
					set.close();
				}
			} catch (SQLException e) {
				throw new RuntimeException("SQLException. Query was: " + query, e);
			}
		}

		// Recursion
		sortIdenticalResults(results,
				nextSortingCriteria.subList(1, nextSortingCriteria.size()),
				depth+1);
	}

	private void sortIdenticalResults(List<SortingResult> results,
			List<ActSorting> nextSortingCriteria,
			int depth) {
		if (nextSortingCriteria.size() <= 0)
			return;

		// first validate unique results
		SortingResult ppreviousResult = null, previousResult = null;
		boolean hasUnsortedResults = false;

		for (SortingResult result : results) {
			if ( previousResult != null ) {
				if ( (ppreviousResult == null
							|| ppreviousResult.getValue() == null
							|| !ppreviousResult.getValue().equals(previousResult.getValue()) )
						&& (!previousResult.getValue().equals(result.getValue())) )
					previousResult.validate();
				else
					hasUnsortedResults = true;
			}

			ppreviousResult = previousResult;
			previousResult = result;
		}

		if ( ppreviousResult == null
				|| ppreviousResult.getValue() == null
				|| !ppreviousResult.getValue().equals(previousResult.getValue()) )
			previousResult.validate();
		else
			hasUnsortedResults = true;

		if (!hasUnsortedResults)
			return;


		// then find all the non-unique results ..
		int startidx, endidx;

		for (startidx = 0; startidx < results.size(); startidx++) {
			if (results.get(startidx).getValue() == null)
				continue;
			for (endidx = startidx + 1; endidx < results.size(); endidx++) {
				if (results.get(endidx).getValue() == null
						|| !results.get(endidx).getValue().equals(results.get(startidx).getValue())) {
					break;
				}
			}

			// .. and sort them
			System.out.println("Sorting: Sorting " + Integer.toString(depth) + " left "
					+ Integer.toString(endidx - startidx - 1) + " elements unsorted (from "
					+ Integer.toString(startidx) + " to " + Integer.toString(endidx - 1) + ")");
			Util.check(startidx < (endidx - 1));
			sortResults(results.subList(startidx, endidx), nextSortingCriteria, depth);

			startidx = endidx - 1;
		}
	}

	public void refresh() {
		masterActList.refresh();

		dbObserver.startOfJobBatch(3);
		try {
			List<SortingResult> results = getSortedList(sortingRule.get(0));

			dbObserver.jobUpdate(DbOp.DB_SORT, 0, 2);
			sortIdenticalResults(results, sortingRule.subList(1, sortingRule.size()), 1);

			dbObserver.jobUpdate(DbOp.DB_SORT, 1, 2);
			this.positionToActRow = new Vector<Integer>(masterActList.getRowCount());
			this.actRowToPosition = new Vector<Integer>(masterActList.getRowCount());
			((Vector<Integer>)actRowToPosition).setSize(masterActList.getRowCount());

			int i = 0;
			for (SortingResult result : results) {
				positionToActRow.add(result.getActRow());
				actRowToPosition.set(result.getActRow(), i);
				i++;
			}
		} finally {
			dbObserver.endOfJobBatch();
		}

		System.out.println("Sorting done");
	}

	public void refresh(Act a) {
		masterActList.refresh(a);
	}

	public int getFileId() {
		return masterActList.getFileId();
	}
}
