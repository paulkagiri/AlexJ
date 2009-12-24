package sairepa.model.fields;

import java.io.IOException;

import sairepa.model.*;

import org.xBaseJ.xBaseJException;
import org.xBaseJ.fields.CharField;

public class LocalityField extends ActField
{
  public LocalityField(String name) throws xBaseJException, IOException {
    super(new CharField(name, 38));
  }

  @Override
  public void hasFocus(ActEntry e) {
    super.hasFocus(e);
    if ("".equals(e.getValue().trim())) {
      // yeah, that's kind of violent
      Act            a = e.getAct();
      ActList        al = a.getActList();
      ActListFactory f  = al.getFactory();
      Model          m  = f.getModel();
      ClientFile     cf = m.getClientFile();
      e.setValue(cf.getZipCode() + " " + cf.getCommune());
    }
  }

  @Override
  public void notifyUpdate(ActEntry e, String previousValue) {
    super.notifyUpdate(e, previousValue);
  }

  @Override
  public boolean validate(ActEntry e) {
    if (!super.validate(e))
      return false;

    return true;
  }
}
