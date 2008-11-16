package sairepa.view;

import javax.swing.ImageIcon;

import sairepa.model.ActList;

public interface ViewerFactory
{
  public String getName();
  public ImageIcon getIcon();
  public Viewer createViewer(ActList list);
}
