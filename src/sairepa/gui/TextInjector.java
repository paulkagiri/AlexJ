/*
** TextInjector.java
** Login : <jflesch@zeus.kwain.net>
** Started on  Sun Feb 22 16:38:28 2009 Jerome Flesch
*/
package sairepa.gui;

import javax.swing.JTable;
import javax.swing.text.JTextComponent;

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
      if (table.getSelectedColumnCount() != 1
	  || table.getSelectedRowCount() != 1)
	return;

      table.setValueAt(txt, table.getSelectedRow(), table.getSelectedColumn());
    }
  }
}
