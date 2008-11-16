package sairepa.view;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import sairepa.gui.CloseableTabbedPane;

public abstract class Viewer extends JPanel implements CloseableTabbedPane.CloseableTab
{
  private String factoryName;
  private String viewerName;
  private ImageIcon icon;

  public Viewer(String factoryName, String viewerName, ImageIcon icon) {
    this.factoryName = factoryName;
    this.viewerName = viewerName;
    this.icon = icon;
  }

  public ImageIcon getIcon() {
    return icon;
  }

  public String getName() {
    return factoryName + "/" + viewerName;
  }

  public abstract void refresh();
  public abstract void close();
}
