package sairepa.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.plaf.TabbedPaneUI;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.plaf.metal.MetalTabbedPaneUI;

import sairepa.model.Util;

public class CloseableTabbedPane extends JTabbedPane implements MouseListener
{
  public static final long serialVersionUID = 1;

  public CloseableTabbedPane() {
    super();
    addMouseListener(this);
  }

  public static interface CloseableTab {
    public void close();
  }


  public void addTab(String title, Component component) {
    this.addTab(title, null, component);
  }

  public void addTab(String title, Icon extraIcon, Component component) {
    if (component instanceof CloseableTab) {
      super.addTab(title, new CloseTabIcon(extraIcon), component);
    } else {
      super.addTab(title, extraIcon, component);
    }
    //setBackground(AppColor);
  }

  public void mouseClicked(MouseEvent e) {
    int tabNumber = getUI().tabForCoordinate(this, e.getX(), e.getY());
    if (tabNumber < 0)
      return;

    if (getComponentAt(tabNumber) instanceof CloseableTab
	&& getSelectedIndex() == tabNumber) {
      Rectangle rect = ((CloseTabIcon) getIconAt(tabNumber)).getCloseButtonBounds();

      if (rect.contains(e.getX(), e.getY())) {
	((CloseableTab)getComponentAt(tabNumber)).close();
      }
    }
  }

  public void mouseEntered(MouseEvent e) { }
  public void mouseExited(MouseEvent e) { }
  public void mousePressed(MouseEvent e) { }
  public void mouseReleased(MouseEvent e) { }

  class CloseTabIcon implements Icon
  {
    private Icon icon;
    private final int DIST = 5;

    public CloseTabIcon(Icon icon) {
      this.icon = icon;
    }

    private int lastX = 0, lastY = 0;

    public void paintIcon(Component c, Graphics g, int x, int y) {
      this.lastX = x;
      this.lastY = y;

      paintIcon(IconBox.minTabClose, c, g, x, y);
      if (icon != null) {
	paintIcon(icon, c, g, x + IconBox.minTabClose.getIconWidth() + DIST, y);
      }
    }

    /**
     * Make sure that icons are always painted in the middle vertically
     */
    private void paintIcon(Icon i, Component c, Graphics g, int x, int y) {
      int shiftY = (getIconHeight() - i.getIconHeight()) / 2;
      i.paintIcon(c, g, x, y+shiftY);
    }

    public int getIconWidth() {
      int iconWidth = ((icon != null) ? icon.getIconWidth() + DIST: 0);
      return IconBox.minTabClose.getIconWidth() + iconWidth;
    }

    public int getIconHeight() {
      int iconHeight = ((icon != null) ? icon.getIconHeight() : 0);

      return ((IconBox.minTabClose.getIconHeight() > iconHeight) ?
	      IconBox.minTabClose.getIconHeight() : iconHeight);
    }

    public Rectangle getCloseButtonBounds() {
      return new Rectangle(lastX, lastY,
			   IconBox.minTabClose.getIconWidth(),
			   IconBox.minTabClose.getIconHeight());
    }
  }

  private static class CloseableTestLabel extends JLabel implements CloseableTab {
    public static final long serialVersionUID = 1;

    private final JTabbedPane pane;

    public CloseableTestLabel(JTabbedPane pane, String str) {
      super(str);
      this.pane = pane;
    }

    public void close() {
      System.out.println("Canard");
      pane.remove(this);
    }
  }

  public static void main(String args[]) throws Exception
  {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

    JFrame frame=new JFrame();
    JTabbedPane tabPane = new CloseableTabbedPane();
    tabPane.addTab("0", new CloseableTestLabel(tabPane, "0   "));
    tabPane.addTab("1", new JLabel("1   "));
    tabPane.addTab("2", new CloseableTestLabel(tabPane, "2   "));
    tabPane.addTab("3", new CloseableTestLabel(tabPane, "3   "));
    frame.setContentPane(tabPane);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(400,400);
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }
}
