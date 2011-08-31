package sairepa.model;

import java.sql.*;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

public class SortedActList implements ActList {
	private final DbHandler db;
	private final ActList masterActList;
	private final List<ActSorting> sortingRule;

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
	}

	public ActList.ActListDbObserver getActListDbObserver() {
		return masterActList.getActListDbObserver();
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
		private int position;

		public SortingResult(String value, int actRow, int position) {
			this.value = value;
			this.actRow = actRow;
			this.position = position;
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

		public void setValue(String v) {
			this.value = v;
		}
		
		public int getActRow() {
			return actRow;
		}

		public int getPosition() {
			return position;
		}

		// comparaison
		// Results are ordered based on their order in a SortingResultSet

		private SortingResultSet refSet = null;
		public void setReferenceSet(SortingResultSet refSet) {
			this.refSet = refSet;
		}

		public int compareTo(SortingResult r) {
			int thisPosition = refSet.getRowToPosition().get(this.getActRow()).getPosition();
			int rPosition = refSet.getRowToPosition().get(r.getActRow()).getPosition();
			return new Integer(thisPosition).compareTo(new Integer(rPosition));
		}
	}

	protected class SortingResultSet {
		private int nbResults;
		private List<SortingResult> positionToRow;
		private List<SortingResult> rowToPosition;

		public SortingResultSet(ActSorting sorting, int nbResults) {
			this.nbResults = nbResults;

			positionToRow = getSortedList(sorting);

			rowToPosition = new Vector<SortingResult>(nbResults);
			((Vector<SortingResult>)rowToPosition).setSize(nbResults);
			for (SortingResult r : positionToRow) {
				rowToPosition.set(r.getActRow(), r);
			}
		}

		/**
		 * Return the whole act list, sorted as requested by the given sorting rule element
		 * @return a list: index = visual row ; value = act row + value
		 */
		private List<SortingResult> getSortedList(ActSorting sorting) {
			int i = 0, total = nbResults;

			Util.check(sorting.getFieldId() >= 0);

			Vector<SortingResult> results = new Vector<SortingResult>(nbResults);

			System.out.println("Sorting on field '" + sorting.getField() + "'");

			synchronized(db.getConnection()) {
				try {
					masterActList.getActListDbObserver().jobUpdate(DbOp.DB_QUERY, 0, 1);
					PreparedStatement rowGetter
						= db.getConnection().prepareStatement(
								"SELECT value, row FROM entries WHERE field = ? "
								+ "ORDER BY LTRIM(value)*1" + (sorting.getOrder() ? " DESC" : "")
								+ ", LOWER(LTRIM(value))" + (sorting.getOrder() ? " DESC" : ""));
					rowGetter.setInt(1, sorting.getFieldId());
					ResultSet set = rowGetter.executeQuery();
					try {
						i = 0;
						while(set.next()) {
							if ( (i % (total > 3000 ? 1000 : 100)) == 0 ) {
								System.out.println("Fetching act order ... "
											+ Integer.toString(i) + "/" + Integer.toString(total));
							}
							masterActList.getActListDbObserver().jobUpdate(DbOp.DB_FETCH, i, total);
							String value = set.getString(1);
							int row = set.getInt(2);
							results.add(new SortingResult(value, row, i));
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

		public List<SortingResult> getPositionToRow() {
			return positionToRow;
		}

		public List<SortingResult> getRowToPosition() {
			return rowToPosition;
		}
	}

	private void sortResults(List<SortingResult> results,
			SortingResultSet baseSet) {
		for (SortingResult sr : results) {
			sr.setReferenceSet(baseSet);
		}
		java.util.Collections.sort(results);
		for (SortingResult sr : results) {
			// result inherit values from the baseSet
			sr.setValue(baseSet.getRowToPosition().get(sr.getActRow()).getValue());
		}
	}

	/* Sort inputSet according to baseSet
	 */
	private boolean sortIdenticalResults(
			SortingResultSet inputSet,
			SortingResultSet baseSet) {
		List<SortingResult> results = inputSet.getPositionToRow();

		// first validate unique results
		SortingResult ppreviousResult = null, previousResult = null;
		boolean hasUnsortedResults = false;

		for (SortingResult result : results) {
			if ( previousResult != null && previousResult.getValue() != null) {
				if ( (ppreviousResult == null
							|| ppreviousResult.getValue() == null
							|| !ppreviousResult.getValue().equals(previousResult.getValue()) )
						&& (result.getValue() == null
							|| !previousResult.getValue().equals(result.getValue())) )
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
			return false;

		// then find all the non-unique results ..
		int startidx, endidx, total = results.size();

		for (startidx = 0; startidx < total; startidx++) {
			if (results.get(startidx).getValue() == null)
				continue;
			for (endidx = startidx + 1; endidx < total; endidx++) {
				if (results.get(endidx).getValue() == null
						|| !results.get(endidx).getValue().equals(results.get(startidx).getValue())) {
					break;
				}
			}

			// .. and sort them
			System.out.println("Sorting: Previous sortings left "
					+ Integer.toString(endidx - startidx) + " elements unsorted (from "
					+ Integer.toString(startidx) + " to " + Integer.toString(endidx - 1) + ")");
			Util.check(startidx < (endidx - 1));
			sortResults(results.subList(startidx, endidx), baseSet);

			startidx = endidx - 1;
		}

		return true;
	}

	public void refresh() {
		masterActList.refresh();

		masterActList.getActListDbObserver().startOfJobBatch("Tri des actes",
				(2 * sortingRule.size()) + 1);
		try {
			List<SortingResultSet> resultSets = new Vector<SortingResultSet>(sortingRule.size());
			int i;
			int total = masterActList.getRowCount();

			for (ActSorting sorting : sortingRule) {
				resultSets.add(new SortingResultSet(sorting, total));
			}

			i = 0;
			for (SortingResultSet set : resultSets.subList(1, resultSets.size())) {
				masterActList.getActListDbObserver().jobUpdate(DbOp.DB_SORT, i, resultSets.size()-1);
				if (!sortIdenticalResults(resultSets.get(0), set))
					break;
				i++;
			}

			positionToActRow = new Vector<Integer>(total);
			((Vector<Integer>)positionToActRow).setSize(total);
			actRowToPosition = new Vector<Integer>(total);
			((Vector<Integer>)actRowToPosition).setSize(total);
			int pos = 0;
			for (SortingResult r : resultSets.get(0).getPositionToRow()) {
				positionToActRow.set(pos, r.getActRow());
				actRowToPosition.set(r.getActRow(), pos);
				pos++;
			}
		} finally {
			masterActList.getActListDbObserver().endOfJobBatch();
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
