package sairepa.controller;

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

  public void init() {
    view.getMainWindow().addWindowListener(
        new ActionQuit(model, view, this));
    view.getMainWindow().getQuitButton().addActionListener(
	new ActionQuit(model, view, this));
    view.getMainWindow().setDefaultCloseOperation(
	JFrame.DO_NOTHING_ON_CLOSE);

    view.getMainWindow().getTabSelecter().addObserver(
        new TabController(model, view));

    ActionPrint actionPrint = new ActionPrint(model, view, this);
    view.getMainWindow().addTabObserver(actionPrint);
    view.getMainWindow().getPrintButton().addActionListener(actionPrint);
  }

  public void close() {

  }
}
