package sairepa.view;

import sairepa.model.ActList;
import sairepa.model.ActListFactory;
import sairepa.model.InMemoryActList;

public class ActListViewerFactory implements ViewerFactory
{
  public final static String NAME = "Tableau";

  public ActListViewerFactory() { }

  public String getName() {
    return NAME;
  }

  public Viewer createViewer(MainWindow mainWindow, ActList list) {
      ActList al = InMemoryActList.encapsulate(list);
      return new ActListViewer(al, true /* allow reordering */);
  }
}
