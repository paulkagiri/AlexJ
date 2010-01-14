package sairepa.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.SQLException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import jp.gr.java_conf.dangan.util.lha.LhaFile;
import jp.gr.java_conf.dangan.util.lha.LhaHeader;
import jp.gr.java_conf.dangan.util.lha.LhaOutputStream;

import sairepa.model.Model;
import sairepa.view.ErrorMessage;
import sairepa.view.SplashScreen;
import sairepa.view.View;
import sairepa.view.Viewer;

public class ActionSave implements ActionListener
{
  private Model model;
  private View view;
  private Controller controller;

  public ActionSave(Model model, View view, Controller controller) {
    this.model = model;
    this.view = view;
    this.controller = controller;
  }

  public static class ZipLhaFileFilter extends FileFilter {
    public ZipLhaFileFilter() { }
    public boolean accept(File file) {
      return (file.isDirectory()
	      || file.getName().toLowerCase().endsWith(".zip")
	      || file.getName().toLowerCase().endsWith(".lzh"));
    }

    public String getDescription() {
      return "Fichier ZIP/LZH";
    }
  }

  public static class DbaseFileFilter implements java.io.FileFilter {
    public DbaseFileFilter() { }

    public boolean accept(File f) {
      return (f.getName().toLowerCase().endsWith(".dbt")
	      || f.getName().toLowerCase().endsWith(".dbf"));
    }
  }

  private static void zip(ZipOutputStream zout, File f) throws IOException {
    zout.putNextEntry(new ZipEntry(f.getName()));
    FileInputStream in = new FileInputStream(f);

    try {
      byte data[] = new byte[32768];
      int r;
      while ((r = in.read(data)) >= 0) {
	zout.write(data, 0, r);
      }
    } finally {
      in.close();
    }
  }

  private void saveZip(File f) {
    ZipOutputStream zout;
    try {
      if (f.exists())
	f.delete();

      FileOutputStream fout = new FileOutputStream(f);
      zout = new ZipOutputStream(fout);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    try {
      for (File file : model.getProjectDir().listFiles(new DbaseFileFilter())) {
	zip(zout, file);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      try {
	zout.close();
      } catch (IOException e) {
	throw new RuntimeException(e);
      }
    }
  }

  private static void lha(LhaOutputStream zout, File f) throws IOException {
    zout.putNextEntry(new LhaHeader(f.getName()));
    FileInputStream in = new FileInputStream(f);

    try {
      byte data[] = new byte[32768];
      int r;
      while ((r = in.read(data)) >= 0) {
	zout.write(data, 0, r);
      }
    } finally {
      in.close();
    }
  }

  private void saveLha(File f) {
    LhaOutputStream zout;
    try {
      if (f.exists())
	f.delete();

      FileOutputStream fout = new FileOutputStream(f);
      zout = new LhaOutputStream(fout);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    try {
      for (File file : model.getProjectDir().listFiles(new DbaseFileFilter())) {
	lha(zout, file);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      try {
	zout.close();
      } catch (IOException e) {
	throw new RuntimeException(e);
      }
    }
  }

  public void save() {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setFileFilter(new ZipLhaFileFilter());
    fileChooser.setSelectedFile(new File(model.getProjectDir().getName() + ".zip"));
    if (fileChooser.showSaveDialog(view.getMainWindow()) != JFileChooser.APPROVE_OPTION)
      return;
    File f = fileChooser.getSelectedFile();

    SplashScreen ss = new SplashScreen("Sauvegarde");
    ss.start();
    try {
	ss.start();
	model.save(ss);

	ss.setProgression(90, "Creation du ZIP");
	if (f.getName().toLowerCase().endsWith(".lzh")) {
	    saveLha(f);
	} else {
	    saveZip(f);
	}
    } catch(Exception e) {
	throw new RuntimeException(e);
    } finally {
      ss.stop();
    }
  }

  public void actionPerformed(ActionEvent e) {
    save();
  }
}
