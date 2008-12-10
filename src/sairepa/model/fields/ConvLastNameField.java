package sairepa.model.fields;

import java.io.IOException;

import sairepa.model.*;

import org.xBaseJ.micro.xBaseJException;
import org.xBaseJ.micro.fields.CharField;

public class ConvLastNameField extends ActField {
  private Sex sex;
  private SexField sexField;

  private ActField[] origins = new ActField[3];

  public ConvLastNameField(String fieldName, Sex sex, ActField origin) throws xBaseJException, IOException {
    super(new CharField(fieldName, 20));
    this.sex = sex;
    this.sexField = null;
    for (int i = 0 ; i< origins.length ; i++) { origins[i] = null; };
    origins[sex.toInteger()] = origin;
    origin.addObserver(this);
  }

  public ConvLastNameField(String fieldName, SexField sexField,
			   ActField originMale, ActField originFemale, ActField originUnknown)
    throws xBaseJException, IOException {

    super(new CharField(fieldName, 20));
    this.sex = Sex.UNKNOWN;
    this.sexField = sexField;
    origins[Sex.MALE.toInteger()] = originMale;
    origins[Sex.FEMALE.toInteger()] = originFemale;
    origins[Sex.UNKNOWN.toInteger()] = originUnknown;
    for (ActField origin : origins) origin.addObserver(this);
  }

  public Sex getSex(Act a) {
    if (sex == Sex.UNKNOWN && sexField != null) {
      ActEntry e = a.getEntry(sexField);
      return SexField.getSex(e);
    }
    return sex;
  }

  protected void notifyUpdate(ActEntry e, String previousValue) {
    super.notifyUpdate(e, previousValue);
  }

  protected void notifyUpdate(ActField f, ActEntry theirEntry, String previousValue) {

  }

  public boolean validate(ActEntry e) {
    return super.validate(e);
  }
}
