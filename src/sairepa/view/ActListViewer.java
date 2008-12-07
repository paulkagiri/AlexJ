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
import sairepa.model.ActListFactory;

public class ActListViewer extends Viewer implements Table.ReorderingListener
{
  public final static long serialVersionUID = 1;

  private ActList actList;
  private ActListTableModel model;
  private Table table;

  public ActListViewer(ActList actList) {
    super(actList,
	  ActListViewerFactory.NAME,
	  ActListViewerFactory.ICON);
    this.actList = actList;
    this.setLayout(new BorderLayout());
    model = new ActListTableModel();
    table = new Table(model);
    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    table.addReorderingListener(this);
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
      return columns.size() + 1;
    }

    public String getColumnName(int i) {
      if (i == 0) {
	return "NUM";
      }

      return columns.get(i-1).getName();
    }

    public ActField getField(int i) {
      if (i == 0) {
	return null;
      }

      return columns.get(i-1);
    }

    public int findColumn(String name) {
      if (name.equals("NUM")) {
	return 0;
      }

      return columns.indexOf(name)+1;
    }

    public Object getValueAt(int row, int column) {
      Act act = actList.getAct(row);

      if (column == 0) {
	return Integer.toString(act.getRow() + 1);
      }

      ActEntry entry = act.getEntry(columns.get(column-1));
      return entry;
    }

    public boolean isCellEditable(int row, int column) {
      return (column > 0);
    }

    public void setValueAt(Object value, int row, int column) {
      System.out.println("Setting value");

      String val = value.toString();
      Act act = actList.getAct(row);
      ActEntry entry = act.getEntry(getField(column));
      entry.setValue(val);

      fireTableRowsUpdated(row, row);

      for (ViewerObserver obs : getObservers()) {
	obs.changingAct(ActListViewer.this, act);
      }
    }
  }

  private void resizeColumnsToDefault() {
    Enumeration<TableColumn> e = table.getColumnModel().getColumns();

    while (e.hasMoreElements()) {
      TableColumn c = e.nextElement();
      int index = c.getModelIndex();
      int fieldWidth = ((index > 0) ? model.getField(c.getModelIndex()).getLength() : 4);
      int fieldNameWidth = ((index > 0) ? model.getField(c.getModelIndex()).getName().length() : 4);
      int width = ((fieldWidth > fieldNameWidth) ? fieldWidth : fieldNameWidth) * 15;
      if (width > 150) {
	width = 150;
      }
      c.setPreferredWidth(width);
    }
  }

  @Override
  public void refresh() {
    model.fireTableDataChanged();
  }

  public void reorder(int columnIndex, boolean desc) {
    if (columnIndex == 0) {
      actList = actList.getSortedActList(null, desc);
    } else {
      String colName = model.getColumnName(columnIndex);
      actList = actList.getSortedActList(colName, desc);
    }
    refresh();
  }

  public boolean canClose() {
    boolean ok = true;

    for (Act a : actList) {
      if (!a.validate()) {
	ok = false;
	break;
      }
    }

    return ok;
  }
}
