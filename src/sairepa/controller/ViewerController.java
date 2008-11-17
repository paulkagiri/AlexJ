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

  /**
   * should add it to the model and start a new one in the view
   */
  public void newAct(Viewer v, Act a) {
    System.out.println("newAct() : TODO");
  }

  /**
   * must update the model if ack
   * and next call refresh()
   */
  public void actChanged(Viewer v, Act a) {
    System.out.println("actChanged() : TODO");
  }

  public void actDeleted(Viewer v, Act a) {
    System.out.println("actDeleted() : TODO");
  }


  public void viewerClosing(Viewer v) {
    view.getMainWindow().removeViewer(v);
  }
}
