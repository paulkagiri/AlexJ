package sairepa.controller;

import javax.swing.AbstractButton;
import javax.swing.JFrame;

import sairepa.model.Model;
import sairepa.view.View;

public class Controller
{
  private Model model;
  private View view;

  public Controller(Model model, View view) {
    this.model = model;
    this.view = view;
  }

  private TabController tabController;

  public TabController getTabController() {
    return tabController;
  }

  public void init() {
    view.getMainWindow().addWindowListener(
        new ActionQuit(model, view, this));
    view.getMainWindow().getQuitButton().addActionListener(
	new ActionQuit(model, view, this));
    view.getMainWindow().getOpenButton().addActionListener(
	new ActionOpen(model, view, this));
    view.getMainWindow().getSaveButton().addActionListener(
	new ActionSave(model, view, this));
    view.getMainWindow().setDefaultCloseOperation(
	JFrame.DO_NOTHING_ON_CLOSE);

    view.getMainWindow().getTabSelecter().addObserver(
        tabController = new TabController(model, view, this));

    ActionPrint actionPrint = new ActionPrint(model, view, this);
    view.getMainWindow().addTabObserver(actionPrint);
    view.getMainWindow().getPrintButton().addActionListener(actionPrint);

    ActionSearch actionSearch = new ActionSearch(model, view, this);
    view.getMainWindow().addTabObserver(actionSearch);
    view.getMainWindow().getSearchButton().addActionListener(actionSearch);

    ActionRestore actionRestore = new ActionRestore(model, view, this);
    for (AbstractButton b : view.getMainWindow().getRestoreButtons())
      b.addActionListener(actionRestore);
  }

  public void close() {

  }
}
