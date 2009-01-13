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
 * if you modify this class, modify also: SepultureListFactory, NotarialDeceaseListFactory
 */
public class DeceaseListFactory extends ActListFactory
{
  public static FieldLayout fields = null;

  private static boolean isEmpty(ActEntry e) {
      return ("".equals(e.getValue().trim()) || "-".equals(e.getValue().trim()));
  }

  private static class AdultTest implements Test {
    private ActField name2;
    private ActField name3;
    private ActField namec;

    public AdultTest(ActField name2, ActField name3, ActField namec) {
      this.name2 = name2;
      this.name3 = name3;
      this.namec = namec;
    }

    public boolean test(Act a) {
      ActEntry name2e = a.getEntry(name2);
      ActEntry name3e = a.getEntry(name3);
      ActEntry namece = a.getEntry(namec);

      return ( (isEmpty(name2e) && isEmpty(name3e)) || !isEmpty(namece) );
    }
  }

  private static class NonEmptyTest implements Test {
    private ActField field;

    public NonEmptyTest(ActField field) {
      this.field = field;
    }

    public boolean test(Act a) {
      return !isEmpty(a.getEntry(field));
    }
  }

  static {
    ActField tmpLastName1;
    ActField tmpFirstName1;
    ActField tmpLastName2;
    ActField tmpFirstName2;
    ActField tmpLastName3;
    ActField tmpFirstName3;
    ActField tmpLastNameC;
    ActField tmpFirstNameC;
    SexField tmpSexField;

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
						 new AdultTest(tmpLastName2, tmpLastName3, tmpLastNameC),
						 new ConvNameField("NOM2CV", Conventionalizer.CONV_LAST_NAME,
								   tmpSexField, tmpLastName1, tmpLastNameC, null),
						 new ConditionalField(new CharField("NOM2CV", 20),
								      new NonEmptyTest(tmpLastName2),
								      new ConvNameField("NOM2CV", Conventionalizer.CONV_LAST_NAME,
											Sex.MALE, tmpLastName2),
								      new ConvNameField("NOM2CV", Conventionalizer.CONV_LAST_NAME,
											Sex.MALE, tmpLastName1))),
			    new ConditionalField(new CharField("PRN2CV", 20),
						 new AdultTest(tmpFirstName2, tmpFirstName3, tmpFirstNameC),
						 new ConvNameField("PRN2CV", Conventionalizer.CONV_FIRST_NAME,
								   tmpSexField, tmpFirstName1, tmpFirstNameC, null),
						 new ConvNameField("PRN2CV", Conventionalizer.CONV_FIRST_NAME,
								   Sex.MALE, tmpFirstName2)),
			    new ConditionalField(new CharField("NOM3CV", 20),
						 new AdultTest(tmpLastName2, tmpLastName3, tmpLastNameC),
						 new ConvNameField("NOM3CV", Conventionalizer.CONV_LAST_NAME,
								   tmpSexField, tmpLastNameC, tmpLastName1, null),
						 new ConvNameField("NOM3CV", Conventionalizer.CONV_LAST_NAME,
								   Sex.FEMALE, tmpLastName3, "?")),
			    new ConditionalField(new CharField("PRN3CV", 20),
						 new AdultTest(tmpFirstName2, tmpFirstName3, tmpFirstNameC),
						 new ConvNameField("PRN3CV", Conventionalizer.CONV_FIRST_NAME,
								   tmpSexField, tmpFirstNameC, tmpFirstName1, null),
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

  public DeceaseListFactory(Model m, File projectDir)
      throws java.io.FileNotFoundException {
    super(m, Util.getFile(projectDir, "deces__5.dbf"), fields);
  }

  public String toString() {
    return "D\351c\350s";
  }
}
