package sairepa.view;

import java.awt.BorderLayout;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JLabel;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

import sairepa.gui.CloseableTabbedPane;
import sairepa.model.Model;

/**
 * Represents the main window of this software.
 * @author jflesch
 */
public class MainWindow extends JFrame {
  public static final long serialVersionUID = 1;

  private int DEFAULT_SIZE_X = 800;
  private int DEFAULT_SIZE_Y = 600;

  private JMenuItem menuFileQuit;
  private TabSelecter tabOpener;
  private CloseableTabbedPane tabs;

  private final static ViewerFactory[] viewerFactories = new ViewerFactory[] {
    new ActViewerFactory(),
    new ActListViewerFactory(),
  };

  /**
   * Creates new form MainWindow
   * @param main
   */
  public MainWindow(Model model) {
    super("SaiRePa");

    this.getContentPane().setLayout(new BorderLayout(5, 5));

    this.setJMenuBar(createMenuBar());

    this.getContentPane().add(tabs = new CloseableTabbedPane(), BorderLayout.CENTER);
    this.getContentPane().add(new JScrollPane(tabOpener = createTabSelecter(model)),
			      BorderLayout.WEST);

    setSize(DEFAULT_SIZE_X, DEFAULT_SIZE_Y);
  }

  public ViewerFactory[] getViewerFactories() {
    return viewerFactories;
  }

  private JMenuBar createMenuBar() {
    JMenuBar menuBar = new JMenuBar();
    JMenu menuFile = new JMenu("Fichier");
    menuFileQuit = new JMenuItem("Quitter");
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

  public void addViewer(Viewer v) {
    tabs.addTab(v.getName(), v.getIcon(), v);
  }
}
