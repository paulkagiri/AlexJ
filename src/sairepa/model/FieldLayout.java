package sairepa.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

/**
 * Used to describe how fields must be organized in the UI.
 */
public class FieldLayout implements Iterable<ActField>, FieldLayoutElement
{
  private String title;
  private FieldLayoutElement[] elements;

  public FieldLayout(FieldLayoutElement[] elements) {
    this(null, elements);
  }

  public FieldLayout(String title, FieldLayoutElement[] elements) {
    this.title = title;
    this.elements = elements;
  }

  public String getTitle() {
    return title;
  }

  public FieldLayoutElement[] getElements() {
    return elements;
  }

  public static class ActFieldIterator implements Iterator<ActField> {
    public Iterator<FieldLayoutElement> elIt;
    public Iterator<ActField> currentIterator;

    public ActFieldIterator(FieldLayoutElement[] elements) {
      ArrayList<FieldLayoutElement> al =
          new ArrayList<FieldLayoutElement>(elements.length);
      for (FieldLayoutElement e : elements) {
	al.add(e);
      }

      elIt = al.iterator();

      currentIterator = null;
    }

    public void initCurrentIterator() {
      if ((currentIterator == null || !currentIterator.hasNext())
	  && elIt.hasNext()) {
	currentIterator = elIt.next().iterator();
      }
    }

    public boolean hasNext() {
      initCurrentIterator();

      return ((currentIterator != null) ? currentIterator.hasNext() : false);
    }

    public ActField next() {
      initCurrentIterator();

      return ((currentIterator != null) ? currentIterator.next() : null);
    }

    public void remove() {
      throw new UnsupportedOperationException(">o_/");
    }
  }

  public Iterator<ActField> iterator() {
    return new ActFieldIterator(elements);
  }

  public boolean validate(Act a) {
    for (FieldLayoutElement el : elements) {
      if (!el.validate(a)) {
	return false;
      }
    }
    return true;
  }

  public int getNmbChildElements() {
    int nmb = 0;
    for (FieldLayoutElement el : elements) {
      nmb+= el.getNmbChildElements();
    }
    return nmb;
  }
}
