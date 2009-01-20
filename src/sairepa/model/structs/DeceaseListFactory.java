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

public class DeceaseListFactory extends ActListFactory
{
  public static FieldLayout fields = SepulchreListFactory.fields;

  public DeceaseListFactory(Model m, File projectDir)
      throws java.io.FileNotFoundException {
    super(m, Util.getFile(projectDir, "deces__5.dbf"), fields);
  }

  public String toString() {
    return "D\351c\350s";
  }
}
