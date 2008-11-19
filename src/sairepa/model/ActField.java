package sairepa.model;

import java.util.ArrayList;
import java.util.Iterator;

import org.xBaseJ.micro.fields.Field;
import org.xBaseJ.micro.fields.MemoField;

public class ActField implements FieldLayoutElement
{
  private Field fieldPrototype;

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

  public int getLength() {
    return fieldPrototype.getLength();
  }

  private int getMaxLength() {
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

  public boolean validate(Act a) {
    return validate(a.getEntry(this));
  }


  /**
   * Called by ActEntry when modified
   * sub-classes can change the behavior of this method (but must call it !).
   */
  protected void notifyUpdate(ActEntry e) {
  }

  /**
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
}
