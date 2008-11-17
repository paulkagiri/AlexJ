package sairepa.controller;

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

  public void viewerClosing(Viewer v) {
    view.getMainWindow().removeViewer(v);
  }
}
