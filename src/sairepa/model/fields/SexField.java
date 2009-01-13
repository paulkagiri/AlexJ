package sairepa.model.fields;

import java.io.IOException;

import sairepa.model.ActEntry;
import sairepa.model.ActField;

import org.xBaseJ.xBaseJException;
import org.xBaseJ.fields.CharField;

public class SexField extends ActField {
  public SexField(String name) throws xBaseJException, IOException {
    super(new CharField(name, 1));
  }

  public void notifyUpdate(ActEntry e, String previousValue) {
    super.notifyUpdate(e, previousValue);
    e.setValue(e.getValue().toUpperCase(), false);
  }

  public boolean validate(ActEntry e) {
    if (!super.validate(e))
      return false;

    if (!"M".equals(e.getValue())
	&& !"F".equals(e.getValue())
	&& !"-".equals(e.getValue())) {
      return false;
    }

    return true;
  }

  public static Sex getSex(ActEntry e) {
    if ("M".equals(e.getValue())) {
      return Sex.MALE;
    } else if ("F".equals(e.getValue())) {
      return Sex.FEMALE;
    } else {
      return Sex.UNKNOWN;
    }
  }
}
