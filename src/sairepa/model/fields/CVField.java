package sairepa.model.fields;

import java.io.IOException;

import sairepa.model.ActEntry;
import sairepa.model.ActField;

import org.xBaseJ.xBaseJException;
import org.xBaseJ.fields.CharField;

public class CVField extends ActField {
  public CVField(String name) throws xBaseJException, IOException {
    super(new CharField(name, 1));
  }

  public void notifyUpdate(ActEntry e, String previousValue) {
    super.notifyUpdate(e, previousValue);
    e.setValue(e.getValue().toUpperCase(), false);
  }

  public boolean validate(ActEntry e) {
    if (!super.validate(e))
      return false;

    if (!"C".equals(e.getValue())
	&& !"V".equals(e.getValue())
	&& !"-".equals(e.getValue())) {
      return false;
    }

    return true;
  }
}
