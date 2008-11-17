package sairepa.model;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;

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
    try {
      fields = new FieldLayout(new FieldLayoutElement[] {
	  new ActField(new NumField("JOUR", 2, 0)),
	  new ActField(new NumField("MOIS", 2, 0)),
	  new ActField(new NumField("ANNEE", 4, 0)),
	  new FieldLayout("Renseignements concernant le defunt",
			  new FieldLayoutElement[] {
			    new ActField(new CharField("NOM1", 20)),
			    new ActField(new CharField("PRN1", 23)),
			    new ActField(new CharField("SEX1", 1)),
			    new ActField(new CharField("AGE1", 13)),
			    new ActField(new CharField("NOT1", 40)),
			  }),
	  new FieldLayout("Pere du defunt",
			  new FieldLayoutElement[] {
			    new ActField(new CharField("NOM2", 20)),
			    new ActField(new CharField("NOM2CV", 20)),
			    new ActField(new CharField("PRN2", 23)),
			    new ActField(new CharField("PRN2CV", 8)),
			    new ActField(new CharField("NOT2", 40)),
			  }),
	  new FieldLayout("Mere du defunt",
			  new FieldLayoutElement[] {
			    new ActField(new CharField("NOM3", 20)),
			    new ActField(new CharField("NOM3CV", 20)),
			    new ActField(new CharField("PRN3", 23)),
			    new ActField(new CharField("PRN3CV", 8)),
			    new ActField(new CharField("NOT3", 40)),
			  }),
	  new FieldLayout("Conjoint du defunt",
			  new FieldLayoutElement[] {
			    new ActField(new CharField("NOMC", 20)),
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
