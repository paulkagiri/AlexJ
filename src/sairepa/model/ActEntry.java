package sairepa.model;

import java.util.Observable;
import java.util.Observer;

public class ActEntry extends Observable
{
    private Act act;
    private ActField field;
    private String value;

    public ActEntry(Act act, ActField field) {
	this(act, field, "");
    }

    public ActEntry(Act act, ActField field, String value) {
	this.act = act;
	Util.check(field != null);
	this.field = field;
	this.value = value;
    }

    public void setAct(Act act) {
	this.act = act;
    }

    public Act getAct() {
	return act;
    }

    public ActField getField() {
	return field;
    }

    public String getValue() {
	return value;
    }

    public void setValue(String value, boolean notify) {
	if ( this.value != null && this.value.equals(value) )
	    return;

	String oldValue = this.value;
	this.value = value;
	setChanged();

	if (notify) {
	    field.notifyUpdate(this, oldValue);
	    notifyObservers(value);
	}
    }

    public void setValue(String value) {
	setValue(value, true);
    }

    public boolean validate() {
	return field.validate(this);
    }

    public boolean warning() {
	return field.warning(this);
    }

    public String toString() {
	return getValue();
    }
}
