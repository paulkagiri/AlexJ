package sairepa.controller;

import javax.swing.JOptionPane;

import sairepa.model.Act;
import sairepa.model.ActListFactory;
import sairepa.model.Model;

import sairepa.view.ErrorMessage;
import sairepa.view.View;
import sairepa.view.Viewer;
import sairepa.view.ViewerFactory;

public class ViewerController implements Viewer.ViewerObserver
{
  private Controller controller;
  private Model model;
  private View view;

  public ViewerController(Model model, View view, Controller controller) {
    this.model = model;
    this.view = view;
    this.controller = controller;
  }

  public boolean creatingAct(Viewer v, Act a) {
    if (!a.validate()) {
      return false;
    }
    v.getActList().insert(a);
    refreshAllViewers(v, a);
    return true;
  }

  public boolean insertingAct(Viewer v, Act a, int row) {
    if (!a.validate()) {
      return false;
    }
    v.getActList().insert(a, row);
    refreshAllViewers(v, null);
    return true;
  }

  public boolean changingAct(Viewer v, Act a) {
    if (!a.validate()) {
      return false;
    }
    a.update();
    refreshAllViewers(v, a);
    return true;
  }

  public boolean deletingAct(Viewer v, Act a) {
    v.getActList().delete(a);
    refreshAllViewers(v, null);
    return true;
  }

    private void refreshAllViewers(Viewer exception, Act a) {
    for (Viewer v : view.getMainWindow().getViewers()) {
      if (v != exception) {
	  if ( a == null ) {
	      v.getActList().refresh();
	      v.refresh();
	  } else {
	      v.getActList().refresh(a);
	      v.refresh(a);
	  }
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
					      reason + " Voulez-vous vraiment fermer cet onglet ?",
					      "Etes-vous sur ?",
					      JOptionPane.YES_NO_OPTION);
      if (ret == JOptionPane.YES_OPTION) {
	view.getMainWindow().removeViewer(v);
      }
    }
  }

  public void requestViewerOpening(Viewer v, ViewerFactory vf, ActListFactory af) {
    controller.getTabController().requestTabOpening(af, vf);
  }

  public void requestViewerOpening(Viewer v, ViewerFactory vf, ActListFactory af, int actNumber) {
    Viewer nv = controller.getTabController().requestTabOpening(af, vf);
    nv.setSelectedAct(actNumber);
  }
}
