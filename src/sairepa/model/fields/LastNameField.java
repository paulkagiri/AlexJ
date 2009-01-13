package sairepa.model.fields;

import java.io.IOException;

import sairepa.model.*;

import org.xBaseJ.xBaseJException;
import org.xBaseJ.fields.CharField;

public class LastNameField extends ActField {
  private Sex sex;
  private ActField origin;

  public LastNameField(String name, Sex sex) throws xBaseJException, IOException {
    super(new CharField(name, 20));
    this.sex = sex;
  }

  /**
   * @param origin if no value is set, listen for notifyUpdate() from origin and takes its value
   */
  public LastNameField(String name, Sex sex, ActField origin) throws xBaseJException, IOException {
    this(name, sex);
    this.origin = origin;
  }

  public Sex getSex() {
    return sex;
  }

  @Override
  public void notifyUpdate(ActEntry e, String previousValue) {
    super.notifyUpdate(e, previousValue);
    if ("".equals(previousValue.trim())
	|| "-".equals(previousValue.trim())) {
      e.setValue(Util.upperCase(e.getValue(), true, sex), false);
    }
  }

  @Override
  public void notifyUpdate(ActField f, ActEntry theirEntry, String previousValue) {
    super.notifyUpdate(f, theirEntry, previousValue);
  }

  @Override
  public void hasFocus(ActEntry e) {
    if (origin == null) {
      return;
    }

    ActEntry theirEntry = e.getAct().getEntry(origin);

    if ("".equals(theirEntry.getValue())) {
      return;
    }

    // let's look first for the value of our entry
    ActEntry ourEntry = theirEntry.getAct().getEntry(this);

    if ("".equals(ourEntry.getValue().trim())) {
      // then we steal their value :)

      String theirValue = theirEntry.getValue();

      if (getSex() == Sex.MALE) {
	if (!(origin instanceof LastNameField) || ((LastNameField)origin).getSex() != Sex.MALE) {
	  theirValue = Util.extractMalePart(theirValue);
	}
      }

      ourEntry.setValue(theirValue);
    }
  }

  @Override
  public boolean validate(ActEntry e) {
    boolean v = super.validate(e);
    if (v) v = e.getValue().matches("[\\D]*");
    return v;
  }
}
