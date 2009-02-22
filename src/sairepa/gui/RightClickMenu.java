/*
** RightClickMenu.java
** Login : <jflesch@GrayBrick>
** Started on  Fri Feb 13 14:59:54 2009 Jerome Flesch
*/
package sairepa.gui;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.text.JTextComponent;

public class RightClickMenu extends JPopupMenu
  implements MouseListener
{
  public final static long serialVersionUID = 1;
  private Component parent;

  public RightClickMenu(Component c) {
    super();
    c.addMouseListener(this);
    this.parent = c;
  }

  public static RightClickMenu addRightClickMenu(JTextComponent textField) {
    RightClickMenu m = new RightClickMenu(textField);
    m.add(new CopyActionItem(new TextExtractor.FieldTextExtractor(textField)));
    m.add(new CutActionItem(new TextExtractor.FieldTextExtractor(textField),
			    new TextInjector.FieldTextInjector(textField)));
    m.add(new PasteActionItem(new TextInjector.FieldTextInjector(textField)));
    return m;
  }

  public static RightClickMenu addRightClickMenu(JTable table) {
    RightClickMenu m = new RightClickMenu(table);
    m.add(new CopyActionItem(new TextExtractor.TableTextExtractor(table)));
    m.add(new PasteActionItem(new TextInjector.TableTextInjector(table)));
    return m;
  }

  public static interface SelfUpdatingItem {
    public void update();
  }

  private void popup(Component pparent, int x, int y) {
    for (Component c : getComponents()) {
      if (c instanceof SelfUpdatingItem) {
	((SelfUpdatingItem)c).update();
      }
    }
    this.show(parent, x, y);
  }

  public void mouseClicked(MouseEvent e) {
    if (e.isPopupTrigger()) popup(e.getComponent(), e.getX(), e.getY());
  }

  public void mouseEntered(MouseEvent e) {
    if (e.isPopupTrigger()) popup(e.getComponent(), e.getX(), e.getY());
  }

  public void mouseExited(MouseEvent e) {
    if (e.isPopupTrigger()) popup(e.getComponent(), e.getX(), e.getY());
  }

  public void mousePressed(MouseEvent e) {
    if (e.isPopupTrigger()) popup(e.getComponent(), e.getX(), e.getY());
  }

  public void mouseReleased(MouseEvent e) {
    if (e.isPopupTrigger()) popup(e.getComponent(), e.getX(), e.getY());
  }
}
