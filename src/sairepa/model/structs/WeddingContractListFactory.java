package sairepa.model.structs;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;

import sairepa.model.*;
import sairepa.model.fields.*;

public class WeddingContractListFactory extends ActListFactory
{
  public static FieldLayout fields = WeddingListFactory.fields;

  public WeddingContractListFactory(Model m, File projectDir)
      throws java.io.FileNotFoundException {
      super(m, Util.getFile(projectDir, "ctt_mar5.dbf"), Util.getFile(projectDir, "ctt_mar5.dbt"), fields);
  }

  public String toString() {
    return "Contrat mariages";
  }
}
