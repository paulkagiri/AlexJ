package sairepa.view;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import javax.swing.text.JTextComponent;

import sairepa.gui.RightClickMenu;
import sairepa.model.Act;
import sairepa.model.ActEntry;
import sairepa.model.ActField;
import sairepa.model.AutoCompleter;
import sairepa.model.Util;

public abstract class VisualActField implements Observer, PopupMenuListener {

  private ActViewer parentViewer;
  private ActField field;
  private JLabel associatedLabel;
  private JPanel parentPanel;

  private ActEntry entry = null;

  private VisualActField nextField = null;
  private VisualActField previousField = null;

  private boolean focusManagement;

  public VisualActField(ActViewer parentViewer, ActField field,
			JLabel associatedLabel, JPanel parentPanel)
  {
    Util.check(parentViewer != null);
    Util.check(field != null);
    Util.check(associatedLabel != null);
    Util.check(parentPanel != null);

    this.parentViewer = parentViewer;
    this.field = field;
    this.associatedLabel = associatedLabel;
    this.parentPanel = parentPanel;
    setFocusManagementEnabled(true);
  }

  public ActField getField() {
    return field;
  }

  public void setNextField(VisualActField field) {
    this.nextField = field;
  }

  public void setPreviousField(VisualActField field) {
    this.previousField = field;
  }

  public void setEntry(ActEntry entry) {
    if (this.entry != null) {
      this.entry.deleteObserver(this);
    }

    this.entry = entry;
    entry.addObserver(this);
    refresh();
  }

  public ActEntry getEntry() {
    return entry;
  }

  /* crappy work around because of some issue with the focus and the right click menu */
  public void setFocusManagementEnabled(boolean b) {
    focusManagement = b;
  }

  public void popupMenuCanceled(PopupMenuEvent e) {
    setFocusManagementEnabled(true);
  }

  public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
    setFocusManagementEnabled(true);
  }

  public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
    setFocusManagementEnabled(false);
  }

  protected void focusGained() {
    if (focusManagement) {
      updateEntry(false);
      field.hasFocus(entry);
      refresh();
      selectWholeText();
      java.awt.Rectangle rect = getTextComponent().getBounds(null);
      parentPanel.scrollRectToVisible(rect);
    }
  }

  protected void focusLost() {
    if (focusManagement) {
      updateEntry(true);
      refresh();
    }
  }

  public void focus() {
    getTextComponent().requestFocus();
  }

  public void updateEntry(boolean notify) {
    entry.setValue(getText(), notify);
    updateColors();
    parentViewer.updateButtonStates();
  }

  public void goNextComponent() {
    if (nextField != null) {
      nextField.focus();
    } else {
      /* ie next act */
      parentViewer.continueTyping();
    }
  }

  public void goPreviousComponent() {
    if (previousField != null) {
      previousField.focus();
    }
  }

  /* ie enter pressed */
  public void inputValidated() {
    updateEntry(true);
    refresh();
    goNextComponent();
  }

  private Color initialTxtCompColor = null;
  private Color initialLabelColor = null;

  public void updateColors() {
    if (initialTxtCompColor == null || initialLabelColor == null) {
      initialTxtCompColor = getTextComponent().getForeground();
      initialLabelColor = associatedLabel.getForeground();
    }

    if (!entry.validate()) {
      // RED
      getTextComponent().setForeground(new Color(255, 0, 0));
      associatedLabel.setForeground(new Color(255, 0, 0));
    } else if (entry.warning()) {
      // ORANGE
      getTextComponent().setForeground(new Color(255, 165, 0));
      associatedLabel.setForeground(new Color(255, 165, 0));
    } else {
      getTextComponent().setForeground(initialTxtCompColor);
      associatedLabel.setForeground(initialLabelColor);
    }
  }

  public void refresh() {
    setText(entry.getValue());
    updateColors();
    getTextComponent().repaint();
  }

  public void update(Observable o, Object param) {
    refresh();
  }

  public abstract JComponent getParentComponent();
  public abstract JComponent getTextComponent();
  public abstract String getText();
  public abstract void setText(String text);
  public abstract void selectWholeText();

  private static class VisualActTextFieldAutoCompletable extends VisualActField
    implements ActionListener, FocusListener, CaretListener {

    private ActField actField;
    private JComboBox comboBox;
    private JTextComponent txtComp;
    private boolean focus = false;

    public VisualActTextFieldAutoCompletable(ActViewer parentViewer, ActField actField,
					     JLabel associatedLabel, JPanel parentPanel) {
      super(parentViewer, actField, associatedLabel, parentPanel);
      this.actField = actField;
      comboBox = new JComboBox(new Object[] { "testA", "testB" } );
      comboBox.setEditable(true);
      txtComp = ((JTextComponent)comboBox.getEditor().getEditorComponent());
      comboBox.addActionListener(this);
      txtComp.addFocusListener(this);
      txtComp.addCaretListener(this);
    }

    public JComponent getParentComponent() {
      return comboBox;
    }

    public JComponent getTextComponent() {
      return txtComp;
    }

    public String getText() {
      return txtComp.getText();
    }

    public void setText(String str) {
      txtComp.setText(str);
    }

    public void selectWholeText() {
      txtComp.selectAll();
    }

    public void focusGained(FocusEvent e) {
      focus = true;
      comboBox.setPopupVisible(true);
    }

    public void focusLost(FocusEvent e) {
      focus = false;
      comboBox.setPopupVisible(false);
    }

    // Dirty hack
    private boolean stopListening = false;

    public void actionPerformed(ActionEvent e) {
      if (stopListening)
	return;
      inputValidated();
    }

    private class UpdateListView implements Runnable {
      private boolean stop = false;
      private final String txt;
      private final List<String> suggestions;
      private final int dot;
      private final int mark;

      public UpdateListView(String txt, List<String> suggestions,
			    int dot, int mark) {
	this.txt = txt;
	this.suggestions = suggestions;
	this.dot = dot;
	this.mark = mark;
      }

      public void stop() {
	stop = true;
      }

      public void run() {
	if (stop)
	  return;

	synchronized(VisualActTextFieldAutoCompletable.this) {
	  stopListening = true;

	  if (focus)
	    comboBox.setPopupVisible(false);

	  comboBox.removeAllItems();
	  for (String s : suggestions)
	    comboBox.addItem(s);

	  txtComp.setText(txt);
	  txtComp.getCaret().setDot(dot);

	  if (focus && suggestions.size() > 0)
	      comboBox.setPopupVisible(true);

	  stopListening = false;
	}
      }
    }

    private class ListUpdater implements Runnable {
      private final String txt;
      private final int dot;
      private final int mark;
      private final Act act;
      private boolean stop = false;
      private UpdateListView upView = null;
      public ListUpdater(Act act, String txt, int dot, int mark) {
	this.txt = txt;
	this.dot = dot;
	this.mark = mark;
	this.act = act;
      }
      public void run() {
	AutoCompleter ac = actField.getAutoCompleter(act);
	List<String> rs = ac.getSuggestions(getEntry(), txt);
	if (stop)
	  return;
	try {
	  SwingUtilities.invokeAndWait(upView = new UpdateListView(txt, rs, dot, mark));
	} catch (InterruptedException e) {
	  throw new RuntimeException(e);
	} catch (java.lang.reflect.InvocationTargetException e) {
	  throw new RuntimeException(e);
	}
      }
      public void stop() {
	stop = true;
	if (upView != null)
	  upView.stop();
      }
    }

    private ListUpdater updater = null;
    private String oldTxt = null;

    public void caretUpdate(CaretEvent e) {
      if (stopListening)
	return;
      String txt = txtComp.getText();
      if (oldTxt != null && oldTxt.equals(txt))
	return;
      oldTxt = txt;
      if (updater != null)
	updater.stop();
      updater = new ListUpdater(getEntry().getAct(), txt, e.getDot(), e.getMark());
      new Thread(updater).start();
    }
  }

  private static class VisualActTextField extends VisualActField
    implements ActionListener, FocusListener {

    private JTextField textField;

    public VisualActTextField(ActViewer parentViewer, ActField field,
			     JLabel associatedLabel, JPanel parentPanel) {
      super(parentViewer, field, associatedLabel, parentPanel);
      textField = new JTextField(ActViewer.maximizeLength(field.getMaxLength()));
      textField.addActionListener(this);
      textField.addFocusListener(this);
      RightClickMenu.addRightClickMenu(textField).addPopupMenuListener(this);
    }

    public JComponent getParentComponent() {
      return textField;
    }

    public JComponent getTextComponent() {
      return textField;
    }

    public String getText() {
      return textField.getText();
    }

    public void setText(String text) {
      textField.setText(text);
    }

    public void actionPerformed(ActionEvent e) {
      this.inputValidated();
    }

    public void focusGained(FocusEvent e) {
      super.focusGained();
    }

    public void focusLost(FocusEvent e) {
      super.focusLost();
    }

    public void selectWholeText() {
      textField.selectAll();
    }
  }

  private static class VisualActTextArea extends VisualActField implements FocusListener {
    private JTextArea textArea;
    private JScrollPane scrollPane;

    public VisualActTextArea(ActViewer parentViewer, ActField field,
			     JLabel associatedLabel, JPanel parentPanel) {
      super(parentViewer, field, associatedLabel, parentPanel);
      textArea = new JTextArea(5, ActViewer.MAX_LINE_LENGTH/2);
      textArea.setLineWrap(true);
      textArea.setWrapStyleWord(true);

      textArea.setLineWrap(true);
      textArea.setWrapStyleWord(true);

      AbstractAction tabAction = new AbstractAction() {
	  public final static long serialVersionUID = 1;
	  public void actionPerformed(ActionEvent e) {
	    inputValidated();
	  }
	};
      KeyStroke tabKey = KeyStroke.getKeyStroke("TAB");
      textArea.getInputMap().put(tabKey, tabAction);

      tabAction = new AbstractAction() {
	  public final static long serialVersionUID = 1;
	  public void actionPerformed(ActionEvent e) {
	    goPreviousComponent();
	  }
	};
      tabKey = KeyStroke.getKeyStroke("shift TAB");
      textArea.getInputMap().put(tabKey, tabAction);

      textArea.addFocusListener(this);
      RightClickMenu.addRightClickMenu(textArea).addPopupMenuListener(this);

      scrollPane = new JScrollPane(textArea);
      scrollPane.getVerticalScrollBar().setUnitIncrement(10);
    }

    public JComponent getParentComponent() {
      return scrollPane;
    }

    public JComponent getTextComponent() {
      return textArea;
    }

    public String getText() {
      return textArea.getText();
    }

    public void setText(String text) {
      textArea.setText(text);
    }

    public void focusGained(FocusEvent e) {
      super.focusGained();
    }

    public void focusLost(FocusEvent e) {
      super.focusLost();
    }

    public void selectWholeText() {
      textArea.selectAll();
    }
  }


  /**
   * @param parentPanel provided for automatic scrolling stuff
   * @param associatedLabel provided for color changes stuff
   */
  public static VisualActField createVisualActField(ActViewer parentViewer, ActField field,
						    JLabel associatedLabel, JPanel parentPanel) {
    if (field.isMemo()) {
      return new VisualActTextArea(parentViewer, field, associatedLabel, parentPanel);
    } else if (field.hasAutoCompleter()) {
      return new VisualActTextFieldAutoCompletable(parentViewer, field, associatedLabel, parentPanel);
    } else {
      return new VisualActTextField(parentViewer, field, associatedLabel, parentPanel);
    }
  }
}
