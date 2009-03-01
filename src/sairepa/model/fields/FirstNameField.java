package sairepa.model.fields;

import java.io.IOException;

import sairepa.model.*;

import org.xBaseJ.xBaseJException;
import org.xBaseJ.fields.CharField;

public class FirstNameField extends ActField
{
  public FirstNameField(String name) throws xBaseJException, IOException {
    super(new CharField(name, 23));
  }

  @Override
  public void notifyUpdate(ActEntry e, String previousValue) {
    super.notifyUpdate(e, previousValue);

    if ("".equals(e.getValue().trim()) && e.getValue().length() >= 1) return;

    char[] cars = Util.trim(e.getValue()).toLowerCase().toCharArray();

    for (int i = 0 ; i < cars.length ; i++) {
      if (i == 0
	  || cars[i-1] == ' '
	  || cars[i-1] == '-')
	cars[i] = Character.toUpperCase(cars[i]);
    }

    e.setValue(new String(cars), false);
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
  public boolean warning(ActEntry e) {
    if (super.warning(e)) return true;
    if (e.getValue().contains("(")) return true;
    if (e.getValue().contains(")")) return true;
    if (e.getValue().contains("+") && !e.getValue().matches(".*\\+")) return true;
    return false;
  }

  @Override
  public boolean validate(ActEntry e) {
    boolean v = super.validate(e);
    if (v) v = e.getValue().matches("[\\D]*");
    return v;
  }
}
