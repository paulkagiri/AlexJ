package sairepa.model;

public class GenericActListIterator implements ActList.ActListIterator
{
  private ActList actList;
  private int currentRow;
  private Act lastActReturned = null;
  private Object lock;

  protected GenericActListIterator(Object lock, ActList actList) {
    this.actList = actList;
    this.lock = lock;
    currentRow = -1;
    lastActReturned = null;
  }

  public void add(Act a) {
    actList.insert(a, currentRow + 1);
  }

  public boolean hasNext() {
    return (currentRow < actList.getRowCount() - 1);
  }

  public boolean hasPrevious() {
    return (currentRow > 0);
  }

  public Act next() {
    synchronized(lock) {
      if (!hasNext()) {
	Util.check(false);
      }
      currentRow++;
      return (lastActReturned = actList.getAct(currentRow));
    }
  }

  public int nextIndex() {
    return currentRow + 1;
  }

  public Act previous() {
    synchronized(lock) {
      if (!hasPrevious()) {
	Util.check(false);
      }
      currentRow--;
      return (lastActReturned = actList.getAct(currentRow));
    }
  }

  public Act seek(int position) {
    synchronized(lock) {
      currentRow = position;
      return (lastActReturned = actList.getAct(position));
    }
  }

  public int previousIndex() {
    return currentRow - 1;
  }

  public int currentIndex() {
    return currentRow;
  }

  public void remove() {
    actList.delete(lastActReturned);
  }

  public void set(Act a) {
    synchronized(lock) {
      a.setRow(currentRow, false);
      a.update();
    }
  }
}
