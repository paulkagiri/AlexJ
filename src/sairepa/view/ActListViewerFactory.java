package sairepa.view;

import javax.swing.ImageIcon;

import sairepa.gui.IconBox;
import sairepa.model.ActList;

public class ActListViewerFactory implements ViewerFactory
{
  public final static String NAME = "Tableau";
  public final static ImageIcon ICON = IconBox.actList;

  public ActListViewerFactory() { }

  public String getName() {
    return NAME;
  }

  public ImageIcon getIcon() {
    return ICON;
  }

  public Viewer createViewer(ActList list) {
    return new ActListViewer(list);
  }
}
