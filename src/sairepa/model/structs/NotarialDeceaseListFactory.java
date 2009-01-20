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

public class NotarialDeceaseListFactory extends ActListFactory
{
  public static FieldLayout fields = SepulchreListFactory.fields;

  public NotarialDeceaseListFactory(Model m, File projectDir)
      throws java.io.FileNotFoundException {
    super(m, Util.getFile(projectDir, "dec_inv5.dbf"), fields);
  }

  public String toString() {
    return "Inv. apr. D\351c\350s";
  }
}
