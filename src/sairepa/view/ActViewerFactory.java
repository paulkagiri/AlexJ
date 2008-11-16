package sairepa.view;

import javax.swing.ImageIcon;

import sairepa.gui.IconBox;
import sairepa.model.ActList;

public class ActViewerFactory implements ViewerFactory
{
  public ActViewerFactory() { }

  public String getName() {
    return "Actes";
  }

  public ImageIcon getIcon() {
    return IconBox.act;
  }

  public Viewer createViewer(ActList list) {
    // TODO
    return null;
  }
}
