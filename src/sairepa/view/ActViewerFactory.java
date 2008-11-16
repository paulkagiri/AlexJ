package sairepa.view;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextField;

import sairepa.gui.IconBox;
import sairepa.model.ActList;

public class ActViewerFactory implements ViewerFactory
{
  public final static String NAME = "Actes";
  public final static ImageIcon ICON = IconBox.act;

  public ActViewerFactory() { }

  public String getName() {
    return NAME;
  }

  public ImageIcon getIcon() {
    return ICON;
  }

  /**
   * is a JPanel displaying one act at a time.
   */
  protected class ActViewer extends Viewer {
    public final static long serialVersionUID = 1;

    public ActViewer(ActList actList) {
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
    return new ActViewer(list);
  }
}
