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

/**
 * Exactly the same than BaptismListFactory except the filename is different
 */
public class ConfirmationListFactory extends ActListFactory
{
  public static FieldLayout fields = null;

  static {
    ActField tmpLastName1;

    try {
      fields = new FieldLayout(new FieldLayoutElement[] {
	  new NumericField("JOUR", 2, 0, 31),
	  new NumericField("MOIS", 2, 0, 12),
	  new NumericField("ANNEE", 4, 1500, 2020),
	  new FieldLayout("Renseignements concernant le confirm\351",
			  new FieldLayoutElement[] {
			    tmpLastName1 = new LastNameField("NOM1", Sex.UNKNOWN),
			    new ActField(new CharField("PRN1", 23)),
			    new SexField("SEX1"),
			    new ActField(new CharField("NEE", 7)),
			    new ActField(new CharField("NOT1", 40)),
	    }),
	  new FieldLayout("Informations concernant le p\350re du confirm\351",
			  new FieldLayoutElement[] {
			    new LastNameField("NOM2", Sex.MALE, tmpLastName1),
			    new ActField(new CharField("NOM2CV", 20)),
			    new ActField(new CharField("PRN2", 23)),
			    new ActField(new CharField("PRN2CV", 8)),
			    new ActField(new CharField("NOT2", 40)),
			  }),
	  new FieldLayout("Informations concernant la m\350re du confirm\351",
			  new FieldLayoutElement[] {
			    new LastNameField("NOM3", Sex.FEMALE),
			    new ActField(new CharField("NOM3CV", 20)),
			    new ActField(new CharField("PRN3", 23)),
			    new ActField(new CharField("PRN3CV", 8)),
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

  protected ConfirmationListFactory(File projectDir, String filename)
      throws java.io.FileNotFoundException {
    super(Util.getFile(projectDir, filename), fields);
  }

  public ConfirmationListFactory(File projectDir)
      throws java.io.FileNotFoundException {
    this(projectDir, "confirm5.dbf");
  }

  public String toString() {
    return "Confirmations";
  }
}
