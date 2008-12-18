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


public class WeddingListFactory extends ActListFactory
{
  public static FieldLayout fields = null;

  static {
    ActField tmpLastName2;
    ActField tmpFirstName2;
    ActField tmpLastName3;
    ActField tmpFirstName3;

    try {
      fields = new FieldLayout(new FieldLayoutElement[] {
	  new NumericField("JOUR", 2, 0, 31),
	  new NumericField("MOIS", 2, 0, 31),
	  new NumericField("ANNEE", 4, 1500, 2020),
	  new FieldLayout("Renseignement concernant l'\351poux",
			  new FieldLayoutElement[] {
			    tmpLastName2 = new LastNameField("NOM2", Sex.MALE),
			    new ConvLastNameField("NOM2CV", Sex.MALE, tmpLastName2),
			    tmpFirstName2 = new ActField(new CharField("PRN2", 23)),
			    new ConvFirstNameField("PRN2CV", Sex.MALE, tmpFirstName2),
			    new ActField(new CharField("LOC2", 50)),
			    new ActField(new CharField("LOC2ID", 38)),
			    new ActField(new CharField("CVF2", 1)),
			    new ActField(new CharField("NOT2", 40)),
			    new FieldLayout("P\350re de l'\351poux",
					    new FieldLayoutElement[] {
					      new LastNameField("NOM4", Sex.MALE, tmpLastName2),
					      new ActField(new CharField("PRN4", 23)),
					      new ActField(new CharField("NOT4", 40)),
					    }),
			    new FieldLayout("M\350re de l'\351poux",
					    new FieldLayoutElement[] {
					      new LastNameField("NOM5", Sex.FEMALE),
					      new ActField(new CharField("PRN5", 23)),
					    })
			  }),
	  new FieldLayout("Renseignement concernant l'\351pouse",
			  new FieldLayoutElement[] {
			    tmpLastName3 = new LastNameField("NOM3", Sex.FEMALE),
			    new ConvLastNameField("NOM3CV", Sex.FEMALE, tmpLastName3),
			    tmpFirstName3 = new ActField(new CharField("PRN3", 23)),
			    new ConvFirstNameField("PRN3CV", Sex.FEMALE, tmpFirstName3),
			    new ActField(new CharField("LOC3", 50)),
			    new ActField(new CharField("LOC3ID", 38)),
			    new ActField(new CharField("CVF3", 1)),
			    new ActField(new CharField("NOT3", 40)),
			    new FieldLayout("P\350re de l'\351pouse",
					    new FieldLayoutElement[] {
					      new LastNameField("NOM6", Sex.MALE, tmpLastName3),
					      new ActField(new CharField("PRN6", 23)),
					      new ActField(new CharField("NOT6", 40)),
					    }),
			    new FieldLayout("M\350re de l'\351pouse",
					    new FieldLayoutElement[] {
					      new LastNameField("NOM7", Sex.FEMALE),
					      new ActField(new CharField("PRN7", 23)),
					    }),
			  }),
	  new FieldLayout("Renseignement divers concernant l'acte",
			  new FieldLayoutElement[] {
			    new ActField(new CharField("TEMOIN", 50)),
			    new ActField(new CharField("DIVERS", 40)),
			    new ActField(new MemoField("TXTLONG"))
			  })
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

  public WeddingListFactory(File projectDir)
      throws java.io.FileNotFoundException {
    super(Util.getFile(projectDir, "mariage5.dbf"), fields);
  }

  public String toString() {
    return "Marriages";
  }
}
