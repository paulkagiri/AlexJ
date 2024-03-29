package sairepa.model;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Vector;

public interface ActList extends Iterable<Act>
{
  public ActListFactory getFactory();
  public String getName();
  public FieldLayout getFields();
  public int getRowCount();

  /**
   * Internal to the model. Do not use outside of it.
   * @return the id of the file this act list is attached to
   */
  public int getFileId();

  public enum DbOp {
    DB_QUERY(),
    DB_FETCH(),
    DB_SORT(),
  }

  /**
   * the obserser is notified each time a long db query
   * or manipulation is started
   */
  public static interface ActListDbObserver {
      public void startOfJobBatch(String description, int nmbJob);
      public void jobUpdate(DbOp job, int currentPosition, int endOfJobPosition);
      public void endOfJobBatch();
  }
  public void setActListDbObserver(ActListDbObserver obs);
  public ActListDbObserver getActListDbObserver();

  public static interface ActListIterator extends ListIterator<Act> {
    public void add(Act a);
    public boolean hasNext();
    public boolean hasPrevious();
    public Act next();
    public int nextIndex();
    public Act previous();
    public Act seek(int position);
    public int previousIndex();
    public int currentIndex();
    public void remove();
    public void set(Act a);
  }

  public Act getAct(int row);
  public int getActVisualRow(Act a);
  public List<Act> getAllActs();

  /**
   * @param sortedBy field name ; can be null
   * @return beware: can return this !
   */
  public ActList getSortedActList(List<ActSorting> sortingRule);

  public void insert(Act act);
  public void insert(Act act, int row);
  public void delete(Act act);
  public ActListIterator iterator();
  public Act createAct();
  public void refresh();
  public void refresh(Act a);
}
