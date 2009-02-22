/*
** CopyActionItem.java
** Login : <jflesch@GrayBrick>
** Started on  Fri Feb 13 15:16:15 2009 Jerome Flesch
*/
package sairepa.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

public class CopyActionItem extends JMenuItem implements ActionListener
{
  public static final long serialVersionUID = 1;

  public CopyActionItem() {
    super("Copier", IconBox.copy);
    addActionListener(this);
  }

  public void actionPerformed(ActionEvent e) {

  }
}
