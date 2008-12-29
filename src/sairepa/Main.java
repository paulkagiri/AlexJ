package sairepa;

import java.awt.Font;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;

import sairepa.controller.Controller;
import sairepa.model.Model;
import sairepa.model.ProgressionObserver;
import sairepa.model.Project;
import sairepa.view.ErrorMessage;
import sairepa.view.ProjectSelector;
import sairepa.view.SplashScreen;
import sairepa.view.View;

/**
 * Where everything starts.
 * @author jflesch
 */
public class Main {
  public final static String APPLICATION_NAME = "AlexJ";

  private Model model = null;
  private View view = null;
  private Controller controller = null;

  private Main() throws Exception {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    // we force metal theme to avoid problems with the closeable tabs
    //UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
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

    SplashScreen ss = new SplashScreen(APPLICATION_NAME,
				       new Font("Dialog", Font.BOLD, 32));
    ss.start();

    try {
      model = p.createModel();
      view = new View(model);
      controller = new Controller(model, view);

      model.init(ss);

      ss.setProgression(99, "Preparation de l'interface utilisateur ...");
      controller.init();
      view.init();
    } catch (Exception e) {
      if (view != null)
	view.close();
      if (controller != null)
	controller.close();
      if (model != null)
	model.close(new ProgressionObserver() {
	    public void setProgression(int progression, String txt) { /* dumb */ }
	  }); // no saving.
      throw e;
    } finally {
      ss.stop();
    }
  }

  public void extractFileFromJar(String src, String dst) throws Exception {
    try {
      String realHome = this.getClass().getProtectionDomain().
	getCodeSource().getLocation().toString();

      String home = java.net.URLDecoder.decode(realHome.substring(5), "UTF-8");

      System.out.println("Extracting : "+realHome+" ; "+src+" ; "+dst);

      ZipFile jar = new ZipFile(home);
      ZipEntry entry = jar.getEntry(src);

      File jarFile = new File(dst);


      InputStream in = new BufferedInputStream(jar.getInputStream(entry));
      OutputStream out = new BufferedOutputStream(new FileOutputStream(jarFile));

      byte[] buffer = new byte[2048];

      int nBytes;

      while( (nBytes = in.read(buffer)) > 0) {
	out.write(buffer, 0, nBytes);
      }

      out.flush();
      out.close();
      in.close();

      return;
    } catch(IOException e) {
      throw new Exception("Can't extract '" + src + "'", e);
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
      main.extractFileFromJar("prncv.dbf", "prncv.dbf");
      main.init();
    } catch(Exception e) {
      ErrorMessage.displayError("Erreur lors de l'initialisation de SaiRePa", e);
      throw e;
    }
  }
}
