package sairepa.model.fields;

import java.io.IOException;

import sairepa.model.*;

import net.kwain.fxie.XBaseFieldType;

public class FirstNameField extends ActField
{
    public FirstNameField(String name) throws IOException {
	super(name, 23, new XBaseFieldType.XBaseFieldTypeString());
    }

    @Override
    public void notifyUpdate(ActEntry e, String previousValue) {
	super.notifyUpdate(e, previousValue);

	String txt = e.getValue();

	if ("".equals(txt.trim()) && e.getValue().length() >= 1) return;

	if ( !Util.isMixedCase(txt) ) {
	    char[] cars = Util.trim(txt.toLowerCase()).toCharArray();
	    for (int i = 0 ; i < cars.length ; i++) {
		if (i == 0
		    || cars[i-1] == ' '
		    || cars[i-1] == '-'
		    || cars[i-1] == ':'
		    || cars[i-1] == '.')
		    cars[i] = Character.toUpperCase(cars[i]);
	    }
	    txt = new String(cars);
	}

	e.setValue(txt, false);
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
