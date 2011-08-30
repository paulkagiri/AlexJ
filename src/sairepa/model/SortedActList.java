package sairepa.model;

import java.sql.*;
import java.util.List;

public class SortedActList implements ActList {
	private final Hsqldb db;
	private final ActList masterActList;
	private final List<ActSorting> sortingRule;

	private ActList.ActListDbObserver dbObserver = new DumbDbObserver();

	public SortedActList(Hsqldb db,
			ActList masterActList,
			List<ActSorting> sortingRule) {
		this.db = db;
		this.masterActList = masterActList;
		this.sortingRule = sortingRule;

		System.out.println("Sorting by: ");
		for (ActSorting as : sortingRule) {
			System.out.println(as.toString());
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
		// TODO
		return -1;
	}

	public Act getAct(int position) {
		// TODO
		return null;
	}

	public List<Act> getAllActs()
	{
		// TODO
		throw new UnsupportedOperationException("TODO");
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

	public void refresh() {
		masterActList.refresh();

		// TODO
	}

	public void refresh(Act a) {
		masterActList.refresh(a);
	}
}
