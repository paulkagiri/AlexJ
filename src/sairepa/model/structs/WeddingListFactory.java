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
 * if you modify this class, modify also: UnionListFactory, WeddingContractListFactory
 */
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
			    new ConvNameField("NOM2CV", Conventionalizer.CONV_LAST_NAME,
					      Sex.MALE, tmpLastName2),
			    tmpFirstName2 = new FirstNameField("PRN2"),
			    new ConvNameField("PRN2CV", Conventionalizer.CONV_FIRST_NAME,
					      Sex.MALE, tmpFirstName2),
			    new ActField(new CharField("LOC2", 50)),
			    new LocalityField("LOC2ID"),
			    new CVField("CVF2"),
			    new ActField(new CharField("NOT2", 40)),
			    new FieldLayout("P\350re de l'\351poux",
					    new FieldLayoutElement[] {
					      new LastNameField("NOM4", Sex.MALE, tmpLastName2),
					      new FirstNameField("PRN4"),
					      new ActField(new CharField("NOT4", 40)),
					    }),
			    new FieldLayout("M\350re de l'\351poux",
					    new FieldLayoutElement[] {
					      new LastNameField("NOM5", Sex.FEMALE),
					      new FirstNameField("PRN5"),
					    })
			  }),
	  new FieldLayout("Renseignement concernant l'\351pouse",
			  new FieldLayoutElement[] {
			    tmpLastName3 = new LastNameField("NOM3", Sex.FEMALE),
			    new ConvNameField("NOM3CV", Conventionalizer.CONV_LAST_NAME,
					      Sex.FEMALE, tmpLastName3),
			    tmpFirstName3 = new FirstNameField("PRN3"),
			    new ConvNameField("PRN3CV", Conventionalizer.CONV_FIRST_NAME,
					      Sex.FEMALE, tmpFirstName3),
			    new ActField(new CharField("LOC3", 50)),
			    new ActField(new CharField("LOC3ID", 38)),
			    new CVField("CVF3"),
			    new ActField(new CharField("NOT3", 40)),
			    new FieldLayout("P\350re de l'\351pouse",
					    new FieldLayoutElement[] {
					      new LastNameField("NOM6", Sex.MALE, tmpLastName3),
					      new FirstNameField("PRN6"),
					      new ActField(new CharField("NOT6", 40)),
					    }),
			    new FieldLayout("M\350re de l'\351pouse",
					    new FieldLayoutElement[] {
					      new LastNameField("NOM7", Sex.FEMALE),
					      new FirstNameField("PRN7"),
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

  public WeddingListFactory(Model m, File projectDir)
      throws java.io.FileNotFoundException {
    super(m, Util.getFile(projectDir, "mariage5.dbf"), fields);
  }

  public String toString() {
    return "Mariages";
  }
}
