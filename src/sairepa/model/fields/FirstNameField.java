package sairepa.model.fields;

import java.io.IOException;

import sairepa.model.*;

import org.xBaseJ.micro.xBaseJException;
import org.xBaseJ.micro.fields.CharField;

public class FirstNameField extends ActField
{
  public FirstNameField(String name) throws xBaseJException, IOException {
    super(new CharField(name, 23));
  }

  @Override
  public void notifyUpdate(ActEntry e, String previousValue) {
    super.notifyUpdate(e, previousValue);

    char[] str = e.getValue().toLowerCase().toCharArray();
    if (str.length >= 1)
      str[0] = Character.toUpperCase(str[0]);
    e.setValue(new String(str), false);
  }

  @Override
  public void notifyUpdate(ActField f, ActEntry theirEntry, String previousValue) {
    super.notifyUpdate(f, theirEntry, previousValue);
  }

  @Override
  public void hasFocus(ActEntry e) {
    super.hasFocus(e);
  }

  @Override
  public boolean validate(ActEntry e) {
    boolean v = super.validate(e);
    if (v) v = e.getValue().matches("[\\D]*");
    if (v) v = !e.getValue().contains("(");
    if (v) v = !e.getValue().contains(")");
    return v;
  }
}
