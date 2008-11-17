package sairepa;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import sairepa.controller.Controller;
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
  private Controller controller;

  private Main() throws Exception {
    UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
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
      controller = new Controller(model, view);

      model.init();
      controller.init();
      view.init();
    } catch (Exception e) {
      view.close();
      controller.close();
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

    try {
      Main main = new Main();
      main.init();
    } catch(Exception e) {
      ErrorMessage.displayError("Erreur lors de l'initialisation de SaiRePa", e);
      throw e;
    }
  }
}
