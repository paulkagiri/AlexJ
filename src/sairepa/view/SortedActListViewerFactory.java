package sairepa.view;

import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextField;

import sairepa.model.ActList;
import sairepa.model.ActListFactory;
import sairepa.model.InMemoryActList;

public class SortedActListViewerFactory implements ViewerFactory
{
	public final static String NAME = "Vue triée";

	public SortedActListViewerFactory() { }

	public String getName() {
		return NAME;
	}

	public Viewer createViewer(MainWindow mainWindow, ActList list) {
		return new SortedActListViewer(list);
	}
}

