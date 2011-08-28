package sairepa.view;

import java.awt.BorderLayout;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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

  private JMenuItem menuFileOpen;
  private JMenuItem menuFileSave;
  private JMenuItem menuFileSearch;
  private JMenuItem menuFilePrint;
  private JMenu menuFileRestore;
  private JMenuItem menuFileQuit;
  private TabSelecter tabOpener;
  private CloseableTabbedPane tabs;

  private List<TabObserver> tabObservers = new Vector<TabObserver>();

  private Model model;

  private final static ViewerFactory[] viewerFactories = new ViewerFactory[] {
    new ActViewerFactory(),
    new ActListViewerFactory(),
    new SortedActListViewerFactory(),
  };

  /**
   * Creates new form MainWindow
   * @param model uninitialized
   */
  public MainWindow(Model model) {
    super(sairepa.Main.APPLICATION_NAME);
    this.model = model;

    this.getContentPane().setLayout(new BorderLayout(5, 5));

    this.setJMenuBar(createMenuBar(model));

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

  private JMenuBar createMenuBar(Model model) {
    JMenuBar menuBar = new JMenuBar();
    JMenu menuFile = new JMenu("Fichier");

    menuFileSave = new JMenuItem("Enregistrer", IconBox.fileSave);
    menuFileSave.setAccelerator(
        javax.swing.KeyStroke.getKeyStroke(
        java.awt.event.KeyEvent.VK_S, java.awt.Event.CTRL_MASK | java.awt.Event.SHIFT_MASK));
    menuFileSave.setEnabled(true);

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

    menuFileOpen = new JMenuItem("A partir d'un fichier ...", IconBox.fileOpen);
    menuFileOpen.setAccelerator(
        javax.swing.KeyStroke.getKeyStroke(
        java.awt.event.KeyEvent.VK_O, java.awt.Event.CTRL_MASK | java.awt.Event.SHIFT_MASK));
    menuFileOpen.setEnabled(true);

    menuFileRestore = new JMenu("Restaurer");
    menuFileRestore.setEnabled(true);
    menuFileRestore.add(menuFileOpen);

    menuFileQuit = new JMenuItem("Quitter", IconBox.quit);
    menuFileQuit.setAccelerator(
        javax.swing.KeyStroke.getKeyStroke(
        java.awt.event.KeyEvent.VK_Q, java.awt.Event.CTRL_MASK));

    menuFile.add(menuFileSave);
    menuFile.add(menuFileRestore);
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

  public AbstractButton getOpenButton() {
    return menuFileOpen;
  }

  public AbstractButton getSaveButton() {
    return menuFileSave;
  }

  public static final DateFormat USER_DATE_FORMAT = DateFormat.getDateInstance(DateFormat.MEDIUM);
  private List<AbstractButton> restoreButtons = new Vector<AbstractButton>();

  public List<AbstractButton> getRestoreButtons() {
    return restoreButtons;
  }

  public void init() {
    initRestoreMenu();
  }

  private void initRestoreMenu() {
    for (Date d : model.getBackupManager().getAvailableBackups()) {
      JMenuItem jmi = new JMenuItem(USER_DATE_FORMAT.format(d));
      menuFileRestore.add(jmi);
      restoreButtons.add(jmi);
    }
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

  public void closeAllViewers() {
    Vector<Viewer> copy = (Vector<Viewer>)viewers.clone();
    for (Viewer v : copy)
      removeViewer(v);
  }

  public List<Viewer> getViewers() {
    return viewers;
  }
}
