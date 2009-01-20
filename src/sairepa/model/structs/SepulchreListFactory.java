package sairepa.model.structs;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;

import sairepa.model.*;
import sairepa.model.fields.*;

import org.xBaseJ.xBaseJException;
import org.xBaseJ.fields.CharField;
import org.xBaseJ.fields.DateField;
import org.xBaseJ.fields.FloatField;
import org.xBaseJ.fields.LogicalField;
import org.xBaseJ.fields.MemoField;
import org.xBaseJ.fields.NumField;
import org.xBaseJ.fields.PictureField;

/**
 * if you modify this class, modify also: DeceaseListFactory, NotarialDeceaseListFactory
 */
public class SepulchreListFactory extends ActListFactory
{
  public static FieldLayout fields = null;

  private static boolean isEmpty(ActEntry e) {
      return ("".equals(e.getValue().trim()) || "-".equals(e.getValue().trim()));
  }

  private static class AdultTest implements Test {
    private ActField lastName2;
    private ActField firstName2;
    private ActField lastName3;
    private ActField firstName3;
    private ActField lastNameC;
    private ActField firstNameC;

    public AdultTest(ActField lastName2, ActField lastName3, ActField lastNameC,
		     ActField firstName2, ActField firstName3, ActField firstNameC) {
      this.lastName2 = lastName2;
      this.lastName3 = lastName3;
      this.lastNameC = lastNameC;
      this.firstName2 = firstName2;
      this.firstName3 = firstName3;
      this.firstNameC = firstNameC;

      Util.check(lastName2 != null);
      Util.check(lastName3 != null);
      Util.check(lastNameC != null);
      Util.check(firstName2 != null);
      Util.check(firstName3 != null);
      Util.check(firstNameC != null);
    }

    public boolean test(Act a) {
      boolean name2e = isEmpty(a.getEntry(lastName2)) && isEmpty(a.getEntry(firstName2));
      boolean name3e = isEmpty(a.getEntry(lastName3)) && isEmpty(a.getEntry(firstName3));
      boolean nameCe = isEmpty(a.getEntry(lastNameC)) && isEmpty(a.getEntry(firstNameC));

      boolean t = ( !nameCe || (name2e && name3e) );
      return t;
    }
  }

  private static class NonEmptyTest implements Test {
    private ActField field1;
    private ActField field2;

    public NonEmptyTest(ActField field1, ActField field2) {
      this.field1 = field1;
      this.field2 = field2;
    }

    public boolean test(Act a) {
      return !(isEmpty(a.getEntry(field1)) && isEmpty(a.getEntry(field2)));
    }
  }

  static {
    ActField tmpLastName1 = null;
    ActField tmpFirstName1 = null;
    ActField tmpLastName2 = null;
    ActField tmpFirstName2 = null;
    ActField tmpLastName3 = null;
    ActField tmpFirstName3 = null;
    ActField tmpLastNameC = null;
    ActField tmpFirstNameC = null;
    SexField tmpSexField = null;

    try {
      fields = new FieldLayout(new FieldLayoutElement[] {
	  new NumericField("JOUR", 2, 0, 31),
	  new NumericField("MOIS", 2, 0, 12),
	  new NumericField("ANNEE", 4, 1500, 2020),
	  new FieldLayout("Renseignements concernant le d\351funt",
			  new FieldLayoutElement[] {
			    tmpLastName1 = new LastNameField("NOM1", Sex.UNKNOWN),
			    tmpFirstName1 = new FirstNameField("PRN1"),
			    tmpSexField = new SexField("SEX1"),
			    new ActField(new CharField("AGE1", 13)),
			    new ActField(new CharField("NOT1", 40)),
			  }),
	  new FieldLayout("P\350re du d\351funt",
			  new FieldLayoutElement[] {
			    tmpLastName2 = new LastNameField("NOM2", Sex.MALE, tmpLastName1),
			    tmpFirstName2 = new FirstNameField("PRN2"),
			    new ActField(new CharField("NOT2", 40)),
			  }),
	  new FieldLayout("M\350re du d\351funt",
			  new FieldLayoutElement[] {
			    tmpLastName3 = new LastNameField("NOM3", Sex.FEMALE),
			    tmpFirstName3 = new FirstNameField("PRN3"),
			    new ActField(new CharField("NOT3", 40)),
			  }),
	  new FieldLayout("Conjoint du d\351funt",
			  new FieldLayoutElement[] {
			    tmpLastNameC = new LastNameField("NOMC", Sex.UNKNOWN),
			    tmpFirstNameC = new FirstNameField("PRNC"),
			    new ActField(new CharField("NOTC", 40)),
			  }),
	  // the following part gives me a freaking headache.
	  new FieldLayout("Noms/Pr\351noms conventionnels",
			  new FieldLayoutElement[] {
			    new ConditionalField(new CharField("NOM2CV", 20),
						 new AdultTest(tmpLastName2, tmpLastName3, tmpLastNameC,
							       tmpFirstName2, tmpFirstName3, tmpFirstNameC),
						 new ConvNameField("NOM2CV", Conventionalizer.CONV_LAST_NAME,
								   tmpSexField, tmpLastName1, tmpLastNameC, null, Sex.MALE),
						 new ConditionalField(new CharField("NOM2CV", 20),
								      new NonEmptyTest(tmpLastName2, tmpFirstName2),
								      new ConvNameField("NOM2CV", Conventionalizer.CONV_LAST_NAME,
											Sex.MALE, tmpLastName2),
								      new ConvNameField("NOM2CV", Conventionalizer.CONV_LAST_NAME,
											Sex.MALE, tmpLastName1))),
			    new ConditionalField(new CharField("PRN2CV", 20),
						 new AdultTest(tmpLastName2, tmpLastName3, tmpLastNameC,
							       tmpFirstName2, tmpFirstName3, tmpFirstNameC),
						 new ConvNameField("PRN2CV", Conventionalizer.CONV_FIRST_NAME,
								   tmpSexField, tmpFirstName1, tmpFirstNameC, null, Sex.MALE),
						 new ConvNameField("PRN2CV", Conventionalizer.CONV_FIRST_NAME,
								   Sex.MALE, tmpFirstName2)),
			    new ConditionalField(new CharField("NOM3CV", 20),
						 new AdultTest(tmpLastName2, tmpLastName3, tmpLastNameC,
							       tmpFirstName2, tmpFirstName3, tmpFirstNameC),
						 new ConvNameField("NOM3CV", Conventionalizer.CONV_LAST_NAME,
								   tmpSexField, tmpLastNameC, tmpLastName1, null, Sex.FEMALE),
						 new ConvNameField("NOM3CV", Conventionalizer.CONV_LAST_NAME,
								   Sex.FEMALE, tmpLastName3, "?")),
			    new ConditionalField(new CharField("PRN3CV", 20),
						 new AdultTest(tmpLastName2, tmpLastName3, tmpLastNameC,
							       tmpFirstName2, tmpFirstName3, tmpFirstNameC),
						 new ConvNameField("PRN3CV", Conventionalizer.CONV_FIRST_NAME,
								   tmpSexField, tmpFirstNameC, tmpFirstName1, null, Sex.FEMALE),
						 new ConvNameField("PRN3CV", Conventionalizer.CONV_FIRST_NAME,
								   Sex.FEMALE, tmpFirstName3, "?")),
			  }),
	  new FieldLayout("Renseignement divers concernant l'acte",
			  new FieldLayoutElement[] {
			    new ActField(new CharField("DIVERS", 40)),
			    new LocalityField("LOC1ID"),
			    new ActField(new MemoField("TXTLONG"))
			  }),
	});
    } catch (xBaseJException e) {
      System.err.println("Can't instanciate field prototypes because : " + e.toString());
      e.printStackTrace();
      System.exit(1);
    } catch (IOException e) {
      System.err.println("Can't instanciate field prototypes because : " + e.toString());
      e.printStackTrace();
      System.exit(1);
    }
  }

  public SepulchreListFactory(Model m, File projectDir)
      throws java.io.FileNotFoundException {
    super(m, Util.getFile(projectDir, "sepultu5.dbf"), fields);
  }

  public String toString() {
    return "S\351pultures";
  }
}
