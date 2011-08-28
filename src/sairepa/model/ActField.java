package sairepa.model;

import java.lang.Comparable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.kwain.fxie.*;

/**
 * ActField can observe each others (notification will specify the ActEntry modified each time)
 */
public class ActField implements FieldLayoutElement, Comparable<ActField>
{
	private String name;
	private int length;
	private XBaseFieldType fieldType;

	private List<ActField> observers = new ArrayList<ActField>();

	/**
	 * fieldPrototype : will be clone() later
	 */
	public ActField(String name, int length, XBaseFieldType fieldPrototype) {
		this.name = name;
		this.length = length;
		this.fieldType = fieldPrototype;
	}

	public String getName() {
		return name;
	}

	public boolean isMemo() {
		return (fieldType instanceof XBaseFieldType.XBaseFieldTypeMemo);
	}

	/**
	 * @see #getMaxLength()
	 */
	@Deprecated
		public int getLength() {
			return getMaxLength();
		}

	public int getMaxLength() {
		return length;
	}

	public static String pad(String initial, char pad, int length)
	{
		StringBuilder builder = new StringBuilder(initial);
		for (int to_pad = length - initial.length();
				to_pad > 0; to_pad--)
			builder.append(pad);
		return builder.toString();
	}

	public XBaseHeader.XBaseField createDBFField() throws XBaseException {
		return fieldType.buildField(name, length);
	}

	public ActEntry createEntry(Act act) {
		return new ActEntry(act, this);
	}

	public Iterator<ActField> iterator() {
		ArrayList<ActField> list = new ArrayList<ActField>();
		list.add(this);
		return list.iterator();
	}

	public int hashCode() {
		return getName().hashCode();
	}

	public boolean equals(Object o) {
		if (o == null || !(o instanceof ActField)) {
			return false;
		}

		return getName().equals(((ActField)o).getName());
	}

	public final boolean validate(Act a) {
		return validate(a.getEntry(this));
	}

	public void hasFocus(ActEntry e) {

	}

	public boolean hasAutoCompleter() {
		return false;
	}

	public AutoCompleter getAutoCompleter(Act a) {
		return null;
	}

	/**
	 * Called when an entry is modified; can modify the value
	 */
	public void notifyUpdate(ActEntry e, String previousValue) {
		if ("".equals(e.getValue()) && "".equals(previousValue)) {
			e.setValue("-", false);
		}
		if (e.getValue().length() > getMaxLength()) {
			e.setValue(e.getValue().substring(0, getMaxLength()), false);
		}
		for (ActField obs : observers) {
			obs.notifyUpdate(this, e, previousValue);
		}
	}

	/**
	 * Another field notifies us of its update.
	 */
	public void notifyUpdate(ActField f, ActEntry e, String previousValue) {
		throw new UnsupportedOperationException();
	}

	/**
	 * If the user must be warned about something (usually if the field is full).
	 */
	public boolean warning(ActEntry entry) {
		return (getMaxLength() > 5 && entry.getValue().length() >= getMaxLength());
	}

	/**
	 * If return false, the act shouldn't be saved to the DB.
	 * Can be overridden
	 */
	public boolean validate(ActEntry entry) {
		if (entry.getValue() == null) {
			return false;
		}

		if (entry.getValue().length() > getMaxLength()) {
			return false;
		}

		return true;
	}

	public int getNmbChildElements() {
		return 1;
	}

	public void addObserver(ActField f) {
		observers.add(f);
	}

	public int compareTo(ActField af) {
		return getName().compareTo(af.getName());
	}

	public String toString() {
		return getName();
	}
}
