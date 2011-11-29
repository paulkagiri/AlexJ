package sairepa.view;

import sairepa.model.ActList;
import sairepa.model.ActListFactory;

public interface ViewerFactory
{
  public String getName();

  /**
   * @param mainWindow just provided if you must create
   *        a dialog, don't touch it directly otherwise.
   */
  public Viewer createViewer(MainWindow mainWindow, ActList list);
}
