package sairepa.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
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
		super(actList, SortedActListViewerFactory.NAME);

		Viewer v = new SortingChoosers(actList);
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

	private class SortingChooser {
		private final JComboBox fieldChooser;
		private final JCheckBox orderChooser;
		private final JButton removeButton;

		public SortingChooser(Vector<ActField> possibleFields) {
			fieldChooser = new JComboBox(possibleFields);
			fieldChooser.setPreferredSize(new java.awt.Dimension(300, 20));
			orderChooser = new JCheckBox("", false);
			removeButton = new JButton(IconBox.remove);
			removeButton.setToolTipText("Retirer ce critère de tri");
		}

		public JComponent getFieldChooser() {
			return fieldChooser;
		}

		public JComponent getOrderChooser() {
			return orderChooser;
		}

		public JButton getRemoveButton() {
			return removeButton;
		}

		public ActField getField() {
			return (ActField)fieldChooser.getSelectedItem();
		}

		public boolean getOrder() {
			return orderChooser.isSelected();
		}
	}

	private class SortingChoosers extends Viewer implements ActionListener {
		private final static long serialVersionUID = 1;

		private final ActList actList;
		private final Vector<ActField> possibleFields;

		private List<SortingChooser> choosers;
		private JButton addButton;
		private JButton validButton;

		public SortingChoosers(ActList al) {
			super(al, SortedActListViewerFactory.NAME);
			this.actList = al;
			choosers = new Vector<SortingChooser>();

			possibleFields = new Vector<ActField>();
			for (ActField af : actList.getFields())
				possibleFields.add(af);
			Collections.sort(possibleFields);

			SortingChooser chooser = new SortingChooser(this.possibleFields);
			choosers.add(chooser);

			rebuildUI();
		}

		private void rebuildUI() {
			this.removeAll();
			this.setLayout(new GridLayout(1,1));

			JPanel omegaPanel = new JPanel();

			omegaPanel.setLayout(new BorderLayout(5, 5));

			JPanel masterPanel = new JPanel(new BorderLayout(5, 5));
			JPanel rightPanel = new JPanel(new BorderLayout(5, 5));

			JPanel orderColumn = new JPanel(new GridLayout(choosers.size() + 3, 1));
			JPanel fieldColumn = new JPanel(new GridLayout(choosers.size() + 3, 1));
			JPanel removeColumn = new JPanel(new GridLayout(choosers.size() + 3, 1));

			/* header */
			fieldColumn.add(new JLabel("Trier par"));
			orderColumn.add(new JLabel("Décroissant"));
			removeColumn.add(new JLabel(""));
			/* choosers */
			int i = 0;
			for (SortingChooser chooser : choosers) {
				orderColumn.add(chooser.getOrderChooser());
				fieldColumn.add(chooser.getFieldChooser());

				if (i == 0)
					removeColumn.add(new JLabel(""));
				else {
					removeColumn.add(chooser.getRemoveButton());
					chooser.getRemoveButton().addActionListener(this);
				}
				i++;
			}

			/* validation / adding */
			fieldColumn.add(new JLabel(""));
			orderColumn.add(new JLabel(""));
			this.addButton = new JButton(IconBox.add);
			addButton.setToolTipText("Ajouter un nouveau critère de tri");
			addButton.addActionListener(this);
			removeColumn.add(addButton);

			orderColumn.add(new JLabel(""));
			this.validButton = new JButton("Valider");
			validButton.addActionListener(this);
			fieldColumn.add(validButton);
			removeColumn.add(new JLabel(""));

			masterPanel.add(fieldColumn, BorderLayout.WEST);
			rightPanel.add(orderColumn, BorderLayout.WEST);
			rightPanel.add(removeColumn, BorderLayout.EAST);
			masterPanel.add(rightPanel, BorderLayout.EAST);

			omegaPanel.add(masterPanel, BorderLayout.NORTH);
			omegaPanel.add(new JLabel(""), BorderLayout.CENTER);
			this.add(omegaPanel);

			this.validate();
		}

		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == validButton) {
				// TODO
			} else if (e.getSource() == addButton) {
				SortingChooser chooser = new SortingChooser(this.possibleFields);
				choosers.add(chooser);
				rebuildUI();
			} else {
				for (SortingChooser chooser : choosers) {
					if (e.getSource() == chooser.getRemoveButton()) {
						choosers.remove(chooser);
						rebuildUI();
						break;
					}
				}
			}
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

