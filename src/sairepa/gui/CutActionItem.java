/*
** CutActionItem.java
** Login : <jflesch@GrayBrick>
** Started on  Fri Feb 13 15:17:20 2009 Jerome Flesch
*/
package sairepa.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

public class CutActionItem extends JMenuItem implements ActionListener
{
  public static final long serialVersionUID = 1;

  public CutActionItem() {
    super("Couper", IconBox.cut);
    addActionListener(this);
  }

  public void actionPerformed(ActionEvent e) {

  }
}
