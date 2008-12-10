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
    try {
      fields = new FieldLayout(new FieldLayoutElement[] {
	  new ActField(new NumField("JOUR", 2, 0)),
	  new ActField(new NumField("MOIS", 2, 0)),
	  new ActField(new NumField("ANNEE", 4, 0)),
	  new FieldLayout("Renseignement concernant l'\351poux",
			  new FieldLayoutElement[] {
			    new ActField(new CharField("NOM2", 20)),
			    new ActField(new CharField("NOM2CV", 20)),
			    new ActField(new CharField("PRN2", 23)),
			    new ActField(new CharField("PRN2CV", 8)),
			    new ActField(new CharField("LOC2", 50)),
			    new ActField(new CharField("LOC2ID", 38)),
			    new ActField(new CharField("CVF2", 1)),
			    new ActField(new CharField("NOT2", 40)),
			    new FieldLayout("P\350re de l'\351poux",
					    new FieldLayoutElement[] {
					      new ActField(new CharField("NOM4", 20)),
					      new ActField(new CharField("PRN4", 23)),
					      new ActField(new CharField("NOT4", 40)),
					    }),
			    new FieldLayout("M\350re de l'\351poux",
					    new FieldLayoutElement[] {
					      new ActField(new CharField("NOM5", 20)),
					      new ActField(new CharField("PRN5", 23)),
					    })
			  }),
	  new FieldLayout("Renseignement concernant l'\351pouse",
			  new FieldLayoutElement[] {
			    new ActField(new CharField("NOM3", 20)),
			    new ActField(new CharField("NOM3CV", 20)),
			    new ActField(new CharField("PRN3", 23)),
			    new ActField(new CharField("PRN3CV", 8)),
			    new ActField(new CharField("LOC3", 50)),
			    new ActField(new CharField("LOC3ID", 38)),
			    new ActField(new CharField("CVF3", 1)),
			    new ActField(new CharField("NOT3", 40)),
			    new FieldLayout("P\350re de l'\351pouse",
					    new FieldLayoutElement[] {
					      new ActField(new CharField("NOM6", 20)),
					      new ActField(new CharField("PRN6", 23)),
					      new ActField(new CharField("NOT6", 40)),
					    }),
			    new FieldLayout("M\350re de l'\351pouse",
					    new FieldLayoutElement[] {
					      new ActField(new CharField("NOM7", 20)),
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
