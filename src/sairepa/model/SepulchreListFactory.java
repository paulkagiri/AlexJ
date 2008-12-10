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

  static {
    ActField tmpLastName1;

    try {
      fields = new FieldLayout(new FieldLayoutElement[] {
	  new NumericField("JOUR", 2, 1, 31),
	  new NumericField("MOIS", 2, 1, 12),
	  new NumericField("ANNEE", 4),
	  new FieldLayout("Renseignements concernant le d\351funt",
			  new FieldLayoutElement[] {
			    tmpLastName1 = new LastNameField("NOM1"),
			    new ActField(new CharField("PRN1", 23)),
			    new SexField("SEX1"),
			    new ActField(new CharField("AGE1", 13)),
			    new ActField(new CharField("NOT1", 40)),
			  }),
	  new FieldLayout("P\350re du d\351funt",
			  new FieldLayoutElement[] {
			    new LastNameField("NOM2", tmpLastName1),
			    new ActField(new CharField("NOM2CV", 20)),
			    new ActField(new CharField("PRN2", 23)),
			    new ActField(new CharField("PRN2CV", 8)),
			    new ActField(new CharField("NOT2", 40)),
			  }),
	  new FieldLayout("M\350re du d\351funt",
			  new FieldLayoutElement[] {
			    new LastNameField("NOM3"),
			    new ActField(new CharField("NOM3CV", 20)),
			    new ActField(new CharField("PRN3", 23)),
			    new ActField(new CharField("PRN3CV", 8)),
			    new ActField(new CharField("NOT3", 40)),
			  }),
	  new FieldLayout("Conjoint du d\351funt",
			  new FieldLayoutElement[] {
			    new LastNameField("NOMC"),
			    new ActField(new CharField("PRNC", 23)),
			    new ActField(new CharField("NOTC", 40)),
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
