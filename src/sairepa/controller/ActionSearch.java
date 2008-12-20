package sairepa.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import sairepa.model.Model;
import sairepa.view.ErrorMessage;
import sairepa.view.MainWindow;
import sairepa.view.View;
import sairepa.view.Viewer;

public class ActionSearch implements ActionListener, MainWindow.TabObserver
{
  private Model model;
  private View view;
  private Controller controller;

  public ActionSearch(Model model, View view, Controller controller) {
    this.model = model;
    this.view = view;
    this.controller = controller;
  }

  private Viewer selectedViewer;

  public void actionPerformed(ActionEvent e) {
    selectedViewer.displaySearchForm();
  }

  public void tabSelected(Viewer v) {
    selectedViewer = v;
    view.getMainWindow().getSearchButton().setEnabled(v.canBeSearched());
  }

  public void allTabClosed() {
    view.getMainWindow().getSearchButton().setEnabled(false);
  }
}
