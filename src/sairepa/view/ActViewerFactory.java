package sairepa.view;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextField;

import sairepa.gui.IconBox;
import sairepa.model.ActList;
import sairepa.model.ActListFactory;

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

  public ActList extractActList(ActListFactory factory) {
    return factory.getActList();
  }

  public Viewer createViewer(ActList list) {
    return new ActViewer(list);
  }
}
