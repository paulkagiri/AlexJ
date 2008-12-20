package sairepa.view;

import javax.swing.ImageIcon;

import sairepa.model.ActList;
import sairepa.model.ActListFactory;

public interface ViewerFactory
{
  public String getName();
  public ImageIcon getIcon();

  /**
   * let the viewer ask the factory for the more adapted ActList.
   */
  public ActList extractActList(ActListFactory factory);

  /**
   * @param mainWindow just provided if you must create
   *        a dialog, don't touch it directly otherwise.
   */
  public Viewer createViewer(MainWindow mainWindow, ActList list);
}
