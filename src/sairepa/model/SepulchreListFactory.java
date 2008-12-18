package sairepa.model;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;

import sairepa.model.fields.*;

import org.xBaseJ.micro.xBaseJException;
import org.xBaseJ.micro.fields.CharField;
import org.xBaseJ.micro.fields.DateField;
import org.xBaseJ.micro.fields.FloatField;
import org.xBaseJ.micro.fields.LogicalField;
import org.xBaseJ.micro.fields.MemoField;
import org.xBaseJ.micro.fields.NumField;
import org.xBaseJ.micro.fields.PictureField;

public class SepulchreListFactory extends ActListFactory
{
  public static FieldLayout fields = null;

  private static class AdultTest implements Test {
    private ActField name2;
    private ActField name3;
    private ActField namec;

    public AdultTest(ActField name2, ActField name3, ActField namec) {
      this.name2 = name2;
      this.name3 = name3;
      this.namec = namec;
    }

    private boolean isEmpty(ActEntry e) {
      return ("".equals(e.getValue().trim()) || "-".equals(e.getValue().trim()));
    }

    public boolean test(Act a) {
      ActEntry name2e = a.getEntry(name2);
      ActEntry name3e = a.getEntry(name3);
      ActEntry namece = a.getEntry(namec);

      return ( (isEmpty(name2e) && isEmpty(name3e)) || !isEmpty(namece) );
    }
  }

  static {
    ActField tmpLastName1;
    ActField tmpLastName2;
    ActField tmpFirstName2;
    ActField tmpLastName3;
    ActField tmpFirstName3;
    ActField tmpLastNameC;
    SexField tmpSexField;

    try {
      fields = new FieldLayout(new FieldLayoutElement[] {
	  new NumericField("JOUR", 2, 0, 31),
	  new NumericField("MOIS", 2, 0, 12),
	  new NumericField("ANNEE", 4, 1500, 2020),
	  new FieldLayout("Renseignements concernant le d\351funt",
			  new FieldLayoutElement[] {
			    tmpLastName1 = new LastNameField("NOM1", Sex.UNKNOWN),
			    new ActField(new CharField("PRN1", 23)),
			    tmpSexField = new SexField("SEX1"),
			    new ActField(new CharField("AGE1", 13)),
			    new ActField(new CharField("NOT1", 40)),
			  }),
	  new FieldLayout("P\350re du d\351funt",
			  new FieldLayoutElement[] {
			    tmpLastName2 = new LastNameField("NOM2", Sex.MALE, tmpLastName1),
			    tmpFirstName2 = new ActField(new CharField("PRN2", 23)),
			    new ActField(new CharField("NOT2", 40)),
			  }),
	  new FieldLayout("M\350re du d\351funt",
			  new FieldLayoutElement[] {
			    tmpLastName3 = new LastNameField("NOM3", Sex.FEMALE),
			    tmpFirstName3 = new ActField(new CharField("PRN3", 23)),
			    new ActField(new CharField("NOT3", 40)),
			  }),
	  new FieldLayout("Conjoint du d\351funt",
			  new FieldLayoutElement[] {
			    tmpLastNameC = new LastNameField("NOMC", Sex.UNKNOWN),
			    new ActField(new CharField("PRNC", 23)),
			    new ActField(new CharField("NOTC", 40)),
			  }),
	  new FieldLayout("Prenoms/Noms conventionnels",
			  new FieldLayoutElement[] {
			    new ConditionalField(new CharField("NOM2CV", 20),
						 new AdultTest(tmpLastName2, tmpLastName3, tmpLastNameC),
						 new ConvLastNameField("NOM2CV", tmpSexField,
								       tmpLastName1, tmpLastNameC, null),
						 new ConvLastNameField("NOM2CV", Sex.UNKNOWN, tmpLastName2)),
			    new ConvFirstNameField("PRN2CV", Sex.MALE, tmpFirstName2),
			    new ConditionalField(new CharField("NOM3CV", 20),
						 new AdultTest(tmpLastName2, tmpLastName3, tmpLastNameC),
						 new ConvLastNameField("NOM3CV", tmpSexField,
								       tmpLastNameC, tmpLastName1, null),
						 new ConvLastNameField("NOM3CV", Sex.UNKNOWN, tmpLastName3)),
			    new ConvFirstNameField("PRN3CV", Sex.FEMALE, tmpFirstName3),
			  }),
	  new FieldLayout("Renseignement divers concernant l'acte",
			  new FieldLayoutElement[] {
			    new ActField(new CharField("DIVERS", 40)),
			    new ActField(new CharField("LOC1ID", 38)),
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

  public SepulchreListFactory(File projectDir)
      throws java.io.FileNotFoundException {
    super(Util.getFile(projectDir, "sepultu5.dbf"), fields);
  }

  public String toString() {
    return "Sepultures";
  }
}
