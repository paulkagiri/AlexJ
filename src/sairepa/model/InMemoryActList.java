package sairepa.model;

import java.util.List;
import java.util.Vector;

public class InMemoryActList implements ActList
{
    private ActList dbActList;
    private List<Act> acts;

    protected InMemoryActList(ActList dbActList) {
	assert(dbActList != null && !(dbActList instanceof InMemoryActList));
	this.dbActList = dbActList;
	refresh();
    }

    public ActListFactory getFactory() {
	return dbActList.getFactory();
    }

    public String getName() {
	return dbActList.getName();
    }

    public FieldLayout getFields() {
	return dbActList.getFields();
    }

    public int getRowCount() {
	return acts.size();
    }

    public Act getAct(int row) {
	return acts.get(row);
    }

    public List<Act> getAllActs() {
	return acts;
    }

    /**
     * Returns an InMemoryActList if possible (== if enought memory is available), otherwise,
     * returns actList
     */
    public static ActList encapsulate(ActList actList)
    {
	try {
	    assert(!(actList instanceof InMemoryActList));
	    return new InMemoryActList(actList);
	} catch (OutOfMemoryError e) {
	    System.err.println("OutOfMemoryError: Woops! JVM probably screwed up because " +
			       "it's unexpected here ; anyway let's fall back on direct DB " +
			       "access");
	    return actList;
	} catch (Exception e) {
	    System.err.println("Got an exception using InMemoryActList, falling back on simple act list: " + e.toString());
	    e.printStackTrace();
	    return actList;
	}
    }

    public ActList getSortedActList(String sortedBy, boolean desc) {
	try {
	    dbActList = dbActList.getSortedActList(sortedBy, desc);
	    refresh();
	} catch (OutOfMemoryError e) {
	    System.err.println("OutOfMemoryError: Woops! JVM probably screwed up because " +
			       "it's unexpected here ; anyway let's fall back on direct DB " +
			       "access");
	    return dbActList;
	} catch (Exception e) {
	    System.err.println("Got an exception using InMemoryActList, falling back on simple act list: " + e.toString());
	    e.printStackTrace();
	    return dbActList;
	}
	return this;
    }

    public void insert(Act act) {
	dbActList.insert(act);
	refresh();
    }

    public void insert(Act act, int row) {
	dbActList.insert(act, row);
	refresh();
    }

    public void delete(Act act) {
	dbActList.delete(act);
	refresh();
    }

    public ActListIterator iterator() {
	// the db iterator is probably more appropriate in most of the case
	return dbActList.iterator();
    }

    public Act createAct() {
	return dbActList.createAct();
    }

    public void refresh() {
	dbActList.refresh();

	// reload the content of the act list in memory
	int current = 0;
	int total = dbActList.getRowCount();
	acts = dbActList.getAllActs();

	if (acts.size() != dbActList.getRowCount()) {
	    System.err.println("WARNING: Row count invalid ! ("
			       + Integer.toString(acts.size()) + " / "
			       + Integer.toString(dbActList.getRowCount()) + ")");
	}

	Util.check(acts.size() == dbActList.getRowCount());
    }
}
