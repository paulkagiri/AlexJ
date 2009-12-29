package sairepa.view;

import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import sairepa.gui.IconBox;
import sairepa.gui.RightClickMenu;
import sairepa.gui.Table;
import sairepa.model.Act;
import sairepa.model.ActEntry;
import sairepa.model.ActField;
import sairepa.model.ActList;
import sairepa.model.ActListFactory;
import sairepa.model.Util;

public class ActListViewer extends Viewer
  implements Table.ReorderingListener, ActionListener, CaretListener, ListSelectionListener
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
    table.setRowSelectionAllowed(true);
    table.setColumnSelectionAllowed(true);
    table.getSelectionModel().addListSelectionListener(this);

    RightClickMenu.addRightClickMenu(table).add(new OpenActActionItem(this));

    this.add(new JScrollPane(table), BorderLayout.CENTER);
    this.add(createSearchForm(), BorderLayout.SOUTH);
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

    public int getActNumber(int row) {
      Act act = actList.getAct(row);
      return act.getRow() + 1;
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
      if (column == 0) /* first column can't be modified */
	return;

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
      int fieldWidth = ((index > 0) ? model.getField(c.getModelIndex()).getMaxLength() : 4);
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

  @Override
  public void refresh(Act a) {
      int row = actList.getActVisualRow(a);
      if ( row < 0 ) {
	  refresh();
	  return;
      }
      model.fireTableRowsUpdated(row, row);
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

  @Override
  public void close() {
    super.close();
  }

  @Override
  public String canClose() {
    return null;
  }

  public void valueChanged(ListSelectionEvent e) {
    if (table.getSelectedColumn() == 0) {
      table.getSelectionModel().removeListSelectionListener(this);
      table.setColumnSelectionInterval(0, model.getColumnCount()-1);
      table.getSelectionModel().addListSelectionListener(this);
    }
  }

  private JLabel searchLabel = new JLabel(IconBox.search);
  private JTextField searchField = new JTextField(20);
  private Color defaultFieldBackColor = null;
  private Color notFoundBackColor = new Color(255, 64, 64);
  private JCheckBox searchColumnOnly = new JCheckBox("Uniquement sur la colone s\351l\351ctionn\351e", false);
  private JButton nextSearchButton = new JButton(IconBox.down);
  private JButton previousSearchButton = new JButton(IconBox.up);

  private JPanel createSearchForm() {
    JPanel p = new JPanel(new BorderLayout());

    JPanel realPanel = new JPanel(new BorderLayout(10, 10));

    JPanel searchPanel = new JPanel(new BorderLayout());
    searchField.addActionListener(this);
    searchField.addCaretListener(this);
    RightClickMenu.addRightClickMenu(searchField);
    searchPanel.add(searchLabel, BorderLayout.WEST);
    searchPanel.add(searchField, BorderLayout.CENTER);

    realPanel.add(searchPanel, BorderLayout.WEST);

    //searchColumnOnly.addActionListener(this);
    realPanel.add(searchColumnOnly, BorderLayout.CENTER);

    JPanel buttonPanel = new JPanel(new GridLayout(1, 2));
    nextSearchButton.addActionListener(this);
    nextSearchButton.setToolTipText("Suivant");
    buttonPanel.add(nextSearchButton);
    previousSearchButton.setToolTipText("Precedent");
    previousSearchButton.addActionListener(this);
    buttonPanel.add(previousSearchButton);
    realPanel.add(buttonPanel, BorderLayout.EAST);

    p.add(realPanel, BorderLayout.WEST);
    p.add(new JLabel(""), BorderLayout.CENTER);
    return p;
  }

  private void focusSearchField() {
    searchField.requestFocus();
  }

  @Override
  public boolean canBeSearched() {
    return true;
  }

  @Override
  public void displaySearchForm() {
    focusSearchField();
  }

  private final static Object SEARCH_LOCK = new Object();

  private class SearchThread implements Runnable {
    private String str;
    private int col;
    private int row;
    private boolean colOnly;
    private boolean cont;
    private int move;
    private boolean running;

    /**
     * @param colOnly don't move to another column
     * @param move +1 or -1, depending of the way you want to go
     */
    public SearchThread(String str, int col, int row, boolean colOnly, boolean cont, int move) {
      this.str = str;
      this.col = col;
      this.row = row;
      this.colOnly = colOnly;
      this.cont = cont;
      this.move = move;
      running = true;
    }

    public void stop() {
      running = false;
    }

    public void run() {
	try {
	    synchronized(SEARCH_LOCK) {
		Util.check(move == 1 || move == -1);

		str = Util.trim(str.toLowerCase());
		if ("".equals(str)) {
		    return;
		}

		if (cont) {
		    if (colOnly) {
			row += move;
		    } else {
			col += move;
		    }
		}

		int rowCount = model.getRowCount();
		int colCount = model.getColumnCount();
		int targetRow = -1;
		int targetCol = -1;

		while (row >= 0 && row < rowCount && targetRow < 0 && running) {
		    while (col >= 0 && col < colCount && targetCol < 0 && running) {
			Object o = model.getValueAt(row, col);
			if ( o == null ) {
			    System.out.println("WARNING: Act " + Integer.toString(row) + " has a missing value "
					       + "(" + Integer.toString(row) + "," + Integer.toString(col) + ")");
			    col += move;
			    continue;
			}
			String s = o.toString();
			s = Util.trim(s);
			s = s.toLowerCase();
			if (s.contains(str)) {
			    targetRow = row;
			    targetCol = col;
			}
			if (!colOnly) col += move;
			else break;
		    }
		    row += move;
		    if (!colOnly)
			col = 0;
		}

		if (targetRow < 0 || targetCol < 0) {
		    if (defaultFieldBackColor == null) defaultFieldBackColor = searchField.getBackground();
		    searchField.setBackground(notFoundBackColor);
		} else {
		    searchField.setBackground(defaultFieldBackColor);
		    selectCell(targetRow, targetCol);
		}
	    }
	} catch (Exception e) {
	    System.err.println("Exception while searching: " + e.toString());
	    e.printStackTrace();
	}
    }
  }

  private void selectCell(int row, int column) {
    table.setRowSelectionInterval(row, row);
    table.setColumnSelectionInterval(column, column);
    java.awt.Rectangle rect = table.getCellRect(row, column, true);
    table.scrollRectToVisible(rect);
    table.repaint();
  }

  private SearchThread currentSearch = null;

  public void searchAndSelect(String str, int col, int row, boolean colOnly, boolean cont, int move) {
    synchronized(this) {
      if (currentSearch != null) currentSearch.stop(); // there can be only one
      currentSearch = new SearchThread(str, col, row, colOnly, cont, move);
      Thread th = new Thread(currentSearch);
      th.start();
    }
  }

  public void caretUpdate(CaretEvent e) {
    if (e.getSource() == searchField) {
      if (!"".equals(searchField.getText().trim())) {
	searchAndSelect(searchField.getText(), 0, 0, searchColumnOnly.isSelected(), false, 1);
      } else {
	selectCell(0, 0);
      }
    }
  }

  public void actionPerformed(ActionEvent e) {
    if (model.getRowCount() <= 0) return;

    if (e.getSource() == searchField) {
      searchAndSelect(searchField.getText(), 0, 0, searchColumnOnly.isSelected(), false, 1);
    } else {
      int row = table.getSelectedRow();
      int col = table.getSelectedColumn();
      if (row < 0) row = 0;
      if (col < 0) col = 0;

      if (e.getSource() == nextSearchButton) {
	searchAndSelect(searchField.getText(),
			col, row, searchColumnOnly.isSelected(), true, 1);
      } else if (e.getSource() == previousSearchButton) {
	searchAndSelect(searchField.getText(),
			col, row, searchColumnOnly.isSelected(), true, -1);
      }
    }
  }

  public void setSelectedAct(int act) {
    /* I'm too lazy */
    throw new UnsupportedOperationException("Nop");
  }

  public int[] getSelectedActs() {
    int rows[] = table.getSelectedRows();
    int acts[] = new int[rows.length];

    for (int i = 0 ; i < rows.length ; i++) {
      acts[i] = model.getActNumber(rows[i]);
    }

    return acts;
  }
}
