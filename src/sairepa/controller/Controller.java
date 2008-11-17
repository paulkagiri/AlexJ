package sairepa.controller;

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

  public void init() {
    view.getMainWindow().addWindowListener(
        new ActionQuit(model, view, this));
    view.getMainWindow().getQuitButton().addActionListener(
	new ActionQuit(model, view, this));
    view.getMainWindow().getTabSelecter().addObserver(
        new TabController(model, view));
  }

  public void close() {

  }
}
