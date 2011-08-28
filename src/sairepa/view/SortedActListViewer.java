package sairepa.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import sairepa.gui.IconBox;
import sairepa.gui.RightClickMenu;
import sairepa.gui.Table;
import sairepa.model.Act;
import sairepa.model.ActEntry;
import sairepa.model.ActField;
import sairepa.model.ActList;
import sairepa.model.ActListFactory;
import sairepa.model.Util;

public class SortedActListViewer extends Viewer
{
	private final static long serialVersionUID = 1;
	
	private Viewer currentViewer = null;

	public SortedActListViewer(ActList actList) {
		super(actList,
				SortedActListViewerFactory.NAME,
				SortedActListViewerFactory.ICON);

		Viewer v = new SortingChooser(actList);
		setViewer(v);
	}

	private void setViewer(Viewer viewer) {
		for (ViewerObserver obs : this.getObservers() ) {
			viewer.addObserver(obs);
		}

		this.removeAll();
		this.add(viewer);

		this.currentViewer = viewer;
	}

	@Override
	public void init() {
		super.init();
		currentViewer.init();
	}

	@Override
	public void refresh() {
		currentViewer.refresh();
	}

	@Override
	public void refresh(Act a) {
		currentViewer.refresh(a);
	}

	@Override
	public String canClose() {
		return currentViewer.canClose();
	}

	@Override
	public int[] getSelectedActs() {
		return currentViewer.getSelectedActs();
	}

	@Override
	public void setSelectedAct(int act) {
		currentViewer.setSelectedAct(act);
	}

	@Override
	public void addObserver(ViewerObserver obs) {
		super.addObserver(obs);
		currentViewer.addObserver(obs);
	}

	@Override
	public void deleteObserver(ViewerObserver obs) {
		super.deleteObserver(obs);
		currentViewer.deleteObserver(obs);
	}

	@Override
	public void close() {
		currentViewer.close();
	}

	@Override
	public boolean canBePrinted() {
		return currentViewer.canBePrinted();
	}
	
	@Override
	public JComponent getPrintableComponent() {
		return currentViewer.getPrintableComponent();
	}

	@Override
	public boolean printOnOnePage() {
		return currentViewer.printOnOnePage();
	}

	@Override
	public void printingDone() {
		currentViewer.printingDone();
	}

	@Override
	public boolean canBeSearched() {
		return currentViewer.canBeSearched();
	}

	@Override
	public void displaySearchForm() {
		currentViewer.displaySearchForm();
	}

	private class SortingChooser extends Viewer {
		private final static long serialVersionUID = 1;

		private final ActList actList;

		public SortingChooser(ActList al) {
			super(al,
					SortedActListViewerFactory.NAME,
					SortedActListViewerFactory.ICON);
			this.actList = al;
		}

		@Override
		public void refresh() {
			// Nothing to do
		}

		@Override
		public void refresh(Act a) {
			// Nothing to do
		}

		@Override
		public String canClose() {
			return null;
		}

		@Override
		public int[] getSelectedActs() {
			return new int[0];
		}

		@Override
		public void setSelectedAct(int act) {
			throw new UnsupportedOperationException("Can't display act");
		}

		@Override
		public void close() {
			super.close();
			// Nothing to do
		}

		@Override
		public boolean canBePrinted() {
			return false;
		}
		
		@Override
		public JComponent getPrintableComponent() {
			throw new UnsupportedOperationException("Can't print the sorting selection dialog");
		}

		@Override
		public boolean printOnOnePage() {
			return false;
		}

		@Override
		public void printingDone() {
			super.printingDone();
		}

		@Override
		public boolean canBeSearched() {
			return false;
		}

		@Override
		public void displaySearchForm() {
			currentViewer.displaySearchForm();
		}
	}
}

