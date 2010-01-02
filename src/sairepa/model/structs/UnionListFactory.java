package sairepa.model.structs;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;

import sairepa.model.*;
import sairepa.model.fields.*;

public class UnionListFactory extends ActListFactory
{
  public static FieldLayout fields = WeddingListFactory.fields;

  public UnionListFactory(Model m, File projectDir)
      throws java.io.FileNotFoundException {
      super(m, Util.getFile(projectDir, "union__5.dbf"), Util.getFile(projectDir, "union__5.dbt"), fields);
  }

  public String toString() {
    return "Mariages";
  }
}
