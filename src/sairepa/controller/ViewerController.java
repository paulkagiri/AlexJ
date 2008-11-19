package sairepa.controller;

import sairepa.model.Act;
import sairepa.model.Model;
import sairepa.view.View;
import sairepa.view.Viewer;

public class ViewerController implements Viewer.ViewerObserver
{
  private Model model;
  private View view;

  public ViewerController(Model model, View view) {
    this.model = model;
    this.view = view;
  }

  public void creatingAct(Viewer v, Act a) {
    v.getActList().insert(a);
    refreshAllViewers();
  }

  public void changingAct(Viewer v, Act a) {
    a.update();
    refreshAllViewers();
  }

  public void deletingAct(Viewer v, Act a) {
    v.getActList().delete(a);
    refreshAllViewers();
  }

  private void refreshAllViewers() {
    for (Viewer v : view.getMainWindow().getViewers()) {
      v.refresh();
    }
  }

  public void viewerClosing(Viewer v) {
    view.getMainWindow().removeViewer(v);
  }
}
