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
  public Viewer createViewer(ActList list);
}
