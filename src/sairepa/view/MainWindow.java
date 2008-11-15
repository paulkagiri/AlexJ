package sairepa.view;

import javax.swing.AbstractButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JFrame;

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

  /**
   * Creates new form MainWindow
   * @param main
   */
  public MainWindow(Model model) {
    super("SaiRePa");

    JMenuBar menuBar = new JMenuBar();
    JMenu menuFile = new JMenu("Fichier");
    menuFileQuit = new JMenuItem("Quitter");

    menuFile.add(menuFileQuit);
    menuBar.add(menuFile);

    this.setJMenuBar(menuBar);

    setSize(DEFAULT_SIZE_X, DEFAULT_SIZE_Y);
  }

  public AbstractButton getQuitButton() {
    return menuFileQuit;
  }
}
