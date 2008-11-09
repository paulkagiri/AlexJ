package sairepa.view;

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

  /**
   * Creates new form MainWindow
   * @param main
   */
  public MainWindow(Model model) {
    super("SaiRePa");

    setSize(DEFAULT_SIZE_X, DEFAULT_SIZE_Y);
  }
}
