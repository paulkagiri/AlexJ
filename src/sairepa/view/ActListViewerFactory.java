package sairepa.view;

import javax.swing.ImageIcon;

import sairepa.gui.IconBox;
import sairepa.model.ActList;
import sairepa.model.ActListFactory;
import sairepa.model.InMemoryActList;

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

  public ActList extractActList(ActListFactory factory) {
    return factory.getActList();
  }

  public Viewer createViewer(MainWindow mainWindow, ActList list) {
      ActList al = InMemoryActList.encapsulate(list, new SplashScreen.DbObserver());
      return new ActListViewer(al);
  }
}
