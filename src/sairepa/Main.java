package sairepa;

import sairepa.model.ClientFile;
import sairepa.gui.MainWindow;

import javax.swing.JOptionPane;
import javax.swing.UIManager;


/**
 * Where everything starts.
 * @author jflesch
 */
public class Main {
  private ClientFile clientFile;
  private MainWindow mainWindow;

  private Main() throws Exception {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
      System.err.println("WARNING - Can't set look'n'feel, because: "
			 + e.toString());
      System.err.println("Message: " + e.getMessage());
      e.printStackTrace();
    }

    try {
      clientFile = new ClientFile("CLIENT.DAT");
    } catch (ClientFile.InvalidClientFileException e) {
      JOptionPane.showMessageDialog(null,
				    "Fichier client invalide. Utilisation non-autorisée",
				    "Fichier client invalide",
				    JOptionPane.ERROR_MESSAGE);
      throw e;
    }

    System.out.println("Client file:\n" + clientFile);

    mainWindow = new MainWindow(this);
  }

  /**
   * Called when the user asked to stop the application.
   * Will take care of the confirmation dialogs / savings.
   */
  public void quit() {
    System.exit(0);
  }

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) throws Exception {
    System.out.println("");
    System.out.println("SAIsie des REgistres PAroissiaux");
    System.out.println("");

    new Main();
  }
}
