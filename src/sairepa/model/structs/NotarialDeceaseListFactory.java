package sairepa.model.structs;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;

import sairepa.model.*;
import sairepa.model.fields.*;

public class NotarialDeceaseListFactory extends ActListFactory
{
  public static FieldLayout fields = SepulchreListFactory.fields;

  public NotarialDeceaseListFactory(Model m, File projectDir)
      throws java.io.FileNotFoundException {
      super(m, Util.getFile(projectDir, "dec_inv5.dbf"), Util.getFile(projectDir, "dec_inv5.dbt"), fields);
  }

  public String toString() {
    return "Inv. apr. D\351c\350s";
  }
}
