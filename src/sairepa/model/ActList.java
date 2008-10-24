package sairepa.model;

import org.xBaseJ.micro.DBF;
import org.xBaseJ.micro.xBaseJException;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ActList implements Iterable<Act>
{
  public static final Object LOCK = new Object();

  private ActField[] fields;

  public ActList(ActField[] fields) throws IOException {
    this.fields = fields;
  }

  private class ActListIterator implements ListIterator<Act> {
    private int position = 0;

    public ActListIterator() { }

    @Override
    public void add(Act e) {

    }

    @Override
    public boolean hasNext() {
      synchronized(LOCK) {
	return false;
      }
    }

    @Override
    public boolean hasPrevious() {
      synchronized(LOCK) {
	return false;
      }
    }

    @Override
    public Act next() {
      synchronized(LOCK) {
	return null;
      }
    }

    @Override
    public int nextIndex() {
      synchronized(LOCK) {
	return -1;
      }
    }

    @Override
    public Act previous() {
      synchronized(LOCK) {
	return null;
      }
    }

    @Override
    public int previousIndex() {
      synchronized(LOCK) {
	return -1;
      }
    }

    @Override
    public void remove() {
      synchronized(LOCK) {

      }
    }

    @Override
    public void	set(Act e) {
      e.update();
    }
  }

  public ListIterator<Act> iterator() {
    return new ActListIterator();
  }
}
