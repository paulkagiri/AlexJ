package sairepa.model.structs;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;

import sairepa.model.*;
import sairepa.model.fields.*;

public class ConfirmationListFactory extends ActListFactory
{
  public static FieldLayout fields = BaptismListFactory.fields;

    protected ConfirmationListFactory(Model m, File projectDir, String dbf, String dbt)
      throws java.io.FileNotFoundException {
	super(m, Util.getFile(projectDir, dbf), Util.getFile(projectDir, dbt), fields);
  }

  public ConfirmationListFactory(Model m, File projectDir)
      throws java.io.FileNotFoundException {
      this(m, projectDir, "confirm5.dbf", "confirm5.dbt");
  }

  public String toString() {
    return "Confirmations";
  }
}
