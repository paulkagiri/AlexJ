package sairepa.view;

import java.util.List;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import sairepa.model.Act;
import sairepa.model.ActList;
import sairepa.model.ActListFactory;
import sairepa.gui.CloseableTabbedPane;

public abstract class Viewer extends JPanel implements CloseableTabbedPane.CloseableTab
{
  private String factoryName;
  private String viewerName;
  private ImageIcon icon;
  private List<ViewerObserver> observers;
  private ActList actList;

  public Viewer(ActList actList, String viewerName, ImageIcon icon) {
    this.factoryName = actList.getName();
    this.viewerName = viewerName;
    this.icon = icon;
    this.actList = actList;
    observers = new Vector<ViewerObserver>();
  }

  public void init() {

  }

  public static interface ViewerObserver {
    /**
     * should add it to the model
     */
    public boolean creatingAct(Viewer v, Act a);

    /**
     * must update the model
     * and next call refresh() on all viewers
     */
    public boolean changingAct(Viewer v, Act a);

    public boolean deletingAct(Viewer v, Act a);

    /**
     * must remove from the main window if ack
     */
    public void viewerClosing(Viewer v);
  }

  public ImageIcon getIcon() {
    return icon;
  }

  public String getName() {
    return factoryName + "/" + viewerName;
  }

  public abstract void refresh();
  public abstract boolean canClose();

  public ActList getActList() {
    return actList;
  }

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
