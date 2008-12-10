package sairepa.model.fields;

import java.io.IOException;

import sairepa.model.*;

import org.xBaseJ.micro.xBaseJException;
import org.xBaseJ.micro.fields.CharField;

public class LastNameField extends ActField {
  public LastNameField(String name) throws xBaseJException, IOException {
    super(new CharField(name, 20));
  }

  /**
   * @param origin if no value is set, listen for notifyUpdate() from origin and takes its value
   */
  public LastNameField(String name, ActField origin) throws xBaseJException, IOException {
    this(name);
    origin.addObserver(this);
  }

  protected void notifyUpdate(ActEntry e) {
    super.notifyUpdate(e);
    e.setValue(e.getValue().toUpperCase(), false);
  }

  protected void notifyUpdate(ActField f, ActEntry theirEntry) {
    if ("".equals(theirEntry.getValue())) {
      return;
    }

    // let's look first for the value of our entry
    ActEntry ourEntry = theirEntry.getAct().getEntry(this);
    if ("".equals(ourEntry.getValue().trim())) {
      // then we steal their value :)
      ourEntry.setValue(theirEntry.getValue());
    }
  }

  public boolean validate(ActEntry e) {
    return super.validate(e);
  }
}
