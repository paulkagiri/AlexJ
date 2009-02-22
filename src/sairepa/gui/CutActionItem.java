/*
** CutActionItem.java
** Login : <jflesch@GrayBrick>
** Started on  Fri Feb 13 15:17:20 2009 Jerome Flesch
*/
package sairepa.gui;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Toolkit;

import javax.swing.JMenuItem;

public class CutActionItem extends JMenuItem
  implements ActionListener, RightClickMenu.SelfUpdatingItem
{
  public static final long serialVersionUID = 1;
  private final TextExtractor te;
  private final TextInjector ti;

  public CutActionItem(TextExtractor te, TextInjector ti) {
    super("Couper", IconBox.cut);
    addActionListener(this);
    this.te = te;
    this.ti = ti;
  }

  /* Dirty workaround for java bug #4096971 */
  /* see http://bugs.sun.com/bugdatabase/view_bug.do;jsessionid=9eb99863d85d8464e70ff98cdff4?bug_id=4096971 */
  private String txt;

  public void update() {
    txt = te.getText();
    setEnabled(txt != null && !("".equals(txt.trim())));
  }

  public void actionPerformed(ActionEvent e) {
    final Toolkit tk = Toolkit.getDefaultToolkit();
    final StringSelection st = new StringSelection(txt);
    final Clipboard cp = tk.getSystemClipboard();
    cp.setContents(st, null);

    ti.inject("");
  }
}
