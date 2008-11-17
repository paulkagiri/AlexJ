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

  public Viewer createViewer(ActList list) {
    return new ActViewer(list, NAME, ICON);
  }
}
