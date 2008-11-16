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
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.plaf.TabbedPaneUI;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.plaf.metal.MetalTabbedPaneUI;

public class CloseableTabbedPane extends JTabbedPane
{
  public static final long serialVersionUID = 1;

  public CloseableTabbedPane() {
    super();
    this.setUI(new CloseableTabbedPaneUI());
  }

  public static interface CloseableTab {
    public void close();
  }

  public class CloseableTabbedPaneUI extends MetalTabbedPaneUI {
    final int MARGIN = 5;
    final int WIDTH = IconBox.minTabClose.getIconWidth();
    final int HEIGHT = IconBox.minTabClose.getIconHeight();

    public CloseableTabbedPaneUI() {
      super();
    }

    protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
      int x = super.calculateTabWidth(tabPlacement, tabIndex, metrics);

      if (tabPane.getComponentAt(tabIndex) instanceof CloseableTab) {
	x += WIDTH + (2*MARGIN);
      }

      return x;
    }

    protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
      int h = super.calculateTabHeight(tabPlacement, tabIndex, fontHeight);

      return ((h >= HEIGHT + (2*MARGIN)) ? h : (HEIGHT + (2*MARGIN)));
    }

    protected void paintTab(Graphics g, int tabPlacement,
			    Rectangle[] rects, int tabIndex,
			    Rectangle iconRect, Rectangle textRect) {
      Rectangle rect=rects[tabIndex];
      super.paintTab(g, tabPlacement, rects, tabIndex, iconRect, textRect);

      if (tabPane.getComponentAt(tabIndex) instanceof CloseableTab) {
	IconBox.minTabClose.paintIcon(CloseableTabbedPane.this, g,
	    rect.x + rect.width - WIDTH - MARGIN, rect.y + ((rect.height - HEIGHT) / 2));
      }
    }

    protected MouseListener createMouseListener() {
      return new MouseHandlerVisitor();
    }

    private class MouseHandlerVisitor implements MouseListener {
      private MouseListener mouseListenerVisited;

      public MouseHandlerVisitor() {
	mouseListenerVisited = CloseableTabbedPaneUI.super.createMouseListener();
      }

      public void mouseClicked(MouseEvent e) {
	int x=e.getX();
	int y=e.getY();
	int tabIndex= CloseableTabbedPaneUI.this.tabForCoordinate(CloseableTabbedPane.this, x, y);

	if (tabIndex >= 0 && tabIndex == getSelectedIndex()) {
	  Rectangle tabRect= CloseableTabbedPaneUI.this.getTabBounds(CloseableTabbedPane.this, tabIndex);
	  x=x-tabRect.x;
	  y=y-tabRect.y;

	  Component c = tabPane.getComponentAt(tabIndex);
	  if ((c instanceof CloseableTab)
	      && ( ( (x >= tabRect.width - WIDTH - MARGIN)
		     && (x <= tabRect.width - MARGIN)
		     && (y >= (tabRect.height - HEIGHT) / 2)
		     && (y <= (tabRect.height - HEIGHT) / 2 + HEIGHT) ))) {
	    ((CloseableTab)c).close();
	  }

	} else {
	  mouseListenerVisited.mouseClicked(e);
	}
      }

      public void mouseEntered(MouseEvent e) {
	mouseListenerVisited.mouseEntered(e);
      }

      public void mouseExited(MouseEvent e) {
	mouseListenerVisited.mouseExited(e);
      }

      public void mousePressed(MouseEvent e) {
	mouseListenerVisited.mousePressed(e);
      }

      public void mouseReleased(MouseEvent e) {
	mouseListenerVisited.mouseReleased(e);
      }
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
    //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

    JFrame frame=new JFrame();
    JTabbedPane tabPane = new CloseableTabbedPane();
    tabPane.addTab("long long long long str1", new JLabel("1"));
    tabPane.addTab("long long long long str2", new CloseableTestLabel(tabPane, "2"));
    tabPane.addTab("long long long long str3", new CloseableTestLabel(tabPane, "3"));
    frame.setContentPane(tabPane);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(200,200);
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }
}
