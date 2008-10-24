package sairepa.gui;

import sairepa.Main;

import javax.swing.JFrame;

/**
 * Represents the main window of this software.
 * @author jflesch
 */
public class MainWindow {
  private Main main;

  private JFrame mainFrame;

  /**
   * Creates new form MainWindow
   * @param main
   */
  public MainWindow(Main main) {
    this.main = main;

    mainFrame = makeGui();
  }

  protected JFrame makeGui() {
    return null;
  }

  public void setVisible(boolean v) {
    //mainFrame.setVisible(v);
  }
}
