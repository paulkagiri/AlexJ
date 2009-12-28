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

public interface ActList extends Iterable<Act>
{
  public ActListFactory getFactory();
  public String getName();
  public FieldLayout getFields();
  public int getRowCount();

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
  public List<Act> getAllActs();

  /**
   * @param sortedBy field name ; can be null
   * @return beware: can return this !
   */
  public ActList getSortedActList(String sortedBy, boolean desc);

  public void insert(Act act);
  public void insert(Act act, int row);
  public void delete(Act act);
  public ActListIterator iterator();
  public Act createAct();
  public void refresh();
}
