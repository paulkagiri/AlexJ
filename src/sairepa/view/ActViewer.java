package sairepa.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.text.JTextComponent;

import sairepa.gui.RightClickMenu;
import sairepa.model.Act;
import sairepa.model.ActEntry;
import sairepa.model.ActField;
import sairepa.model.ActList;
import sairepa.model.ActListFactory;
import sairepa.model.FieldLayout;
import sairepa.model.FieldLayoutElement;
import sairepa.model.Util;

/**
 * is a JPanel displaying one act at a time.
 * Note: all the methods prefixed with "sub" won't save the act !
 */
public class ActViewer extends Viewer implements ActionListener
{
	public final static int MAX_LINE_LENGTH = 60;
	public final static int MAX_FIELD_LENGTH = 30;

	public final static long serialVersionUID = 1;
	private ActList actList;
	private ActList.ActListIterator actListIterator;
	private Act currentAct;
	private boolean newAct;
	private int newActRow = -1;

	private GlobalPanel globalPanel;

	private MainWindow mainWindow;

	public ActViewer(MainWindow mainWindow, ActList actList) {
		super(actList, ActViewerFactory.NAME, ActViewerFactory.ICON);
		this.actList = actList;
		this.mainWindow = mainWindow;
		prepareUI(actList);
		initActList(actList);
		refresh();
	}

	private class GlobalPanel {
		private JPanel gp;
		private List<VisualActField> visualActFieldsOrdered = new ArrayList<VisualActField>();
		private Map<ActField, VisualActField> visualActFields = new HashMap<ActField, VisualActField>();

		public GlobalPanel() {
			super();
		}

		public List<VisualActField> getVisualActFieldsOrdered() {
			return visualActFieldsOrdered;
		}

		public Map<ActField, VisualActField> getVisualActFields() {
			return visualActFields;
		}

		public JPanel getPanel() {
			return gp;
		}

		private class PanelCreationResult {
			public int nmbIdx = 0;
			public JPanel panel = null;

			public PanelCreationResult(int nmbIdx, JPanel panel) {
				Util.check(nmbIdx > 0);
				this.nmbIdx = nmbIdx;
				this.panel = panel;
			}
		}

		private JPanel createPanel(FieldLayout layout) {
			JPanel omegaPanel = new JPanel(new GridLayout(1, 1));
			JPanel panel = new JPanel(new BorderLayout(5, 5));
			JPanel subPanel = panel;

			for (int i = 0 ; i < layout.getElements().length ; ) {
				FieldLayoutElement el = layout.getElements()[i];
				PanelCreationResult r = createPanel(layout.getElements(), i);
				subPanel.add(r.panel, BorderLayout.NORTH);
				JPanel subsubPanel = new JPanel(new BorderLayout());
				subPanel.add(subsubPanel, BorderLayout.CENTER);
				subPanel = subsubPanel;
				i += r.nmbIdx;
			}

			if (layout.getTitle() != null) {
				panel.setBorder(BorderFactory.createTitledBorder(layout.getTitle()));
			}

			omegaPanel.add(panel);
			omegaPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

			gp = omegaPanel;
			return omegaPanel;
		}

		private PanelCreationResult createPanel(FieldLayoutElement[] els, int idx) {
			if (els[idx] instanceof FieldLayout) {
				return new PanelCreationResult(1, createPanel((FieldLayout)(els[idx])));
			} else if (els[idx] instanceof ActField) {
				if (((ActField)els[idx]).isMemo()) {
					return new PanelCreationResult(1, createPanel((ActField)els[idx], true));
				}

				JPanel bigMess = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));

				int i;
				int nmbChars = 0;

				for (i = idx;
						i < els.length && els[i] instanceof ActField && !((ActField)els[i]).isMemo();
						i++) {
					ActField field = (ActField)els[i];
					int lng =  10 + maximizeLength(field.getMaxLength());
					nmbChars += lng;
					if (nmbChars > MAX_LINE_LENGTH && lng < MAX_LINE_LENGTH) {
						break;
					}
					bigMess.add(createPanel(field, i == idx));
						}

				return new PanelCreationResult(i-idx, bigMess);
			} else {
				Util.check(false);
				return null;
			}
		}

		private JPanel createPanel(ActField field, boolean firstOnLine) {
			JLabel l = new JLabel(field.getName());
			l.setPreferredSize(new java.awt.Dimension(60, 20));
			l.setHorizontalAlignment(JLabel.RIGHT);

			if (field.isMemo()) {
				l.setVerticalAlignment(JLabel.TOP);
			}
			JPanel panel = new JPanel(new BorderLayout(5, 5));
			VisualActField f = VisualActField.createVisualActField(ActViewer.this, field, l, panel);
			visualActFields.put(field, f);
			visualActFieldsOrdered.add(f);

			panel.add(l, BorderLayout.WEST);
			panel.add(f.getParentComponent(), BorderLayout.CENTER);

			return panel;
		}

		public void refresh() {
			for (ActEntry e : currentAct.getEntries()) {
				VisualActField f = visualActFields.get(e.getField());
				f.setEntry(e);
			}
		}

		public void connectUIComponents(ActList actList) {
			for (int i = 0 ; i < visualActFieldsOrdered.size() ; i++) {
				VisualActField f = visualActFieldsOrdered.get(i);
				VisualActField next = (((i+1) < visualActFieldsOrdered.size()) ?
						visualActFieldsOrdered.get(i+1) : null);
				if (next != null)
					next.setPreviousField(f);
				// we don't connect the memo field
				if (next != null && next.getField().isMemo()) {
					next = null;
				}
				f.setNextField(next);
			}
		}
	}

	private void prepareUI(ActList actList) {
		this.setLayout(new BorderLayout(5, 5));
		globalPanel = new GlobalPanel();
		globalPanel.createPanel(actList.getFields());
		JScrollPane scrollPane =
			new JScrollPane(globalPanel.getPanel(),
					JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.getVerticalScrollBar().setUnitIncrement(15);
		this.add(scrollPane, BorderLayout.CENTER);
		this.add(createButtonPanel(), BorderLayout.SOUTH);

		globalPanel.connectUIComponents(actList);
	}

	protected static int maximizeLength(int lng) {
		if (lng > 10)
			lng = lng - (lng % 10);
		return (lng > MAX_FIELD_LENGTH ? MAX_FIELD_LENGTH : lng);
	}

	private JLabel positionLabel = new JLabel("x / y");
	private JButton applyButton = new JButton("Valider");
	private JButton deleteButton = new JButton("Effacer");
	private JButton newButton = new JButton("Nouveau");
	private JButton beginningButton = new JButton("<<");
	private JButton previousButton = new JButton("<");
	private JTextField currentActField = new JTextField();
	private JButton nextButton = new JButton(">");
	private JButton endButton = new JButton(">>");
	private List<JButton> buttons = new Vector<JButton>();

	private JPanel createButtonPanel() {
		applyButton.setMnemonic(java.awt.event.KeyEvent.VK_E);

		JPanel globalPanel = new JPanel(new BorderLayout());
		globalPanel.add(new JLabel(""), BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel(new BorderLayout(5, 5));
		JPanel bigButtonsPanel = new JPanel(new GridLayout(1, 2));
		bigButtonsPanel.add(applyButton);
		applyButton.addActionListener(this);
		bigButtonsPanel.add(deleteButton);
		deleteButton.addActionListener(this);
		bigButtonsPanel.add(newButton);
		newButton.addActionListener(this);

		JPanel smallButtonsPanel = new JPanel(new GridLayout(1, 4));
		smallButtonsPanel.add(beginningButton);
		beginningButton.addActionListener(this);
		smallButtonsPanel.add(previousButton);
		previousButton.addActionListener(this);
		smallButtonsPanel.add(currentActField);
		currentActField.addActionListener(this);
		RightClickMenu.addRightClickMenu(currentActField);
		smallButtonsPanel.add(nextButton);
		nextButton.addActionListener(this);
		smallButtonsPanel.add(endButton);
		endButton.addActionListener(this);

		buttonPanel.add(bigButtonsPanel, BorderLayout.NORTH);
		buttonPanel.add(smallButtonsPanel, BorderLayout.SOUTH);

		JPanel omegaPanel = new JPanel(new BorderLayout());
		omegaPanel.add(positionLabel, BorderLayout.CENTER);
		omegaPanel.add(buttonPanel, BorderLayout.EAST);

		return omegaPanel;
	}

	public void updateButtonStates() {
		boolean e = currentAct.validate();
		applyButton.setEnabled(e);
		newButton.setEnabled(e);
		beginningButton.setEnabled(e);
		previousButton.setEnabled(actListIterator.hasPrevious() ? e : false);
		currentActField.setEditable(e);
		nextButton.setEnabled(actListIterator.hasNext() ? e : false);
		endButton.setEnabled(e);

		deleteButton.setText(newAct ? "Annuler saisie" : "Effacer");
		newButton.setText((newAct || !actListIterator.hasNext()) ? "Nouvel acte" : "Inserer nouvel acte");
	}

	private boolean hasElements() {
		return (actList.getRowCount() > 0);
	}

	/**
	 * always save the current act before doing anything. Stops
	 * immediatly if can't save
	 */
	public void actionPerformed(ActionEvent e) {
		try
		{
			mainWindow.getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

			if (e.getSource() != deleteButton) {
				if (!saveAct()) {
					return;
				}
			}

			if (e.getSource() == beginningButton) {
				if (!hasElements()) {
					subStartNewAct();
				} else {
					currentAct = actListIterator.seek(0);
					newAct = false;
					refresh();
				}
			} else if (e.getSource() == previousButton) {
				if (actListIterator.hasPrevious()) {
					currentAct = actListIterator.previous();
					newAct = false;
					refresh();
				}
			} else if (e.getSource() == currentActField) {
				setSelectedAct(Integer.valueOf(currentActField.getText()));
			} else if (e.getSource() == nextButton) {
				if (actListIterator.hasNext()) {
					currentAct = actListIterator.next();
					newAct = false;
					refresh();
				}
			} else if (e.getSource() == endButton) {
				subGoToLastAct();
			} else if (e.getSource() == applyButton) {
				subContinueTyping();
			} else if (e.getSource() == deleteButton) {
				subDeleteAct();
			} else if (e.getSource() == newButton) {
				subStartNewAct();
			}
		} finally {
			mainWindow.getContentPane().setCursor(Cursor.getDefaultCursor());
		}
	}

	private boolean saveAct() {
		// just to make sure
		for (VisualActField vFields : globalPanel.getVisualActFieldsOrdered()) {
			vFields.updateEntry(true);
			vFields.refresh();
		}

		if (newAct) {
			for (ViewerObserver obs : getObservers()) {
				if (!obs.insertingAct(this, currentAct, newActRow))
					return false;
			}

			currentAct = actListIterator.seek(currentAct.getRow());
		} else {
			for (ViewerObserver obs : getObservers()) {
				if (!obs.changingAct(this, currentAct))
					return false;
			}
		}

		return true;
	}

	protected void continueTyping() {
		if (!currentAct.validate())
			return;
		if (!saveAct())
			return;
		subContinueTyping();
	}

	protected void gotoPreviousAct() {
		if (!saveAct())
			return;
		if (actListIterator.hasPrevious()) {
			currentAct = actListIterator.previous();
			newAct = false;
			refresh();
		}
	}

	protected void gotoFirstField() {
		int i = 0;
		while (i < globalPanel.getVisualActFieldsOrdered().size()
				&& !globalPanel.getVisualActFieldsOrdered().get(i).includeInFieldLoop())
			i++;
		if ( i < globalPanel.getVisualActFieldsOrdered().size() )
			globalPanel.getVisualActFieldsOrdered().get(i).focus();
	}

	protected void gotoLastField() {
		int i = globalPanel.getVisualActFieldsOrdered().size() - 1;
		while (i >= 0 && !globalPanel.getVisualActFieldsOrdered().get(i).includeInFieldLoop())
			i--;
		if ( i>= 0 )
			globalPanel.getVisualActFieldsOrdered().get(i).focus();
	}

	private void subContinueTyping() {
		if (!currentAct.validate()) {
			return;
		}

		// has already been saved
		if (newAct) {
			subStartNewAct();
		} else {
			if (actListIterator.hasNext()) {
				currentAct = actListIterator.next();
			} else {
				newActRow = currentAct.getRow() +1;
				currentAct = actList.createAct();
				newAct = true;
			}
			refresh();
		}
	}

	private void subStartNewAct() {
		if (currentAct != null) {
			newActRow = currentAct.getRow() + 1;
		} else {
			newActRow = 0;
		}
		currentAct = actList.createAct();
		newAct = true;
		refresh();
		globalPanel.getVisualActFieldsOrdered().get(0).focus();
	}

	private void subMoveBack() {
		// we are on the last element, so we need to go back
		if (actListIterator.hasPrevious()) {
			currentAct = actListIterator.previous();
			newAct = false;
			refresh();
		} else {
			// and if we can't ...
			subStartNewAct();
		}
	}

	private void subGoToLastAct() {
		if (!hasElements()) {
			subStartNewAct();
		} else {
			currentAct = actListIterator.seek(actList.getRowCount()-1);
			newAct = false;
			refresh();
		}
	}

	private void subDeleteAct() {
		if (!newAct) {
			int ret = JOptionPane.showConfirmDialog(mainWindow,
					"Attention, cet acte sera d\351finitivement supprim\351. " +
					"Etes-vous s\373r ?", "Effacer ?",
					JOptionPane.YES_NO_OPTION);
			if (ret != JOptionPane.YES_OPTION) {
				return;
			}

			Act actToDelete = currentAct;

			if (!actListIterator.hasNext()) {
				// we are on the last element, so we need to go back
				subMoveBack();
			}

			reloadAct();

			for (ViewerObserver obs : getObservers()) {
				obs.deletingAct(this, actToDelete);
			}

			refresh();
		} else {
			subGoToLastAct();
		}
	}

	private void updatePositionLabel() {
		if (!newAct) {
			positionLabel.setText(Integer.toString(actListIterator.currentIndex()+1)
					+ " / " + Integer.toString(actList.getRowCount()));
			currentActField.setText(Integer.toString(actListIterator.currentIndex()+1));
		} else {
			positionLabel.setText("" + Integer.toString(newActRow+1)
					+ " (nouveau) / " + Integer.toString(actList.getRowCount()+1));
			currentActField.setText(Integer.toString(newActRow+1));
		}
	}

	private void initActList(ActList actList) {
		actListIterator = actList.iterator();
		if (actListIterator.hasNext()) {
			currentAct = actListIterator.seek(actList.getRowCount()-1);
			newAct = false;
		} else {
			// nothing to save -> so we can call a sub[...]() directly
			subStartNewAct();
		}
	}

	private void reloadAct() {
		if (!newAct) {
			currentAct.reload();
		}
	}

	@Override
		public synchronized void refresh() {
			reloadAct();
			updatePositionLabel();
			globalPanel.refresh();
			updateButtonStates();
		}

	@Override
		public void refresh(Act a) {
			if ( a.getActList().getFactory() != currentAct.getActList().getFactory() )
				return;
			if ( a.getRow() == currentAct.getRow() )
				refresh();
		}

	@Override
		public void close() {
			if (currentAct.validate()) {
				saveAct();
			}
			super.close();
		}

	@Override
		public String canClose() {
			if (currentAct.validate()) {
				return null;
			} else {
				if (newAct && actList.getRowCount() <= 0) {
					// we are working on an empty list, so
					// when the user cancels, they get a new act
					// all the time, that are invalids by default
					return null;
				}

				return "L'acte actuellement en cours d'\351dition "+
					"est invalide et ne peut \352tre enregistr\351.";
			}
		}

	@Override
		public boolean canBePrinted() {
			return true;
		}

	@Override
		public JComponent getPrintableComponent() {
			GlobalPanel gp = new GlobalPanel();
			gp.createPanel(actList.getFields());
			gp.refresh();
			return gp.getPanel();
		}

	@Override
		public boolean printOnOnePage() {
			return true;
		}

	@Override
		public void printingDone() {

		}

	public int[] getSelectedActs() {
		return new int[] { currentAct.getRow() };
	}

	public void setSelectedAct(int actNmb) {
		int i = actNmb - 1;
		if (i >= 0 && i < actList.getRowCount()) {
			currentAct = actListIterator.seek(i);
			newAct = false;
		}
		refresh();
	}
}


