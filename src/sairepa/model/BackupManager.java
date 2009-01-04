package sairepa.model;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import java.io.*;
import java.nio.channels.*;

public class BackupManager
{
  public final static String BACKUP_DIR_NAME = "Sauvegardes";
  public final static int NMB_BACKUPS = 5;

  private final File projectDir;
  private final File backupDir;
  private static final DateFormat FILE_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");

  public BackupManager(File projectDir) {
    this.projectDir = projectDir;
    this.backupDir = new File(projectDir, BACKUP_DIR_NAME);
    if ( (backupDir.exists() && !backupDir.isDirectory())
	 || (!backupDir.exists() && !backupDir.mkdir()) )
      throw new RuntimeException("Ne peut pas creer le repertoire pour les sauvegardes '" + backupDir.getPath() + "'");
  }

  private List<Date> availableBackups;

  public void init() {
    availableBackups = new Vector<Date>();
    File[] backupDirs = backupDir.listFiles(new Util.DirectoryFilter());
    for (int i = 0 ; i < backupDirs.length ; i++) {
      try {
	Date d = FILE_DATE_FORMAT.parse(backupDirs[i].getName());
	availableBackups.add(d);
      } catch (ParseException e) {
	System.err.println("WARNING: Not a backup directory: " + backupDirs[i].getPath());
      }
    }

    java.util.Collections.sort(availableBackups);

    while(availableBackups.size() > NMB_BACKUPS) {
      Date d = availableBackups.get(0);
      delete(new File(backupDir, FILE_DATE_FORMAT.format(d)));
      availableBackups.remove(d);
    }
  }

  public List<Date> getAvailableBackups() {
    return availableBackups;
  }

  private void delete(File f) {
    if (f.isDirectory()) {
      File[] fs = f.listFiles();
      for (File sf : fs) {
	if (!".".equals(sf.getName())
	    && !"..".equals(sf.getName()))
	  delete(sf);
      }
    }

    if (!f.delete())
      throw new RuntimeException("Ne peut effacer une des precedente sauvegardes: " + f.getPath());
  }

  private void copy(File srcFile, File dstFile) {
    FileChannel inChannel = null;
    FileChannel outChannel = null;

    try {
      inChannel = new FileInputStream(srcFile).getChannel();
      outChannel = new FileOutputStream(dstFile).getChannel();
      inChannel.transferTo(0, inChannel.size(), outChannel);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      if (inChannel != null)  try { inChannel.close();  } catch (IOException e) { }
      if (outChannel != null) try { outChannel.close(); } catch (IOException e) { }
    }
  }

  public void doBackup() {
    String backupName = FILE_DATE_FORMAT.format(new Date());
    File bDir = new File(backupDir, backupName);

    if (bDir.exists()) {
      System.err.println("WARNING: Erasing a previous backup made today");
      delete(bDir);
    }

    if (!bDir.mkdir())
      throw new RuntimeException("Ne peut creer le repertoire pour la sauvegarde: " + bDir.getPath());

    File[] srcFiles = projectDir.listFiles(new Util.NonDirectoryFilter());
    for (int i = 0 ; i < srcFiles.length ; i++) {
      System.out.println("Backuping '" + srcFiles[i].getPath() + "'");
      copy(srcFiles[i], new File(bDir, srcFiles[i].getName()));
    }
  }

  /**
   * Model must be closed before calling this function !
   */
  public void restore(Date d, ProgressionObserver obs) {
    System.out.println("Restoring from backup: " + d.toString());
    obs.setProgression(0, "Restauration de: " + d.toString());
    String backupName = FILE_DATE_FORMAT.format(d);
    File bDir = new File(backupDir, backupName);

    File[] input = bDir.listFiles(new Util.NonDirectoryFilter());
    for (int i = 0 ; i < input.length ; i++) {
      obs.setProgression(i * 99 / input.length,
			 "Restauration de: " + input[i].getName());
      File output = new File(projectDir, input[i].getName());
      if (output.exists())
	delete(output);
      copy(input[i], output);
    }

    obs.setProgression(100, "Restauration finie");
  }
}
