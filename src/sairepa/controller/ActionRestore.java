package sairepa.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import javax.swing.AbstractButton;
import javax.swing.JOptionPane;

import sairepa.model.Model;
import sairepa.model.ProgressionObserver;
import sairepa.view.ErrorMessage;
import sairepa.view.MainWindow;
import sairepa.view.SplashScreen;
import sairepa.view.View;

public class ActionRestore implements ActionListener
{
  private Model model;
  private View view;
  private Controller controller;

  public ActionRestore(Model model, View view, Controller controller) {
    this.model = model;
    this.view = view;
    this.controller = controller;
  }

  public void actionPerformed(ActionEvent e) {
    if (JOptionPane.showConfirmDialog(view.getMainWindow(),
				      "Attention ! Vous allez effacer tout les changements " +
				      "effectu\351s entre le moment de la sauvegarde et maintenant. " +
				      "Ceci est irr\351versible ! \312tes-vous s\373r ?",
				      "\312tes-vous s\373r ?",
				      JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION)
      return;

    Date d;
    try {
      d = MainWindow.USER_DATE_FORMAT.parse(((AbstractButton)e.getSource()).getText());
    } catch (java.text.ParseException exc) {
      ErrorMessage.displayError(view.getMainWindow(), exc);
      return;
    }

    view.getMainWindow().closeAllViewers();
    view.getMainWindow().setVisible(false);

    SplashScreen sc = new SplashScreen("Restauration");
    sc.start();
    try {
      model.close(ProgressionObserver.DUMB_OBSERVER);
      model.getBackupManager().restore(d, sc);
      // we don't need to purge the DB, because when we will reinitialize the model,
      // it will see that the dbf are more recent than the DB
      model.init(sc);
    } catch (Exception exc) {
      ErrorMessage.displayError(exc);
      throw new RuntimeException(exc);
    } finally {
      sc.stop();
    }

    view.getMainWindow().setVisible(true);
  }
}
