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

  /**
   * is a JPanel displaying a table with all the act in the list
   */
  protected class ActListViewer extends Viewer {
    public final static long serialVersionUID = 1;

    public ActListViewer(ActList actList) {
      super(actList.getName(), NAME, ICON);
    }

    public void refresh() {
      System.err.println("TODO");
    }

    public void close() {
      System.err.println("TODO");
    }
  }

  public Viewer createViewer(ActList list) {
    return new ActListViewer(list);
  }
}
