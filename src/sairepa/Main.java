package sairepa;

import sairepa.model.Model;
import sairepa.gui.MainWindow;

import java.io.File;
import java.io.IOException;
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
    quit(0);
  }

  public void quit(int code) {
    try {
      model.save();
      System.exit(code);
    } catch (SQLException e) {
      System.out.println("SQLException: " + e.toString());
      e.printStackTrace();
      // TODO: display the exception in a better way
    } catch (IOException e) {
      System.out.println("IOException: " + e.toString());
      e.printStackTrace();
      // TODO: display the exception in a better way
    }
  }

  public void init() throws Exception {
    Model model = new Model(new File("koe"));
    try {
      model.init();
      model.save();
    } finally {
      model.close();
    }
  }

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) throws Exception {
    System.out.println("");
    System.out.println("SAIREPA : SAIsie des REgistres PAroissiaux");
    System.out.println("");

    // TODO: catch exceptions
    new Main().init();
  }
}
