package sairepa.model.fields;

import java.io.IOException;

import sairepa.model.*;

import net.kwain.fxie.XBaseFieldType;

public class ConvNameField extends ActField {
	public static final int CONV_NAME_FIELD_PRN_LNG = 8;
	public static final int CONV_NAME_FIELD_NOM_LNG = 20;

	private Sex sex;
	private SexField sexField;
	private String defaultValue = "";

	private Conventionalizer conventionalizer;
	private ActField[] origins = new ActField[3];

	public ConvNameField(String fieldName, Conventionalizer conv,
			Sex sex, ActField origin) throws IOException {
		super(fieldName, 8, new XBaseFieldType.XBaseFieldTypeString());
		this.conventionalizer = conv;
		this.sex = sex;
		this.sexField = null;
		for (int i = 0 ; i< origins.length ; i++) { origins[i] = null; };
		origins[sex.toInteger()] = origin;
	}

	public ConvNameField(String fieldName, Conventionalizer conv,
			Sex sex, ActField origin, String defaultValue) throws IOException {
		this(fieldName, conv, sex, origin);
		this.defaultValue = defaultValue;
	}

	public ConvNameField(String fieldName, Conventionalizer conv, SexField sexField,
			ActField originMale, ActField originFemale, ActField originUnknown)
		throws IOException {

		super(fieldName, 8, new XBaseFieldType.XBaseFieldTypeString());
		this.sex = Sex.UNKNOWN;
		this.conventionalizer = conv;
		this.sexField = sexField;
		origins[Sex.MALE.toInteger()] = originMale;
		origins[Sex.FEMALE.toInteger()] = originFemale;
		origins[Sex.UNKNOWN.toInteger()] = originUnknown;
	}

	public ConvNameField(String fieldName, Conventionalizer conv, SexField sexField,
			ActField originMale, ActField originFemale, ActField originUnknown,
			Sex sex)
		throws IOException {
		this(fieldName, conv, sexField, originMale, originFemale, originUnknown);
		this.sex = sex;
	}

	public ConvNameField(String fieldName,  Conventionalizer conv, SexField sexField,
			ActField originMale, ActField originFemale, ActField originUnknown,
			String defaultValue)
		throws IOException {
		this(fieldName, conv, sexField, originMale, originFemale, originUnknown);
		this.defaultValue = defaultValue;
	}

	public ConvNameField(String fieldName,  Conventionalizer conv, SexField sexField,
			ActField originMale, ActField originFemale, ActField originUnknown,
			String defaultValue, Sex sex)
		throws IOException {
		this(fieldName, conv, sexField, originMale, originFemale, originUnknown, defaultValue);
		this.sex = sex;
	}

	private Sex getSex(Act a) {
		Sex s;

		if (sexField != null) {
			ActEntry e = a.getEntry(sexField);
			s = SexField.getSex(e);
		}
		else
			s = sex;

		return s;
	}

	@Override
		public boolean hasAutoCompleter() {
			return true;
		}

	@Override
		public AutoCompleter getAutoCompleter(Act a) {
			return conventionalizer.getAutoCompleter(getName(), getSex(a));
		}

	@Override
		public void hasFocus(ActEntry e) {
			super.hasFocus(e);

			if ("".equals(e.getValue().trim())
					|| PrncvDb.UNKNOWN.equals(e.getValue().trim())) {
				ActField origin = origins[getSex(e.getAct()).toInteger()];
				if (origin == null) return;
				ActEntry src = e.getAct().getEntry(origin);
				Sex s = sex;
				if (s == Sex.UNKNOWN) s = getSex(e.getAct());
				String str = conventionalizer.conventionalize(src.getValue(), s);
				str = Util.trim(str);
				if ("".equals(str) || "-".equals(str)) str = defaultValue;
				e.setValue(str);
					}
		}

	@Override
		public void notifyUpdate(ActEntry e, String previousValue) {
			super.notifyUpdate(e, previousValue);
		}

	@Override
		public void notifyUpdate(ActField f, ActEntry theirEntry, String previousValue) {
			super.notifyUpdate(f, theirEntry, previousValue);
		}

	@Override
		public boolean validate(ActEntry e) {
			if (!super.validate(e)) return false;
			return e.getValue().matches("[\\D]*");
		}
}
