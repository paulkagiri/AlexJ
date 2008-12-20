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

public class BaptismListFactory extends ActListFactory
{
  public static FieldLayout fields = null;

  static {
    ActField tmpLastName1;
    ActField tmpLastName2;
    ActField tmpFirstName2;
    ActField tmpLastName3;
    ActField tmpFirstName3;

    try {
      fields = new FieldLayout(new FieldLayoutElement[] {
	  new NumericField("JOUR", 2, 0, 31),
	  new NumericField("MOIS", 2, 0, 12),
	  new NumericField("ANNEE", 4, 1500, 2020),
	  new FieldLayout("Renseignements concernant le baptis\351",
			  new FieldLayoutElement[] {
			    tmpLastName1 = new LastNameField("NOM1", Sex.UNKNOWN),
			    new ActField(new CharField("PRN1", 23)),
			    new SexField("SEX1"),
			    new ActField(new CharField("NEE", 7)),
			    new ActField(new CharField("NOT1", 40)),
	    }),
	  new FieldLayout("Informations concernant le p\350re du baptis\351",
			  new FieldLayoutElement[] {
			    tmpLastName2 = new LastNameField("NOM2", Sex.MALE, tmpLastName1),
			    new ConvLastNameField("NOM2CV", Sex.MALE, tmpLastName2),
			    tmpFirstName2 = new ActField(new CharField("PRN2", 23)),
			    new ConvFirstNameField("PRN2CV", Sex.MALE, tmpFirstName2),
			    new ActField(new CharField("NOT2", 40)),
			  }),
	  new FieldLayout("Informations concernant la m\350re du baptis\351",
			  new FieldLayoutElement[] {
			    tmpLastName3 = new LastNameField("NOM3", Sex.FEMALE),
			    new ConvLastNameField("NOM3CV", Sex.FEMALE, tmpLastName3) ,
			    tmpFirstName3 = new ActField(new CharField("PRN3", 23)),
			    new ConvFirstNameField("PRN3CV", Sex.FEMALE, tmpFirstName3),
			    new ActField(new CharField("NOT3", 40)),
			  }),
	  new FieldLayout("Informations concernant le parrain",
			  new FieldLayoutElement[] {
			    new LastNameField("NOMP", Sex.MALE),
			    new ActField(new CharField("PRNP", 23)),
			    new ActField(new CharField("NOTP", 40)),
			  }),
	  new FieldLayout("Informations concernant la marraine",
			  new FieldLayoutElement[] {
			    new LastNameField("NOMM", Sex.FEMALE),
			    new ActField(new CharField("PRNM", 23)),
			    new ActField(new CharField("NOTM", 40)),
			  }),
	  new FieldLayout("Renseignements divers concernant l'acte",
			  new FieldLayoutElement[] {
			    new ActField(new CharField("DIVERS", 40)),
			    new ActField(new CharField("LOC2ID", 38)),
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

  public BaptismListFactory(File projectDir)
      throws java.io.FileNotFoundException {
    this(projectDir, "bapteme5.dbf");
  }

  protected BaptismListFactory(File projectDir, String filename)
      throws java.io.FileNotFoundException {
    super(Util.getFile(projectDir, filename), fields);
  }

  public String toString() {
    return "Bapt\352mes";
  }
}
