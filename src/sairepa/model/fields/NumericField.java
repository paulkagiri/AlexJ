package sairepa.model.fields;

import java.io.IOException;

import sairepa.model.ActEntry;
import sairepa.model.ActField;

import org.xBaseJ.xBaseJException;
import org.xBaseJ.fields.NumField;

public class NumericField extends ActField {
    private int min;
    private int max;

    public NumericField(String fieldName, int size) throws xBaseJException, IOException {
	this(fieldName, size, 0, ((int)Math.pow(10, size))-1);
    }

    public NumericField(String fieldName, int size, int min, int max) throws xBaseJException, IOException {
	super(new NumField(fieldName, size, 0));
	this.min = min;
	this.max = max;
    }

    public boolean validate(ActEntry e) {
	int i;
	try {
	    i = Integer.valueOf(e.getValue());
	} catch (NumberFormatException exception) {
	    return false;
	}
	return (i >= min && i <= max);
    }
}
