/*
** TextInjector.java
** Login : <jflesch@zeus.kwain.net>
** Started on  Sun Feb 22 16:38:28 2009 Jerome Flesch
*/
package sairepa.gui;

import javax.swing.JTable;
import javax.swing.text.JTextComponent;

import sairepa.model.Util;

public interface TextInjector
{
  public void inject(String txt);

  public static class FieldTextInjector implements TextInjector {
    public JTextComponent field;

    public FieldTextInjector(JTextComponent field) {
      this.field = field;
    }
    public void inject(String txt) {
      field.replaceSelection(txt);
    }
  }

  public static class TableTextInjector implements TextInjector {
    public JTable table;
    public TableTextInjector(JTable table) { this.table = table; }
    public void inject(String txt) {
      int[] rows = table.getSelectedRows();
      int[] cols = table.getSelectedColumns();

      String[] stringRows = txt.split(TextExtractor.TABLE_LINE_SEPARATOR_BACKQUOTED);
      String[][] stringTable = new String[stringRows.length][];

      for (int i = 0 ; i < stringRows.length ; i++)
	  stringTable[i] = stringRows[i].split(TextExtractor.TABLE_CELL_SEPARATOR_BACKQUOTED);

      for (int i = 0 ; i < rows.length ; i++)
	{
	  for (int j = 0 ; j < cols.length ; j++)
	    {
	      if (i < stringTable.length && j < stringTable[i].length)
		{
		  Util.check(stringTable[i] != null);
		  Util.check(stringTable[i][j] != null);
		  String value = stringTable[i][j];
		  table.setValueAt(value, rows[i], cols[j]);
		}
	    }
	}
    }
  }
}
