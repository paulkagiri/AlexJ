package sairepa.view;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextField;

import sairepa.gui.IconBox;
import sairepa.model.ActList;
import sairepa.model.ActListFactory;
import sairepa.model.InMemoryActList;

public class SortedActListViewerFactory implements ViewerFactory
{
	public final static String NAME = "Vue triée";
	public final static ImageIcon ICON = IconBox.actList;

	public SortedActListViewerFactory() { }

	public String getName() {
		return NAME;
	}

	public ImageIcon getIcon() {
		return ICON;
	}

	public ActList extractActList(ActListFactory factory) {
		return factory.getActList();
	}

	public Viewer createViewer(MainWindow mainWindow, ActList list) {
		return new SortedActListViewer(list);
	}
}

