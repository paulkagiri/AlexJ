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

  public boolean creatingAct(Viewer v, Act a) {
    if (!a.validate()) {
      return false;
    }
    v.getActList().insert(a);
    refreshAllViewers();
    return true;
  }

  public boolean changingAct(Viewer v, Act a) {
    if (!a.validate()) {
      return false;
    }
    a.update();
    refreshAllViewers();
    return true;
  }

  public boolean deletingAct(Viewer v, Act a) {
    if (!a.validate()) {
      return false;
    }
    v.getActList().delete(a);
    refreshAllViewers();
    return true;
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
