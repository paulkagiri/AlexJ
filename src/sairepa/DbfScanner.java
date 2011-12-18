package sairepa;

import java.io.File;
import java.util.List;

import net.kwain.fxie.*;

/**
 * Simple main() class/function to scan quickly a dbf file
 */
public class DbfScanner
{
	public static void main(String args[]) throws Exception
	{
		XBaseImport imp = new XBaseImport(new File(args[0]),
				new File(args[0].replaceAll(".dbf", ".dbt")));

		try {
			for (XBaseHeader.XBaseField field : imp.getHeader().getFields()) {
				System.out.println("====================");
				System.out.println("Name: " + field.getName());
				System.out.println("Type: " + field.getFieldType().toString());
				System.out.println("Length: " + field.getLength());
				System.out.println("");
			}

			if (args.length >= 2) {
				while(true) {
					List<XBaseValue> values = imp.read();
					System.out.println("\n=====================");
					for (XBaseValue value : values) {
						System.out.println(value.getField().getName() + " : " + value.getHumanReadableValue());
					}
				}
			}
		} finally {
			imp.close();
		}
	}
}
