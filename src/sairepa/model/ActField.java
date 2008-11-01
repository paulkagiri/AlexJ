package sairepa.model;

import org.xBaseJ.micro.fields.Field;
import org.xBaseJ.micro.fields.MemoField;

public class ActField
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

  /**
   * Called by ActEntry when modified
   * sub-classes can change the behavior of this method (but must call it !).
   */
  protected void notifyUpdate(ActEntry e) {
    int maxLength = 255;

    if (!(fieldPrototype instanceof MemoField)) {
      maxLength = fieldPrototype.Length;
    }

    // truncate if too long
    if (e.getValue().length() > maxLength) {
      e.setValue(e.getValue().substring(0, maxLength), false);
    }
  }

  public Field createDBFField() {
    try {
      return (Field)fieldPrototype.clone();
    } catch (CloneNotSupportedException e) {
      // not supposed to happen.
      throw new RuntimeException(e);
    }
  }
}
