package sairepa.view;

import javax.swing.ImageIcon;

import sairepa.gui.IconBox;
import sairepa.model.ActList;

public class ActListViewerFactory implements ViewerFactory
{
  public ActListViewerFactory() { }

  public String getName() {
    return "Tableau";
  }

  public ImageIcon getIcon() {
    return IconBox.actList;
  }

  public Viewer createViewer(ActList list) {
    // TODO
    return null;
  }
}
