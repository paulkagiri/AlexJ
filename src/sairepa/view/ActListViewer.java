package sairepa.view;

import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import sairepa.gui.Table;
import sairepa.model.Act;
import sairepa.model.ActEntry;
import sairepa.model.ActField;
import sairepa.model.ActList;

public class ActListViewer extends Viewer
{
  public final static long serialVersionUID = 1;

  private ActList actList;
  private ActListTableModel model;
  private JTable table;

  public ActListViewer(ActList actList) {
    super(actList.getName(),
	  ActListViewerFactory.NAME,
	  ActListViewerFactory.ICON,
	  actList);
    this.actList = actList;
    this.setLayout(new BorderLayout());
    model = new ActListTableModel();
    table = new Table(model);
    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    this.add(new JScrollPane(table), BorderLayout.CENTER);
  }

  @Override
  public void init() {
    super.init();
    resizeColumnsToDefault();
  }

  private class ActListTableModel extends AbstractTableModel {
    public static final long serialVersionUID = 1;

    private List<ActField> columns;

    public ActListTableModel() {
      columns = new ArrayList<ActField>();

      for (ActField field : actList.getFields()) {
	columns.add(field);
      }
    }

    public int getRowCount() {
      return actList.getRowCount();
    }

    public int getColumnCount() {
      return columns.size();
    }

    public String getColumnName(int i) {
      return columns.get(i).getName();
    }

    public ActField getField(int i) {
      return columns.get(i);
    }

    public int findColumn(String name) {
      return columns.indexOf(name);
    }

    public Object getValueAt(int row, int column) {
      Act act = actList.getAct(row);
      ActEntry entry = act.getEntry(columns.get(column));
      return entry.getValue();
    }

  }

  private void resizeColumnsToDefault() {
    Enumeration<TableColumn> e = table.getColumnModel().getColumns();

    while (e.hasMoreElements()) {
      TableColumn c = e.nextElement();
      int fieldWidth = model.getField(c.getModelIndex()).getLength();
      int fieldNameWidth = model.getField(c.getModelIndex()).getName().length();
      int width = ((fieldWidth > fieldNameWidth) ? fieldWidth : fieldNameWidth) * 10;
      if (width > 100) {
	width = 100;
      }
      c.setPreferredWidth(width);
    }
  }

  @Override
  public void refresh() {
    model.fireTableDataChanged();
  }
}
