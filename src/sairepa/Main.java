package sairepa;

import sairepa.model.Model;
import sairepa.gui.MainWindow;

import java.io.File;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

/**
 * Where everything starts.
 * @author jflesch
 */
public class Main {
  private Model model;

  private Main() throws Exception {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
      // TODO display exception
      System.err.println("WARNING - Can't set look'n'feel, because: "
			 + e.toString());
      System.err.println("Message: " + e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * Called when the user asked to stop the application.
   * Will take care of the confirmation dialogs / savings.
   */
  public void quit() {
    try {
      model.save();
      System.exit(0);
    } catch (SQLException e) {
      System.out.println(e.toString());
      e.printStackTrace();
      // TODO: display the exception in a better way
    }
  }

  public void init() throws Exception {
    Model model = new Model(new File("koe"));
    model.init();
  }

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) throws Exception {
    System.out.println("");
    System.out.println("SAIsie des REgistres PAroissiaux");
    System.out.println("");

    // TODO: catch exceptions
    new Main().init();
  }
}
