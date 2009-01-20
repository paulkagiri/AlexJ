package sairepa.model.structs;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;

import sairepa.model.*;
import sairepa.model.fields.*;

import org.xBaseJ.xBaseJException;
import org.xBaseJ.fields.CharField;
import org.xBaseJ.fields.DateField;
import org.xBaseJ.fields.FloatField;
import org.xBaseJ.fields.LogicalField;
import org.xBaseJ.fields.MemoField;
import org.xBaseJ.fields.NumField;
import org.xBaseJ.fields.PictureField;

public class ConfirmationListFactory extends ActListFactory
{
  public static FieldLayout fields = BaptismListFactory.fields;

  protected ConfirmationListFactory(Model m, File projectDir, String filename)
      throws java.io.FileNotFoundException {
    super(m, Util.getFile(projectDir, filename), fields);
  }

  public ConfirmationListFactory(Model m, File projectDir)
      throws java.io.FileNotFoundException {
    this(m, projectDir, "confirm5.dbf");
  }

  public String toString() {
    return "Confirmations";
  }
}
