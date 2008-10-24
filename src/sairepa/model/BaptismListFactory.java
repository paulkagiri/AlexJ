package sairepa.model;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;

public class BaptismListFactory extends ActListFactory
{
  public final static ActField[] fields = new ActField[] {
    new ActField("JOUR"),
    new ActField("MOIS"),
    new ActField("ANNEE"),
    new ActField("PRN1"),
    new ActField("NOM1"),
    new ActField("NEE"),
    new ActField("SEX1"),
    new ActField("NOT1"),
    new ActField("PRN2"),
    new ActField("NOM2"),
    new ActField("NOT2"),
    new ActField("PRN3"),
    new ActField("NOM3"),
    new ActField("NOT3"),
    new ActField("PRNP"),
    new ActField("NOMP"),
    new ActField("NOTP"),
    new ActField("PRNM"),
    new ActField("NOMM"),
    new ActField("NOTM"),
    new ActField("DIVERS"),
    new ActField("LOC2ID"),
    new ActField("PRN2CV"),
    new ActField("NOM2CV"),
    new ActField("PRN3CV"),
    new ActField("NOM3CV")
  };

  public BaptismListFactory(Connection db, File projectDir)
      throws java.io.FileNotFoundException {
    super(db, Util.getFile(projectDir, "bapteme5.dbf"), fields);
  }
}
