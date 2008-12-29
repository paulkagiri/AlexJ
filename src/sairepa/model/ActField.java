package sairepa.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.xBaseJ.micro.fields.Field;
import org.xBaseJ.micro.fields.MemoField;

/**
 * ActField can observe each others
 */
public class ActField implements FieldLayoutElement
{
  private Field fieldPrototype;
  private List<ActField> observers = new ArrayList<ActField>();

  /**
   * fieldPrototype : will be clone()
   */
  public ActField(Field fieldPrototype) {
    this.fieldPrototype = fieldPrototype;
  }

  public String getName() {
    return fieldPrototype.getName();
  }

  public boolean isMemo() {
    return (fieldPrototype instanceof MemoField);
  }

  /**
   * @see #getMaxLength()
   */
  @Deprecated
  public int getLength() {
    return getMaxLength();
  }

  public int getMaxLength() {
    int maxLength = 255;

    if (!(fieldPrototype instanceof MemoField)) {
      maxLength = fieldPrototype.getLength();
    }

    return maxLength;
  }

  public Field createDBFField() {
    try {
      return (Field)fieldPrototype.clone();
    } catch (CloneNotSupportedException e) {
      // not supposed to happen.
      throw new RuntimeException(e);
    }
  }

  public ActEntry createEntry(Act act) {
    return new ActEntry(act, this);
  }

  public Iterator<ActField> iterator() {
    ArrayList<ActField> list = new ArrayList<ActField>();
    list.add(this);
    return list.iterator();
  }

  public int hashCode() {
    return getName().hashCode();
  }

  public boolean equals(Object o) {
    if (o == null || !(o instanceof ActField)) {
      return false;
    }

    return getName().equals(((ActField)o).getName());
  }

  /**
   * Shouldn't be overriden
   */
  public boolean validate(Act a) {
    return validate(a.getEntry(this));
  }

  public void hasFocus(ActEntry e) {

  }

  /**
   * Called when an entry is modified; can modify the value
   */
  public void notifyUpdate(ActEntry e, String previousValue) {
    if ("".equals(e.getValue()) && "".equals(previousValue)) {
      e.setValue("-", false);
    }
    if (e.getValue().length() > getMaxLength()) {
      e.setValue(e.getValue().substring(0, getMaxLength()), false);
    }
    for (ActField obs : observers) {
      obs.notifyUpdate(this, e, previousValue);
    }
  }

  /**
   * Another field notifies us of its update.
   */
  public void notifyUpdate(ActField f, ActEntry e, String previousValue) {
    throw new UnsupportedOperationException();
  }

  /**
   * If return false, the act shouldn't be saved to the DB.
   * Can be overridden
   */
  public boolean validate(ActEntry entry) {
    if (entry.getValue() == null) {
      return false;
    }

    if (entry.getValue().length() > getMaxLength()) {
      return false;
    }

    return true;
  }

  public int getNmbChildElements() {
    return 1;
  }

  public void addObserver(ActField f) {
    observers.add(f);
  }
}
