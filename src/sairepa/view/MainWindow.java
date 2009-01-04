package sairepa.view;

import java.awt.BorderLayout;
import java.util.List;
import java.util.Vector;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JLabel;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import sairepa.gui.CloseableTabbedPane;
import sairepa.gui.IconBox;
import sairepa.model.Model;

/**
 * Represents the main window of this software.
 * @author jflesch
 */
public class MainWindow extends JFrame implements ChangeListener {
  public static final long serialVersionUID = 1;

  private int DEFAULT_SIZE_X = 900;
  private int DEFAULT_SIZE_Y = 700;

  private JMenuItem menuFileSearch;
  private JMenuItem menuFilePrint;
  private JMenuItem menuFileQuit;
  private TabSelecter tabOpener;
  private CloseableTabbedPane tabs;

  private List<TabObserver> tabObservers = new Vector<TabObserver>();

  private final static ViewerFactory[] viewerFactories = new ViewerFactory[] {
    new ActViewerFactory(),
    new ActListViewerFactory(),
  };

  /**
   * Creates new form MainWindow
   * @param main
   */
  public MainWindow(Model model) {
    super(sairepa.Main.APPLICATION_NAME);

    this.getContentPane().setLayout(new BorderLayout(5, 5));

    this.setJMenuBar(createMenuBar());

    JScrollPane tabOpenerScrollPane;

    this.getContentPane().add(tabs = new CloseableTabbedPane(), BorderLayout.CENTER);
    this.getContentPane().add(tabOpenerScrollPane =
                              new JScrollPane(tabOpener = createTabSelecter(model)),
			      BorderLayout.WEST);

    tabs.addChangeListener(this);
    tabOpenerScrollPane.getVerticalScrollBar().setUnitIncrement(15);
    tabOpenerScrollPane.setPreferredSize(new java.awt.Dimension(140, 140));

    setSize(DEFAULT_SIZE_X, DEFAULT_SIZE_Y);
  }

  public static interface TabObserver {
    public void tabSelected(Viewer v);
    public void allTabClosed();
  }

  public void addTabObserver(TabObserver obs) {
    tabObservers.add(obs);
  }

  public void deleteTabObserver(TabObserver obs) {
    tabObservers.remove(obs);
  }

  public void stateChanged(ChangeEvent e) {
    for (TabObserver obs : tabObservers) {
      if (tabs.getTabCount() <= 0) {
	obs.allTabClosed();
      } else {
	obs.tabSelected((Viewer)tabs.getSelectedComponent());
      }
    }
  }

  public ViewerFactory[] getViewerFactories() {
    return viewerFactories;
  }

  private JMenuBar createMenuBar() {
    JMenuBar menuBar = new JMenuBar();
    JMenu menuFile = new JMenu("Fichier");

    menuFileSearch = new JMenuItem("Rechercher", IconBox.search);
    menuFileSearch.setAccelerator(
        javax.swing.KeyStroke.getKeyStroke(
        java.awt.event.KeyEvent.VK_F, java.awt.Event.CTRL_MASK));
    menuFileSearch.setEnabled(false);

    menuFilePrint = new JMenuItem("Imprimer", IconBox.print);
    menuFilePrint.setAccelerator(
        javax.swing.KeyStroke.getKeyStroke(
        java.awt.event.KeyEvent.VK_P, java.awt.Event.CTRL_MASK));
    menuFilePrint.setEnabled(false);

    menuFileQuit = new JMenuItem("Quitter", IconBox.quit);
    menuFileQuit.setAccelerator(
        javax.swing.KeyStroke.getKeyStroke(
        java.awt.event.KeyEvent.VK_Q, java.awt.Event.CTRL_MASK));

    menuFile.add(menuFileSearch);
    menuFile.add(menuFilePrint);
    menuFile.add(menuFileQuit);

    menuBar.add(menuFile);

    return menuBar;
  }

  private TabSelecter createTabSelecter(Model model) {
    return new TabSelecter(model.getFactories(), getViewerFactories());
  }

  public TabSelecter getTabSelecter() {
    return tabOpener;
  }

  public AbstractButton getQuitButton() {
    return menuFileQuit;
  }

  public AbstractButton getSearchButton() {
    return menuFileSearch;
  }

  public AbstractButton getPrintButton() {
    return menuFilePrint;
  }

  private Vector<Viewer> viewers = new Vector<Viewer>();

  public void addViewer(Viewer v) {
    //tabs.addTab(v.getName(), v.getIcon(), v);
    tabs.addTab(v.getName(), v);
    viewers.add(v);
  }

  public void selectViewer(Viewer v) {
    tabs.setSelectedComponent(v);
  }

  public void removeViewer(Viewer v) {
    tabs.remove(v);
    viewers.remove(v);
  }

  public List<Viewer> getViewers() {
    return viewers;
  }
}
