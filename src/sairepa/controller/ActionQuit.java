package sairepa.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.sql.SQLException;
import javax.swing.JOptionPane;

import sairepa.model.Model;
import sairepa.view.ErrorMessage;
import sairepa.view.SplashScreen;
import sairepa.view.View;
import sairepa.view.Viewer;

public class ActionQuit implements ActionListener, WindowListener
{
  private Model model;
  private View view;
  private Controller controller;

  public ActionQuit(Model model, View view, Controller controller) {
    this.model = model;
    this.view = view;
    this.controller = controller;
  }

  /**
   * Called when the user asked to stop the application.
   * Will take care of the confirmation dialogs / savings.
   */
  public void quit() {
    quit(0);
  }

  public void quit(int code) {
    if (code == 0) {
      if (!canQuit() && !askUserIfTheyReallyWantToDoThat()) {
	return;
      }
    }

    System.out.println("Quitting ...");

    SplashScreen ss = new SplashScreen("Fermeture de " + sairepa.Main.APPLICATION_NAME);
    try {
      ss.start();
      ss.setProgression(0, "Fermeture ...");
      view.close();
      controller.close();
      model.save(ss);
      model.close(ss);

      System.exit(code);
    } catch (SQLException e) {
      System.out.println("SQLException: " + e.toString());
      e.printStackTrace();
      ErrorMessage.displayError("Erreur au moment de quitter", e);
    } catch (IOException e) {
      System.out.println("IOException: " + e.toString());
      e.printStackTrace();
      ErrorMessage.displayError("Erreur au moment de quitter", e);
    } finally {
      ss.stop();
    }
  }

  public boolean canQuit() {
    for (Viewer v : view.getMainWindow().getViewers()) {
      if (v.canClose() != null) {
	return false;
      }
    }
    return true;
  }

  public boolean askUserIfTheyReallyWantToDoThat() {
    int r = JOptionPane.showConfirmDialog(view.getMainWindow(),
					  "Certains onglets contiennent des donn\351es invalides "
					  + "qui ne seront pas sauvegard\351es. "
					  + "\312tes-vous s\373r de vouloir quitter ?",
					  "\312tes-vous s\373r ?",
					  JOptionPane.YES_NO_OPTION);
    return (r == JOptionPane.YES_OPTION);
  }

  public void actionPerformed(ActionEvent e) {
    quit();
  }

  public void windowActivated(WindowEvent e) { }
  public void windowClosed(WindowEvent e) { }
  public void windowClosing(WindowEvent e) {
    quit();
  }
  public void windowDeactivated(WindowEvent e) { }
  public void windowDeiconified(WindowEvent e) { }
  public void windowIconified(WindowEvent e) { }
  public void windowOpened(WindowEvent e) { }
}
