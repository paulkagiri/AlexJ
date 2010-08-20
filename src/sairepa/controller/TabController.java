package sairepa.controller;

import sairepa.model.ActListFactory;
import sairepa.model.Model;
import sairepa.model.Util;
import sairepa.view.TabSelecter;
import sairepa.view.View;
import sairepa.view.Viewer;
import sairepa.view.ViewerFactory;

/**
 * Controls the Tab selecter.
 */
public class TabController implements TabSelecter.TabSelecterObserver
{
	private Model model;
	private View view;
	private Controller controller;

	public TabController(Model model, View view, Controller controller) {
		this.model = model;
		this.view = view;
		this.controller = controller;
	}

	public Viewer requestTabOpening(ActListFactory actListFactory, ViewerFactory viewerFactory) {
		Viewer v = viewerFactory.createViewer(view.getMainWindow(),
				viewerFactory.extractActList(actListFactory));
		Util.check(v != null);
		v.addObserver(new ViewerController(model, view, controller));
		view.getMainWindow().addViewer(v);
		view.getMainWindow().selectViewer(v);
		v.init();
		return v;
	}
}
