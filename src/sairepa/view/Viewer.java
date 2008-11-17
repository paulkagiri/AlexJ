package sairepa.view;

import java.util.List;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import sairepa.gui.CloseableTabbedPane;

public abstract class Viewer extends JPanel implements CloseableTabbedPane.CloseableTab
{
  private String factoryName;
  private String viewerName;
  private ImageIcon icon;
  private List<ViewerObserver> observers;

  public Viewer(String factoryName, String viewerName, ImageIcon icon) {
    this.factoryName = factoryName;
    this.viewerName = viewerName;
    this.icon = icon;
    observers = new Vector<ViewerObserver>();
  }

  public static interface ViewerObserver {
    public void viewerClosing(Viewer v);
  }

  public ImageIcon getIcon() {
    return icon;
  }

  public String getName() {
    return factoryName + "/" + viewerName;
  }

  public abstract void refresh();


  public void addObserver(ViewerObserver obs) {
    observers.add(obs);
  }

  public void deleteObserver(ViewerObserver obs) {
    observers.remove(obs);
  }

  public List<ViewerObserver> getObservers() {
    return observers;
  }

  public void close() {
    for (ViewerObserver obs : observers) {
      obs.viewerClosing(this);
    }
  }
}
