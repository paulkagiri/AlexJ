package sairepa.model.structs;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;

import sairepa.model.*;
import sairepa.model.fields.*;

public class DeceaseListFactory extends ActListFactory
{
  public static FieldLayout fields = SepulchreListFactory.fields;

  public DeceaseListFactory(Model m, File projectDir)
      throws java.io.FileNotFoundException {
      super(m, Util.getFile(projectDir, "deces__5.dbf"), Util.getFile(projectDir, "deces__5.dbt"), fields);
  }

  public String toString() {
    return "D\351c\350s";
  }
}
