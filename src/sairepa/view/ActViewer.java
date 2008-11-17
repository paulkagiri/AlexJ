package sairepa.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import sairepa.model.Act;
import sairepa.model.ActEntry;
import sairepa.model.ActField;
import sairepa.model.ActList;
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

  private List<VisualActField> visualActFieldsOrdered = new ArrayList<VisualActField>();
  private Map<ActField, VisualActField> visualActFields = new HashMap<ActField, VisualActField>();

  public ActViewer(ActList actList, String name, ImageIcon icon) {
    super(actList.getName(), name, icon, actList);
    this.actList = actList;
    prepareUI(actList);
    connectUIComponents(actList);
    initActList(actList);
    refresh();
  }

  protected class VisualActField implements ActionListener {
    private final long serialVersionUID = 1;

    private VisualActField nextField = null;
    private JTextComponent textComponent;
    private JComponent component;

    private ActEntry entry = null;

    public VisualActField(ActField field) {
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
      }
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

    public void actionPerformed(ActionEvent e) {
      entry.setValue(textComponent.getText());

      if (nextField != null) {
	nextField.focus();
      } else {
	validateAct();
      }
    }

    public void refresh() {
      textComponent.setText(entry.getValue());
    }
  }


  private void prepareUI(ActList actList) {
    this.setLayout(new BorderLayout(5, 5));
    this.add(new JScrollPane(createPanel(actList.getFields()),
			     JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			     JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED),
	     BorderLayout.CENTER);
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
    VisualActField f = new VisualActField(field);
    visualActFields.put(field, f);
    visualActFieldsOrdered.add(f);
    JPanel panel = new JPanel(new BorderLayout(5, 5));

    panel.add(l, BorderLayout.WEST);
    panel.add(f.getComponent(), BorderLayout.CENTER);

    return panel;
  }

  private JLabel positionLabel = new JLabel("x / y");
  private JButton applyButton = new JButton("Appliquer");
  private JButton deleteButton = new JButton("Effacer");
  private JButton beginningButton = new JButton("<<");
  private JButton previousButton = new JButton("<");
  private JButton nextButton = new JButton(">");
  private JButton endButton = new JButton(">>");

  private JPanel createButtonPanel() {
    JPanel globalPanel = new JPanel(new BorderLayout());
    globalPanel.add(new JLabel(""), BorderLayout.CENTER);

    JPanel buttonPanel = new JPanel(new BorderLayout(5, 5));
    JPanel bigButtonsPanel = new JPanel(new GridLayout(1, 2));
    bigButtonsPanel.add(applyButton);
    applyButton.addActionListener(this);
    bigButtonsPanel.add(deleteButton);
    deleteButton.addActionListener(this);

    JPanel smallButtonsPanel = new JPanel(new GridLayout(1, 4));
    smallButtonsPanel.add(beginningButton);
    beginningButton.addActionListener(this);
    smallButtonsPanel.add(previousButton);
    previousButton.addActionListener(this);
    smallButtonsPanel.add(nextButton);
    nextButton.addActionListener(this);
    smallButtonsPanel.add(endButton);
    endButton.addActionListener(this);

    buttonPanel.add(bigButtonsPanel, BorderLayout.WEST);
    buttonPanel.add(smallButtonsPanel, BorderLayout.CENTER);

    globalPanel.add(buttonPanel, BorderLayout.EAST);
    globalPanel.add(positionLabel, BorderLayout.WEST);

    return globalPanel;
  }

  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == beginningButton) {
      currentAct = actListIterator.seek(0);
      newAct = false;
      refresh();
    } else if (e.getSource() == previousButton) {
      if (actListIterator.hasPrevious()) {
	currentAct = actListIterator.previous();
	newAct = false;
	refresh();
      }
    } else if (e.getSource() == nextButton) {
      if (actListIterator.hasNext()) {
	currentAct = actListIterator.next();
	newAct = false;
	refresh();
      }
    } else if (e.getSource() == endButton) {
      currentAct = actListIterator.seek(actList.getRowCount()-1);
      newAct = false;
      refresh();
    } else {
      System.err.println("Button non supporte => TODO");
      Util.check(false);
    }
  }

  private void updatePositionLabel() {
    if (!newAct) {
      positionLabel.setText(Integer.toString(actListIterator.currentIndex()+1)
			    + " / " + Integer.toString(actList.getRowCount()));
    } else {
      positionLabel.setText("[nouveau] / " + Integer.toString(actList.getRowCount()));
    }
  }

  private void connectUIComponents(ActList actList) {
    for (int i = 0 ; i < visualActFieldsOrdered.size() ; i++) {
      VisualActField f = visualActFieldsOrdered.get(i);
      VisualActField next = (((i+1) < visualActFieldsOrdered.size()) ?
			     visualActFieldsOrdered.get(i+1) : null);
      f.setNextField(next);
    }
  }

  private void initActList(ActList actList) {
    actListIterator = actList.iterator();
    if (actListIterator.hasNext()) {
      currentAct = actListIterator.seek(actList.getRowCount()-1);
      newAct = false;
    } else {
      currentAct = actList.createAct();
      newAct = true;
    }
  }

  public void validateAct() {
    System.err.println("ActViewer.validateAct(): TODO");
  }

  public void refresh() {
    updatePositionLabel();

    for (ActEntry e : currentAct.getEntries()) {
      VisualActField f = visualActFields.get(e.getField());
      f.setEntry(e);
    }
  }
}