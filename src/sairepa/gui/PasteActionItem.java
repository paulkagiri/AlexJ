/*
** PasteActionItem.java
** Login : <jflesch@GrayBrick>
** Started on  Fri Feb 13 15:20:15 2009 Jerome Flesch
*/
package sairepa.gui;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Toolkit;

import javax.swing.JMenuItem;
import javax.swing.text.DefaultEditorKit;

public class PasteActionItem extends JMenuItem
  implements ActionListener, RightClickMenu.SelfUpdatingItem
{
  public static final long serialVersionUID = 1;
  private final TextInjector ti;

  public PasteActionItem(TextInjector ti) {
    super("Coller", IconBox.paste);
    addActionListener(this);
    this.ti = ti;
  }

  private String getTextToPaste() {
    final Toolkit tk = Toolkit.getDefaultToolkit();
    final Clipboard cp = tk.getSystemClipboard();

    final Transferable contents = cp.getContents(null);

    final boolean hasTransferableText = ((contents != null) &&
        contents.isDataFlavorSupported(DataFlavor.stringFlavor));

    try {
      if (hasTransferableText) {
	return (String)contents.getTransferData(DataFlavor.stringFlavor);
      } else {
	System.err.println("PasteActionItem.getTextToPaste(): hasTransferableText == false");
	return null;
      }
    } catch(final java.awt.datatransfer.UnsupportedFlavorException e) {
      System.err.println("PasteActionItem.getTextToPaste(): UnsupportedFlavorException");
      return null;
    } catch(final java.io.IOException e) {
      System.err.println("PasteActionItem.getTextToPaste(): IOException: " + e.toString());
      e.printStackTrace();
      return null;
    }
  }

  String txt;

  public void update() {
    //txt = getTextToPaste();
    //setEnabled(txt != null && !"".equals(txt.trim()));
  }

  public void actionPerformed(ActionEvent e) {
    ti.inject(getTextToPaste());
  }
}
