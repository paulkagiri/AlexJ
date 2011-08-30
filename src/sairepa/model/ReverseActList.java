package sairepa.model;

import java.util.List;

public class ReverseActList implements ActList {
	private final DbHandler db;
	private final ActList masterActList;
	private ActList.ActListDbObserver dbObserver = new DumbDbObserver();

	protected ReverseActList(DbHandler db, ActList masterActList) {
		this.db = db;
		this.masterActList = masterActList;
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

	public int getActVisualRow(Act a) {
		return (getRowCount()-1) - masterActList.getActVisualRow(a);
	}

	public Act getAct(int position) {
		return masterActList.getAct((getRowCount()-1) - position);
	}

	public List<Act> getAllActs()
	{
		List<Act> acts = masterActList.getAllActs();
		java.util.Collections.reverse(acts);
		return acts;
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
		/* row definition is not clear here, better not do anything */
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
	}

	public void refresh(Act a) {
		masterActList.refresh(a);
	}

	public int getFileId() {
		return masterActList.getFileId();
	}
}

