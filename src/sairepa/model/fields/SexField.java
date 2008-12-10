package sairepa.model.fields;

import java.io.IOException;

import sairepa.model.ActEntry;
import sairepa.model.ActField;

import org.xBaseJ.micro.xBaseJException;
import org.xBaseJ.micro.fields.CharField;

public class SexField extends ActField {
    public SexField(String name) throws xBaseJException, IOException {
	super(new CharField(name, 1));
    }

    protected void notifyUpdate(ActEntry e) {
	super.notifyUpdate(e);
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
}
