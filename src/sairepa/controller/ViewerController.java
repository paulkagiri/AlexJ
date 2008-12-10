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
    refreshAllViewers(v);
    return true;
  }

  public boolean insertingAct(Viewer v, Act a, int row) {
    if (!a.validate()) {
      return false;
    }
    v.getActList().insert(a, row);
    refreshAllViewers(v);
    return true;
  }

  public boolean changingAct(Viewer v, Act a) {
    if (!a.validate()) {
      return false;
    }
    a.update();
    refreshAllViewers(v);
    return true;
  }

  public boolean deletingAct(Viewer v, Act a) {
    v.getActList().delete(a);
    refreshAllViewers(v);
    return true;
  }

  private void refreshAllViewers(Viewer exception) {
    for (Viewer v : view.getMainWindow().getViewers()) {
      if (v != exception) {
	v.getActList().refresh();
	v.refresh();
      }
    }
  }

  public void viewerClosing(Viewer v) {
    if (v.canClose()) {
      view.getMainWindow().removeViewer(v);
    }
  }
}
