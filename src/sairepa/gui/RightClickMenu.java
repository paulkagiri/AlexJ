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

public class RightClickMenu extends JPopupMenu implements MouseListener
{
  public final static long serialVersionUID = 1;

  public RightClickMenu(Component c) {
    super();
    c.addMouseListener(this);
  }

  private void popup(Component c, int x, int y) {
    this.show(c, x, y);
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
