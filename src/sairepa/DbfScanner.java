package sairepa;

import org.xBaseJ.micro.DBF;
import org.xBaseJ.micro.xBaseJException;

import org.xBaseJ.micro.fields.CharField;
import org.xBaseJ.micro.fields.DateField;
import org.xBaseJ.micro.fields.Field;
import org.xBaseJ.micro.fields.FloatField;
import org.xBaseJ.micro.fields.LogicalField;
import org.xBaseJ.micro.fields.MemoField;
import org.xBaseJ.micro.fields.NumField;
import org.xBaseJ.micro.fields.PictureField;

public class DbfScanner
{
    public static void main(String args[]) throws Exception
    {
      DBF dbfFile = new DBF(args[0], DBF.READ_ONLY);

      for (int i = 1 ; i <= dbfFile.getFieldCount() ; i++) {
	Field field = dbfFile.getField(i);

	System.out.println("====================");
	System.out.println("Name: " + field.getName());
	System.out.println("Type: " + field.getClass().getName());
	System.out.println("Length: " + field.Length);

	if (field instanceof NumField) {
	  System.out.println("DecPosition: " + ((NumField)field).decPosition);
	}

	System.out.println("");
      }

      dbfFile.close();
    }
}
