package sairepa.model.fields;

import java.io.IOException;

import sairepa.model.*;

import org.xBaseJ.xBaseJException;
import org.xBaseJ.fields.CharField;

public class ConvNameField extends ActField {
  private Sex sex;
  private SexField sexField;
  private String defaultValue = "";

  private Conventionalizer conventionalizer;
  private ActField[] origins = new ActField[3];

  public ConvNameField(String fieldName, Conventionalizer conv,
		       Sex sex, ActField origin) throws xBaseJException, IOException {
    super(new CharField(fieldName, 20));
    this.conventionalizer = conv;
    this.sex = sex;
    this.sexField = null;
    for (int i = 0 ; i< origins.length ; i++) { origins[i] = null; };
    origins[sex.toInteger()] = origin;
  }

  public ConvNameField(String fieldName, Conventionalizer conv,
		       Sex sex, ActField origin, String defaultValue) throws xBaseJException, IOException {
    this(fieldName, conv, sex, origin);
    this.defaultValue = defaultValue;
  }

  public ConvNameField(String fieldName, Conventionalizer conv, SexField sexField,
		       ActField originMale, ActField originFemale, ActField originUnknown)
    throws xBaseJException, IOException {

    super(new CharField(fieldName, 20));
    this.sex = Sex.UNKNOWN;
    this.conventionalizer = conv;
    this.sexField = sexField;
    origins[Sex.MALE.toInteger()] = originMale;
    origins[Sex.FEMALE.toInteger()] = originFemale;
    origins[Sex.UNKNOWN.toInteger()] = originUnknown;
  }

  public ConvNameField(String fieldName,  Conventionalizer conv, SexField sexField,
		       ActField originMale, ActField originFemale, ActField originUnknown,
		       String defaultValue)
    throws xBaseJException, IOException {
    this(fieldName, conv, sexField, originMale, originFemale, originUnknown);
    this.defaultValue = defaultValue;
  }

  public Sex getSex(Act a) {
    if (sex == Sex.UNKNOWN && sexField != null) {
      ActEntry e = a.getEntry(sexField);
      return SexField.getSex(e);
    }
    return sex;
  }

  @Override
  public void hasFocus(ActEntry e) {
    super.hasFocus(e);

    if ("".equals(e.getValue().trim())
	|| PrncvDb.UNKNOWN.equals(e.getValue().trim())) {
      ActField origin = origins[getSex(e.getAct()).toInteger()];
      if (origin == null) return;
      ActEntry src = e.getAct().getEntry(origin);
      String str = conventionalizer.conventionalize(src.getValue(), getSex(e.getAct()));
      str = str.trim();
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
    return super.validate(e);
  }
}
