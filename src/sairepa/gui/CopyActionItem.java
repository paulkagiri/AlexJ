/*
** CopyActionItem.java
** Login : <jflesch@GrayBrick>
** Started on  Fri Feb 13 15:16:15 2009 Jerome Flesch
*/
package sairepa.gui;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Toolkit;

import javax.swing.JMenuItem;

public class CopyActionItem extends JMenuItem
  implements ActionListener, RightClickMenu.SelfUpdatingItem
{
  public static final long serialVersionUID = 1;
  private final TextExtractor te;

  public CopyActionItem(TextExtractor te) {
    super("Copier", IconBox.copy);
    addActionListener(this);
    this.te = te;
  }

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
  }
}
