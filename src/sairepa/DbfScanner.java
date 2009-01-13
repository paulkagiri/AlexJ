package sairepa;

import org.xBaseJ.DBF;
import org.xBaseJ.xBaseJException;

import org.xBaseJ.fields.CharField;
import org.xBaseJ.fields.DateField;
import org.xBaseJ.fields.Field;
import org.xBaseJ.fields.FloatField;
import org.xBaseJ.fields.LogicalField;
import org.xBaseJ.fields.MemoField;
import org.xBaseJ.fields.NumField;
import org.xBaseJ.fields.PictureField;

/**
 * Simple main() class/function to scan quickly a dbf file
 */
public class DbfScanner
{
    public static void main(String args[]) throws Exception
    {
      DBF dbfFile = new DBF(args[0], DBF.READ_ONLY, "CP850");

      try {
	for (int i = 1 ; i <= dbfFile.getFieldCount() ; i++) {
	  Field field = dbfFile.getField(i);

	  System.out.println("====================");
	  System.out.println("Name: " + field.getName());
	  System.out.println("Type: " + field.getClass().getName());
	  System.out.println("Length: " + field.Length);

	  System.out.println("");
	}

	if (args.length >= 2) {
	  while(true) {
	    dbfFile.read();
	    System.out.println("\n=====================");
	    for (int i = 1 ; i <= dbfFile.getFieldCount() ; i++) {
	    Field field = dbfFile.getField(i);
	    System.out.println(field.getName() + " : " + field.get());
	    }
	  }
	}
      } finally {
	dbfFile.close();
      }
    }
}
