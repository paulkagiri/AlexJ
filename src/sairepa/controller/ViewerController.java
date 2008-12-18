package sairepa.controller;

import javax.swing.JOptionPane;

import sairepa.model.Act;
import sairepa.model.Model;
import sairepa.view.ErrorMessage;
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
    String reason;

    if ( (reason = v.canClose()) == null ) {
      view.getMainWindow().removeViewer(v);
    } else {
      //ErrorMessage.displayError(reason);
      int ret = JOptionPane.showConfirmDialog(view.getMainWindow(),
					      reason + " Voulez-vous vraiment fermer cette tabulation ?",
					      "Etes-vous sur ?",
					      JOptionPane.YES_NO_OPTION);
      if (ret == JOptionPane.YES_OPTION) {
	view.getMainWindow().removeViewer(v);
      }
    }
  }
}
