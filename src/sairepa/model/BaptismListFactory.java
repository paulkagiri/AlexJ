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

public class BaptismListFactory extends ActListFactory
{
  public static ActField[] fields = null;

  static {
    try {
      fields = new ActField[] {
	new ActField(new NumField("JOUR", 2, 0)),
	new ActField(new NumField("MOIS", 2, 0)),
	new ActField(new NumField("ANNEE", 4, 0)),
	new ActField(new CharField("PRN1", 23)),
	new ActField(new CharField("NOM1", 20)),
	new ActField(new CharField("NEE", 7)),
	new ActField(new CharField("SEX1", 1)),
	new ActField(new CharField("NOT1", 40)),
	new ActField(new CharField("PRN2", 23)),
	new ActField(new CharField("NOM2", 20)),
	new ActField(new CharField("NOT2", 40)),
	new ActField(new CharField("PRN3", 23)),
	new ActField(new CharField("NOM3", 20)),
	new ActField(new CharField("NOT3", 40)),
	new ActField(new CharField("PRNP", 23)),
	new ActField(new CharField("NOMP", 20)),
	new ActField(new CharField("NOTP", 40)),
	new ActField(new CharField("PRNM", 23)),
	new ActField(new CharField("NOMM", 20)),
	new ActField(new CharField("NOTM", 40)),
	new ActField(new CharField("DIVERS", 40)),
	new ActField(new CharField("LOC2ID", 38)),
	new ActField(new CharField("PRN2CV", 8)),
	new ActField(new CharField("NOM2CV", 20)),
	new ActField(new CharField("PRN3CV", 8)),
	new ActField(new CharField("NOM3CV", 20)),
	new ActField(new MemoField("TXTLONG"))
      };
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

  public BaptismListFactory(Connection db, File projectDir)
      throws java.io.FileNotFoundException {
    super(db, Util.getFile(projectDir, "bapteme5.dbf"), fields);
  }
}