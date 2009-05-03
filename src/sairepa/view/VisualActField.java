package sairepa.view;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.util.Observable;
import java.util.Observer;
import javax.swing.AbstractAction;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.text.JTextComponent;

import java.util.Observable;
import java.util.Observer;

import sairepa.gui.RightClickMenu;
import sairepa.model.ActEntry;
import sairepa.model.ActField;
import sairepa.model.Util;

public class VisualActField implements ActionListener, InputMethodListener,
			     CaretListener, FocusListener, Observer,
			     PopupMenuListener {
  private final long serialVersionUID = 1;

  private ActViewer parentViewer;

  private VisualActField nextField = null;
  private VisualActField previousField = null;
  private JTextComponent textComponent;
  private JComponent component;

  private JLabel associatedLabel;

  private ActField field = null;
  private ActEntry entry = null;

  private Color initialColor;
  private Color initialLabelColor;

  private JPanel parentPanel;

  public VisualActField(ActViewer parentViewer, ActField field, JLabel associatedLabel, JPanel parent) {
    Util.check(parentViewer != null);
    Util.check(field != null);
    Util.check(parent != null);

    this.parentViewer = parentViewer;
    this.field = field;
    this.parentPanel = parent;
    this.associatedLabel = associatedLabel;

    if (!field.isMemo()) {
      JTextField f = new JTextField(parentViewer.maximizeLength(field.getMaxLength()));
      f.addActionListener(this);
      textComponent = f;
      component = textComponent;
    } else {
      JTextArea area = new JTextArea(5, ActViewer.MAX_LINE_LENGTH/2);
      area.setLineWrap(true);
      area.setWrapStyleWord(true);

      AbstractAction tabAction = new AbstractAction() {
	  public final static long serialVersionUID = 1;
	  public void actionPerformed(ActionEvent e) {
	    VisualActField.this.actionPerformed(null);
	  }
	};
      KeyStroke tabKey = KeyStroke.getKeyStroke("TAB");
      area.getInputMap().put(tabKey, tabAction);

      tabAction = new AbstractAction() {
	  public final static long serialVersionUID = 1;
	  public void actionPerformed(ActionEvent e) {
	    goPreviousComponent();
	  }
	};
      tabKey = KeyStroke.getKeyStroke("shift TAB");
      area.getInputMap().put(tabKey, tabAction);

      textComponent = area;
      component = new JScrollPane(textComponent);
      ((JScrollPane)component).getVerticalScrollBar().setUnitIncrement(10);
    }

    textComponent.addInputMethodListener(this);
    textComponent.addCaretListener(this);
    textComponent.addFocusListener(this);
    RightClickMenu.addRightClickMenu(textComponent).addPopupMenuListener(this);

    initialColor = textComponent.getForeground();
    initialLabelColor = associatedLabel.getForeground();
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

  public JComponent getComponent() {
    return component;
  }

  /* crappy work around because of some issue with the focus and the right click menu */
  private boolean focusManagement = true;
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

  public void focus() {
    textComponent.requestFocus();
  }

  public void updateEntry(boolean notify) {
    entry.setValue(textComponent.getText(), notify);
    updateColor();
    parentViewer.updateButtonStates();
  }

  /**
   * @param e can be null (see the content of the constructor of VisualActField)
   */
  public void actionPerformed(ActionEvent e) {
    updateEntry(true);
    refresh();

    if (nextField != null) {
      nextField.focus();
    } else {
      parentViewer.continueTyping();
    }
  }

  public void goPreviousComponent() {
    if (previousField != null) {
      previousField.focus();
    }
  }

  public void updateColor() {
    if (!entry.validate()) {
      // RED
      textComponent.setForeground(new Color(255, 0, 0));
      associatedLabel.setForeground(new Color(255, 0, 0));
    } else if (entry.warning()) {
      // ORANGE
      textComponent.setForeground(new Color(255, 165, 0));
      associatedLabel.setForeground(new Color(255, 165, 0));
    } else {
      textComponent.setForeground(initialColor);
      associatedLabel.setForeground(initialColor);
    }
  }

  public void refresh() {
    textComponent.setText(entry.getValue());
    updateColor();
    textComponent.repaint();
  }

  public void caretPositionChanged(InputMethodEvent event) {
    //updateEntry(false);
  }

  public void inputMethodTextChanged(InputMethodEvent event) {
    //updateEntry(false);
  }

  public void	caretUpdate(CaretEvent e) {
    //updateEntry(false);
  }

  public void	focusGained(FocusEvent e) {
    if (focusManagement) {
      updateEntry(false);
      field.hasFocus(entry);
      refresh();
      textComponent.selectAll();
      java.awt.Rectangle rect = textComponent.getBounds(null);
      parentPanel.scrollRectToVisible(rect);
    }
  }

  public void focusLost(FocusEvent e) {
    if (focusManagement) {
      updateEntry(true);
      refresh();
    }
  }

  public void update(Observable o, Object param) {
    refresh();
  }
}
