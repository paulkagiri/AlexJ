/*
** TextExtractor.java
** Login : <jflesch@zeus.kwain.net>
** Started on  Sun Feb 22 16:17:18 2009 Jerome Flesch
*/
package sairepa.gui;

import javax.swing.JTable;
import javax.swing.text.JTextComponent;

public interface TextExtractor
{
  public String getText();

  public static class FieldTextExtractor implements TextExtractor {
    public JTextComponent field;
    public FieldTextExtractor(JTextComponent field) { this.field = field; }
    public String getText() { return field.getSelectedText(); }
  }


  public final static String TABLE_CELL_SEPARATOR = "||";
  public final static String TABLE_CELL_SEPARATOR_BACKQUOTED = "\\|\\|";

  public final static String TABLE_LINE_SEPARATOR = "\n";
  public final static String TABLE_LINE_SEPARATOR_BACKQUOTED = "\n";

  public static class TableTextExtractor implements TextExtractor {
    public JTable table;
    public TableTextExtractor(JTable table) { this.table = table; }
    public String getText() {
      StringBuilder builder = new StringBuilder();
      int[] rows = table.getSelectedRows();
      int[] cols = table.getSelectedColumns();

      for (int i = 0 ; i < rows.length ; i++)
	{
	  for (int j = 0 ; j < cols.length ; j++)
	    {
	      builder.append(table.getValueAt(rows[i], cols[j]));
	      if (j < cols.length - 1)
		builder.append(TABLE_CELL_SEPARATOR);
	    }
	  if (i < rows.length - 1)
	    builder.append(TABLE_LINE_SEPARATOR);
	}

      return builder.toString();
    }
  }
}
