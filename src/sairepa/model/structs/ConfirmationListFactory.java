package sairepa.model.structs;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;

import sairepa.model.*;
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
 * if you modify this class, modify also: BaptismListFactory, BirthListFactory, NotarialConfirmationListFactory
 */
public class ConfirmationListFactory extends ActListFactory
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
	  new FieldLayout("Renseignements concernant le confirm\351",
			  new FieldLayoutElement[] {
			    tmpLastName1 = new LastNameField("NOM1", Sex.UNKNOWN),
			    new FirstNameField("PRN1"),
			    new SexField("SEX1"),
			    new ActField(new CharField("NEE", 7)),
			    new ActField(new CharField("NOT1", 40)),
	    }),
	  new FieldLayout("Informations concernant le p\350re du confirm\351",
			  new FieldLayoutElement[] {
			    tmpLastName2 = new LastNameField("NOM2", Sex.MALE, tmpLastName1),
			    new ConvNameField("NOM2CV", Conventionalizer.CONV_LAST_NAME,
					      Sex.MALE, tmpLastName2),
			    tmpFirstName2 = new FirstNameField("PRN2"),
			    new ConvNameField("PRN2CV", Conventionalizer.CONV_FIRST_NAME,
					      Sex.MALE, tmpFirstName2),
			    new ActField(new CharField("NOT2", 40)),
			  }),
	  new FieldLayout("Informations concernant la m\350re du confirm\351",
			  new FieldLayoutElement[] {
			    tmpLastName3 = new LastNameField("NOM3", Sex.FEMALE),
			    new ConvNameField("NOM3CV", Conventionalizer.CONV_LAST_NAME,
					      Sex.FEMALE, tmpLastName3) ,
			    tmpFirstName3 = new FirstNameField("PRN3"),
			    new ConvNameField("PRN3CV", Conventionalizer.CONV_FIRST_NAME,
					      Sex.FEMALE, tmpLastName3),
			    new ActField(new CharField("NOT3", 40)),
			  }),
	  new FieldLayout("Informations concernant le parrain",
			  new FieldLayoutElement[] {
			    new LastNameField("NOMP", Sex.MALE),
			    new FirstNameField("PRNP"),
			    new ActField(new CharField("NOTP", 40)),
			  }),
	  new FieldLayout("Informations concernant la marraine",
			  new FieldLayoutElement[] {
			    new LastNameField("NOMM", Sex.FEMALE),
			    new FirstNameField("PRNM"),
			    new ActField(new CharField("NOTM", 40)),
			  }),
	  new FieldLayout("Renseignements divers concernant l'acte",
			  new FieldLayoutElement[] {
			    new ActField(new CharField("DIVERS", 40)),
			    new LocalityField("LOC2ID"),
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

  protected ConfirmationListFactory(Model m, File projectDir, String filename)
      throws java.io.FileNotFoundException {
    super(m, Util.getFile(projectDir, filename), fields);
  }

  public ConfirmationListFactory(Model m, File projectDir)
      throws java.io.FileNotFoundException {
    this(m, projectDir, "confirm5.dbf");
  }

  public String toString() {
    return "Confirmations";
  }
}
