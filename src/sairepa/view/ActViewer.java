package sairepa.view;

import java.awt.BorderLayout;
import java.awt.Color;
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
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.JTextComponent;

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
 */
public class ActViewer extends Viewer implements ActionListener
{
  public final static long serialVersionUID = 1;
  private ActList actList;
  private ActList.ActListIterator actListIterator;
  private Act currentAct;
  private boolean newAct;
  private int newActRow = -1;

  private List<VisualActField> visualActFieldsOrdered = new ArrayList<VisualActField>();
  private Map<ActField, VisualActField> visualActFields = new HashMap<ActField, VisualActField>();

  public ActViewer(ActList actList) {
    super(actList, ActViewerFactory.NAME, ActViewerFactory.ICON);
    this.actList = actList;
    prepareUI(actList);
    connectUIComponents(actList);
    initActList(actList);
    refresh();
  }

  protected class VisualActField implements ActionListener, InputMethodListener, CaretListener, FocusListener {
    private final long serialVersionUID = 1;

    private VisualActField nextField = null;
    private JTextComponent textComponent;
    private JComponent component;

    private JLabel associatedLabel;

    private ActField field = null;
    private ActEntry entry = null;

    private Color initialColor;
    private Color initialLabelColor;

    private JPanel parentPanel;

    public VisualActField(ActField field, JLabel associatedLabel, JPanel parent) {
      this.parentPanel = parent;

      Util.check(field != null);

      this.field = field;
      this.associatedLabel = associatedLabel;

      if (!field.isMemo()) {
	JTextField f = new JTextField(maximizeLength(field.getLength()));
	f.addActionListener(this);
	textComponent = f;
	component = textComponent;
      } else {
	JTextArea area = new JTextArea(5, MAX_LINE_LENGTH/2);
	area.setLineWrap(true);
	area.setWrapStyleWord(true);
	textComponent = area;
	component = new JScrollPane(textComponent);
	((JScrollPane)component).getVerticalScrollBar().setUnitIncrement(10);
      }

      textComponent.addInputMethodListener(this);
      textComponent.addCaretListener(this);
      textComponent.addFocusListener(this);

      initialColor = textComponent.getForeground();
      initialLabelColor = associatedLabel.getForeground();
    }

    public ActField getField() {
      return field;
    }

    public void setNextField(VisualActField field) {
      this.nextField = field;
    }

    public void setEntry(ActEntry entry) {
      this.entry = entry;
      refresh();
    }

    public JComponent getComponent() {
      return component;
    }

    public void focus() {
      textComponent.requestFocus();
    }

    public void updateEntry() {
      entry.setValue(textComponent.getText());

      if (entry.validate()) {
	textComponent.setForeground(initialColor);
	associatedLabel.setForeground(initialLabelColor);
      } else {
	textComponent.setForeground(new Color(255, 0, 0));
	associatedLabel.setForeground(new Color(255, 0, 0));
      }
      updateButtonStates();
    }

    public void actionPerformed(ActionEvent e) {
      updateEntry();
      refresh();

      if (nextField != null) {
	nextField.focus();
      } else {
	continueTyping();
      }
    }

    public void refresh() {
      textComponent.setText(entry.getValue());
    }

    public void caretPositionChanged(InputMethodEvent event) { }

    public void inputMethodTextChanged(InputMethodEvent event) {
      updateEntry();
    }

    public void	caretUpdate(CaretEvent e) {
      updateEntry();
    }

    public void	focusGained(FocusEvent e) {
      java.awt.Rectangle rect = textComponent.getBounds(null);
      parentPanel.scrollRectToVisible(rect);
    }

    public void focusLost(FocusEvent e) { }
  }


  private void prepareUI(ActList actList) {
    this.setLayout(new BorderLayout(5, 5));
    JScrollPane scrollPane =
      new JScrollPane(createPanel(actList.getFields()),
		      JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
		      JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    scrollPane.getVerticalScrollBar().setUnitIncrement(15);
    this.add(scrollPane, BorderLayout.CENTER);
    this.add(createButtonPanel(), BorderLayout.SOUTH);
  }

  public final static int MAX_LINE_LENGTH = 60;
  public final static int MAX_FIELD_LENGTH = 20;

  private int maximizeLength(int lng) {
    return (lng > MAX_FIELD_LENGTH ? MAX_FIELD_LENGTH : lng);
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

    return panel;
  }

  private PanelCreationResult createPanel(FieldLayoutElement[] els, int idx) {
    if (els[idx] instanceof FieldLayout) {
      return new PanelCreationResult(1, createPanel((FieldLayout)(els[idx])));
    } else if (els[idx] instanceof ActField) {
      if (((ActField)els[idx]).isMemo()) {
	return new PanelCreationResult(1, createPanel((ActField)els[idx]));
      }

      JPanel bigMess = new JPanel(new FlowLayout(FlowLayout.LEADING, 10, 10));

      int i;
      int nmbChars = 0;

      for (i = idx;
	   i < els.length && els[i] instanceof ActField && !((ActField)els[i]).isMemo();
	   i++) {
	ActField field = (ActField)els[i];
	int lng = field.getName().length() + 5 + maximizeLength(field.getLength());
	nmbChars += lng;
	if (nmbChars > MAX_LINE_LENGTH && lng < MAX_LINE_LENGTH) {
	  break;
	}
	bigMess.add(createPanel(field));
      }

      return new PanelCreationResult(i-idx, bigMess);
    } else {
      Util.check(false);
      return null;
    }
  }

  private JPanel createPanel(ActField field) {
    JLabel l = new JLabel(field.getName());
    if (field.isMemo()) {
      l.setVerticalAlignment(JLabel.TOP);
    }
    JPanel panel = new JPanel(new BorderLayout(5, 5));
    VisualActField f = new VisualActField(field, l, panel);
    visualActFields.put(field, f);
    visualActFieldsOrdered.add(f);

    panel.add(l, BorderLayout.WEST);
    panel.add(f.getComponent(), BorderLayout.CENTER);

    return panel;
  }

  private JLabel positionLabel = new JLabel("x / y");
  private JButton applyButton = new JButton("Appliquer");
  private JButton deleteButton = new JButton("Effacer");
  private JButton newButton = new JButton("Nouveau");
  private JButton beginningButton = new JButton("<<");
  private JButton previousButton = new JButton("<");
  private JTextField currentActField = new JTextField();
  private JButton nextButton = new JButton(">");
  private JButton endButton = new JButton(">>");
  private List<JButton> buttons = new Vector<JButton>();

  private JPanel createButtonPanel() {
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
  }

  private boolean hasElements() {
    return (actList.getRowCount() > 0);
  }

  /**
   * always save the current act before doing anything. Stops
   * immediatly if can't save
   */
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() != deleteButton) {
      if (!saveAct()) {
	return;
      }
    }

    if (e.getSource() == beginningButton) {
      if (!hasElements()) {
	startNewAct();
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
	int i = Integer.valueOf(currentActField.getText()) - 1;
	if (i >= 0 && i < actList.getRowCount()) {
	    currentAct = actListIterator.seek(i);
	    newAct = false;
	}
	refresh();
    } else if (e.getSource() == nextButton) {
      if (actListIterator.hasNext()) {
	currentAct = actListIterator.next();
	newAct = false;
	refresh();
      }
    } else if (e.getSource() == endButton) {
      goToLastAct();
    } else if (e.getSource() == applyButton) {
      continueTyping();
    } else if (e.getSource() == deleteButton) {
      deleteAct();
    } else if (e.getSource() == newButton) {
      startNewAct();
    }
  }

  private boolean saveAct() {
    // just to make sure
    for (VisualActField vFields : visualActFieldsOrdered) {
      vFields.updateEntry();
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

  private void continueTyping() {
    if (!currentAct.validate()) {
      return;
    }

    // has already been saved
    if (newAct) {
      startNewAct();
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

  private void startNewAct() {
      if (currentAct != null) {
	  newActRow = currentAct.getRow() + 1;
      } else {
	  newActRow = 0;
      }
      currentAct = actList.createAct();
      newAct = true;
      refresh();
  }

  private void moveBack() {
    // we are on the last element, so we need to go back
    if (actListIterator.hasPrevious()) {
      currentAct = actListIterator.previous();
      newAct = false;
      refresh();
    } else {
      // and if we can't ...
      startNewAct();
    }
  }

  private void goToLastAct() {
    if (!hasElements()) {
      startNewAct();
    } else {
      currentAct = actListIterator.seek(actList.getRowCount()-1);
      newAct = false;
      refresh();
    }
  }

  private void deleteAct() {
    if (!newAct) {
      Act actToDelete = currentAct;

      if (!actListIterator.hasNext()) {
	// we are on the last element, so we need to go back
	moveBack();
      }

      reloadAct();

      for (ViewerObserver obs : getObservers()) {
	obs.deletingAct(this, actToDelete);
      }
    } else {
      goToLastAct();
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

  private void connectUIComponents(ActList actList) {
    for (int i = 0 ; i < visualActFieldsOrdered.size() ; i++) {
      VisualActField f = visualActFieldsOrdered.get(i);
      VisualActField next = (((i+1) < visualActFieldsOrdered.size()) ?
			     visualActFieldsOrdered.get(i+1) : null);
      // we don't connect the memo field
      if (next != null && next.getField().isMemo()) {
	next = null;
      }
      f.setNextField(next);
    }
  }

  private void initActList(ActList actList) {
    actListIterator = actList.iterator();
    if (actListIterator.hasNext()) {
      currentAct = actListIterator.seek(actList.getRowCount()-1);
      newAct = false;
    } else {
      startNewAct();
    }
  }

  private void reloadAct() {
    if (!newAct) {
      currentAct.reload();
    }
  }

  @Override
  public void refresh() {
    reloadAct();
    updatePositionLabel();

    for (ActEntry e : currentAct.getEntries()) {
      VisualActField f = visualActFields.get(e.getField());
      f.setEntry(e);
    }

    updateButtonStates();
  }

  public boolean canClose() {
    return currentAct.validate();
  }
}
