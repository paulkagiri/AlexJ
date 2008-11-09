package sairepa;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import sairepa.model.Model;
import sairepa.model.Project;
import sairepa.view.ErrorMessage;
import sairepa.view.ProjectSelector;
import sairepa.view.View;

/**
 * Where everything starts.
 * @author jflesch
 */
public class Main {
  private Model model;
  private View view;

  private Main() throws Exception {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
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
      view.close();
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

  public Project promptForProject() {
    Vector<Project> projects = Model.locateProjects(new File("."));

    if (projects.size() <= 0) {
      throw new IllegalStateException("Aucun fichier client. Ne peut continuer.");
    }

    if (projects.size() == 1) {
      return projects.get(0);
    }

    ProjectSelector selector = new ProjectSelector(projects);
    return selector.promptUser();
  }

  public void init() throws Exception {
    Project p = promptForProject();

    if (p == null) {
      System.out.println("Interrupted by user");
      return;
    }

    try {
      model = p.createModel();
      view = new View(model);

      model.init();
      view.init();
    } catch (Exception e) {
      view.close();
      model.close(); // no saving.
      throw e;
    }
  }

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) throws Exception {
    System.out.println("");
    System.out.println("SAIREPA : SAIsie des REgistres PAroissiaux");
    System.out.println("");

    Main main;

    try {
      main = new Main();
      main.init();
    } catch(Exception e) {
      ErrorMessage.displayError("Erreur lors de l'initialisation de SaiRePa", e);
      throw e;
    }

    main.quit();
  }
}
