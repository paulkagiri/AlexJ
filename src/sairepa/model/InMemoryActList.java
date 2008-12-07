package sairepa.model;

import java.util.List;
import java.util.Vector;

public class InMemoryActList implements ActList
{
  private ActList dbActList;
  private List<Act> acts;

  public InMemoryActList(ActList dbActList) {
    this.dbActList = dbActList;
    refresh();
  }

  public String getName() {
    return dbActList.getName();
  }

  public FieldLayout getFields() {
    return dbActList.getFields();
  }

  public int getRowCount() {
    return dbActList.getRowCount();
  }

  public Act getAct(int row) {
    return acts.get(row);
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
    // reload the content of the act list in memory
    acts = new Vector<Act>();
    for (Act a : dbActList) {
      acts.add(a);
    }
  }
}