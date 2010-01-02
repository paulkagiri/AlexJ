package sairepa.model.structs;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;

import sairepa.model.*;
import sairepa.model.fields.*;

public class BirthListFactory extends ActListFactory
{
  public static FieldLayout fields = BaptismListFactory.fields;

  public BirthListFactory(Model m, File projectDir)
      throws java.io.FileNotFoundException {
      this(m, projectDir, "naissan5.dbf", "naissan5.dbt");
  }

    protected BirthListFactory(Model m, File projectDir, String dbf, String dbt)
      throws java.io.FileNotFoundException {
	super(m, Util.getFile(projectDir, dbf), Util.getFile(projectDir, dbt), fields);
  }

  public String toString() {
    return "Naissances";
  }
}
