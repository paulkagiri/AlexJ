package sairepa.view;

import java.util.List;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;

import sairepa.model.Act;
import sairepa.model.ActList;
import sairepa.model.ActListFactory;
import sairepa.gui.CloseableTabbedPane;

public abstract class Viewer extends JPanel implements CloseableTabbedPane.CloseableTab
{
  private final String factoryName;
  private final String viewerName;
  private final ImageIcon icon;
  private List<ViewerObserver> observers;
   private final ActList actList;

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

    public boolean insertingAct(Viewer v, Act a, int row);

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

    public void requestViewerOpening(Viewer v, ViewerFactory vf, ActListFactory af);
    public void requestViewerOpening(Viewer v, ViewerFactory vf, ActListFactory af, int actNumber);
  }

  public ImageIcon getIcon() {
    return icon;
  }

  public String getName() {
    return factoryName + "/" + viewerName;
  }

  public String getPrintableName() {
    String ret = factoryName;

    if (ret.endsWith("s")) {
      ret = ret.substring(0, ret.length()-1);
    }

    return ret;
  }

  public abstract void refresh();
  public abstract void refresh(Act a);

  /**
   * @return null if it can, else the reason why not
   */
  public abstract String canClose();

  public abstract int[] getSelectedActs();
  public abstract void setSelectedAct(int act);

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

  public boolean canBePrinted() {
    return false;
  }

  public JComponent getPrintableComponent() {
    return null;
  }

  public boolean printOnOnePage() {
    return true;
  }

  public void printingDone() {

  }

  public boolean canBeSearched() {
    return false;
  }

  public void displaySearchForm() {

  }
}
