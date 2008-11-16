package sairepa.controller;

import sairepa.model.ActListFactory;
import sairepa.model.Model;
import sairepa.model.Util;
import sairepa.view.TabSelecter;
import sairepa.view.View;
import sairepa.view.Viewer;
import sairepa.view.ViewerFactory;

public class TabManager implements TabSelecter.TabSelecterObserver
{
  private Model model;
  private View view;

  public TabManager(Model model, View view) {
    this.model = model;
    this.view = view;
  }

  public void tabSelected(ActListFactory actListFactory, ViewerFactory viewerFactory) {
    Viewer v = viewerFactory.createViewer(actListFactory.getList());
    Util.check(v != null);
    view.getMainWindow().addViewer(v);
  }
}
