package sairepa.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import sairepa.model.Model;
import sairepa.view.ErrorMessage;
import sairepa.view.MainWindow;
import sairepa.view.PrintingManager;
import sairepa.view.View;
import sairepa.view.Viewer;

public class ActionPrint implements ActionListener, MainWindow.TabObserver
{
  private Model model;
  private View view;
  private Controller controller;

  public ActionPrint(Model model, View view, Controller controller) {
    this.model = model;
    this.view = view;
    this.controller = controller;
  }

  private Viewer selectedViewer;

  public void actionPerformed(ActionEvent e) {
    PrintingManager.print(selectedViewer.getPrintableComponent(),
			  selectedViewer.getPrintableName(),
			  selectedViewer.printOnOnePage());
    selectedViewer.printingDone();
  }

  public void tabSelected(Viewer v) {
    selectedViewer = v;
    view.getMainWindow().getPrintButton().setEnabled(v.canBePrinted());
  }

  public void allTabClosed() {
    view.getMainWindow().getPrintButton().setEnabled(false);
  }
}
