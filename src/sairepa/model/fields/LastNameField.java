package sairepa.model.fields;

import java.io.IOException;

import sairepa.model.*;

import org.xBaseJ.micro.xBaseJException;
import org.xBaseJ.micro.fields.CharField;

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

  protected void notifyUpdate(ActEntry e, String previousValue) {
    super.notifyUpdate(e, previousValue);
    if ("".equals(previousValue.trim())) {
      e.setValue(upperCase(e.getValue(), sex), false);
    }
  }

  protected static String upperCase(String str, Sex sex) {
    char[] chars = str.toCharArray();
    int max = chars.length;

    if (sex != Sex.MALE && str.toLowerCase().endsWith("in")) {
      max -= 2;
    }

    int i;

    for (i = 0 ; i < max ; i++) {
      if ( (chars[i] >= 'a' && chars[i] <= 'z')
	   || (chars[i] >= 'A' && chars[i] <= 'Z') ) { // exclude the accents
	chars[i] = Character.toUpperCase(chars[i]);
      } else {
	chars[i] = Character.toLowerCase(chars[i]);
      }
    }

    for (; i < chars.length ; i++) {
      chars[i] = Character.toLowerCase(chars[i]);
    }

    return new String(chars);
  }

  protected void notifyUpdate(ActField f, ActEntry theirEntry, String previousValue) {

  }

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

  public boolean validate(ActEntry e) {
    return super.validate(e);
  }
}
