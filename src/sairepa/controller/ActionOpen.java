package sairepa.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import jp.gr.java_conf.dangan.util.lha.LhaFile;
import jp.gr.java_conf.dangan.util.lha.LhaHeader;

import sairepa.model.Model;
import sairepa.model.ProgressionObserver;
import sairepa.model.Util;
import sairepa.view.ErrorMessage;
import sairepa.view.SplashScreen;
import sairepa.view.View;
import sairepa.view.Viewer;

public class ActionOpen implements ActionListener
{
  private Model model;
  private View view;
  private Controller controller;

  public ActionOpen(Model model, View view, Controller controller) {
    this.model = model;
    this.view = view;
    this.controller = controller;
  }

  private void openZip(File f) throws IOException {
    ZipFile zip = new ZipFile(f);

    try {
      Enumeration<? extends ZipEntry> entries = zip.entries();

      while (entries.hasMoreElements()) {
	ZipEntry entry = entries.nextElement();
	InputStream in = zip.getInputStream(entry);
	try {
	  FileOutputStream out = new FileOutputStream(Util.getFile(model.getProjectDir(), entry.getName()));
	  try {
	    byte[] buffer = new byte[32768];
	    int nBytes;

	    while( (nBytes = in.read(buffer)) > 0) {
	      out.write(buffer, 0, nBytes);
	    }
	  } finally {
	    out.close();
	  }
	} finally {
	  in.close();
	}
      }
    } finally {
      zip.close();
    }
  }

  private void openLha(File f) throws IOException {
    LhaFile lha = new LhaFile(f);

    try {
      Enumeration entries = lha.entries();

      while (entries.hasMoreElements()) {
	LhaHeader entry = ((LhaHeader)entries.nextElement());
	InputStream in = lha.getInputStream(entry);
	try {
	  FileOutputStream out = new FileOutputStream(Util.getFile(model.getProjectDir(), entry.getPath()));
	  try {
	    byte[] buffer = new byte[32768];
	    int nBytes;

	    while( (nBytes = in.read(buffer)) > 0) {
	      out.write(buffer, 0, nBytes);
	    }
	  } finally {
	    out.close();
	  }
	} finally {
	  in.close();
	}
      }
    } finally {
      lha.close();
    }
  }

  public void open() {
    if (JOptionPane.showConfirmDialog(view.getMainWindow(),
				      "Attention, la restauration ecrasera tout vos changements. Etes-vous sur ?",
				      "Etes-vous sur ?",
				      JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION)
      return;

    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setFileFilter(new ActionSave.ZipLhaFileFilter());
    if (fileChooser.showOpenDialog(view.getMainWindow()) != JFileChooser.APPROVE_OPTION)
      return;
    File f = fileChooser.getSelectedFile();

    view.getMainWindow().closeAllViewers();
    view.getMainWindow().setVisible(false);

    SplashScreen ss = new SplashScreen("Ouverture");
    ss.start();
    ss.setProgression(0, "Unziping");
    try {
	model.close(ProgressionObserver.DUMB_OBSERVER);

	if (f.getName().toLowerCase().endsWith(".lzh")) {
	    openLha(f);
	} else {
	    openZip(f);
	}

	model.init(ss);
    } catch(Exception e) { /* wouldn't miss the runtime ones also */
	ErrorMessage.displayError(e);
	throw new RuntimeException(e);
    } finally {
	ss.stop();
    }

    view.getMainWindow().setVisible(true);
  }

  public void actionPerformed(ActionEvent e) {
    open();
  }
}
