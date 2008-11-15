package sairepa.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.sql.SQLException;

import sairepa.model.Model;
import sairepa.view.ErrorMessage;
import sairepa.view.View;

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
    System.out.println("Quitting ...");

    try {
      view.close();
      controller.close();
      model.save();
      model.close();

      System.exit(code);
    } catch (SQLException e) {
      System.out.println("SQLException: " + e.toString());
      e.printStackTrace();
      ErrorMessage.displayError("Erreur au moment de quitter", e);
    } catch (IOException e) {
      System.out.println("IOException: " + e.toString());
      e.printStackTrace();
      ErrorMessage.displayError("Erreur au moment de quitter", e);
    }
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
