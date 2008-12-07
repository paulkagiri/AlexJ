package sairepa.gui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.ListSelectionModel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Component;
import javax.swing.ImageIcon;
import java.util.List;
import java.util.Vector;

/**
 * Inherits from JTable
 */
public class Table extends JTable implements MouseListener {

  private static final long serialVersionUID = 3653294061426267455L;
  public final static Color COLOR_ONE = Color.WHITE;
  public final static Color COLOR_TWO = new Color(240, 240, 240);

  private boolean desc = false;
  private int sortedColumn = -1;

  private List<ReorderingListener> orderListeners = new Vector<ReorderingListener>();

  public static interface ReorderingListener {
    public void reorder(int columnIndex, boolean desc);
  }

  public Table() {
    super();
    setDefaultRenderer();
    addMouseListenerOnHeader();
  }

  public Table(int numRows, int numColumns) {
    super(numRows, numColumns);
    setDefaultRenderer();
    addMouseListenerOnHeader();
  }


  public Table(Object[][] data, Object[] columnNames) {
    super(data, columnNames);
    setDefaultRenderer();
    addMouseListenerOnHeader();
  }


  public Table(TableModel model) {
    super(model);
    setDefaultRenderer();
    addMouseListenerOnHeader();
  }

  public Table(TableModel model, TableColumnModel cModel) {
    super(model, cModel);
    setDefaultRenderer();
    addMouseListenerOnHeader();
  }

  public Table(TableModel model, TableColumnModel cModel,
	       ListSelectionModel lModel) {
    super(model, cModel, lModel);
    setDefaultRenderer();
    addMouseListenerOnHeader();
  }

  public Table(Vector data, Vector columns) {
    super(data, columns);
    setDefaultRenderer();
    addMouseListenerOnHeader();
  }

  public void addReorderingListener(ReorderingListener l) {
    orderListeners.add(l);
  }

  public void deleteReorderingListener(ReorderingListener l) {
    orderListeners.remove(l);
  }

  protected void notifyReordering(int columnIdx, boolean desc) {
    for (ReorderingListener l : orderListeners) {
      l.reorder(columnIdx, desc);
    }
  }

  private DefaultRenderer renderer;


  public void addMouseListenerOnHeader() {
    final JTableHeader header = this.getTableHeader();
    header.setUpdateTableInRealTime(true);
    header.addMouseListener(this);
    header.setReorderingAllowed(true);
  }

  public void setDefaultRenderer() {
    renderer = new DefaultRenderer();
    setDefaultRenderer(getColumnClass(0), renderer);

    ((DefaultTableCellRenderer)getTableHeader().getDefaultRenderer()).setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
  }

  public void mouseClicked(final MouseEvent e) {
    final TableColumnModel colModel = getColumnModel();
    final int columnModelIndex = colModel.getColumnIndexAtX(e.getX());
    final int modelIndex = colModel.getColumn(columnModelIndex).getModelIndex();

    if (modelIndex < 0 || columnModelIndex < 0)
      return;

    if (sortedColumn == modelIndex)
      desc = !desc;
    else
      sortedColumn = modelIndex;

    notifyReordering(sortedColumn, desc);

    for (int i = 0; i < getColumnCount(); i++) {
      final TableColumn column = colModel.getColumn(i);
      String suffix = "";

      if (column.getModelIndex() == sortedColumn) {
	suffix = " " + ((desc) ? ">>" : "<<");
      }

      column.setHeaderValue(getColumnName(column.getModelIndex()) + suffix);
    }
    getTableHeader().repaint();
  }

  public void mouseEntered(MouseEvent e) { }
  public void mouseExited(MouseEvent e) { }
  public void mousePressed(MouseEvent e) { }
  public void mouseReleased(MouseEvent e) { }

  public static class DefaultRenderer extends DefaultTableCellRenderer {
    private final static long serialVersionUID = 20060821;

    private boolean statusInProgressBars = true;
    private int columnWithKeys = -1;

    private JLabel labelRenderer;
    private JTextArea textAreaRenderer;

    public DefaultRenderer() {
      labelRenderer = new JLabel();
      textAreaRenderer = new JTextArea();
      textAreaRenderer.setEditable(false);
      textAreaRenderer.setLineWrap(true);
      textAreaRenderer.setWrapStyleWord(true);
    }

    public void showStatusInProgressBars(boolean v) {
      statusInProgressBars = v;
    }

    public void specifyColumnWithKeys(int c) {
      columnWithKeys = c;
    }


    /**
     * @return null if default color
     */
    public static Color setBackground(Component c, int row, boolean isSelected) {
      if (!isSelected) {
	if (row % 2 == 0) {
	  if (c != null)
	    c.setBackground(COLOR_ONE);
	  return COLOR_ONE;
	} else {
	  if (c != null)
	    c.setBackground(COLOR_TWO);
	  return COLOR_TWO;
	}
      }

      return null;
    }

    public Component getTableCellRendererComponent(final JTable table, Object value,
						   final boolean isSelected, final boolean hasFocus,
						   final int row, final int column) {

      if (value == null)
	value = "";

      Component cell;

      if (value instanceof ImageIcon) {
	labelRenderer.setIcon((ImageIcon)value);
	return labelRenderer;
      } if (value instanceof JPanel) {
	cell = (Component)value;
      } else if (value instanceof String && ((String)value).indexOf("\n") >= 0) {
	textAreaRenderer.setText((String)value);

	if (table.getRowHeight(row) < textAreaRenderer.getPreferredSize().getHeight())
	  table.setRowHeight((int)textAreaRenderer.getPreferredSize().getHeight());

	cell = textAreaRenderer;

      } else {
	cell = super.getTableCellRendererComponent(table, value,
						   isSelected, hasFocus,
						   row, column);

      }

      setBackground(cell, row, isSelected);

      cell.setForeground(Color.BLACK);


      if (column == columnWithKeys && value instanceof String) {
	String key = (String)value;
      }

      return cell;
    }

  }
}

