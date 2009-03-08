/*
** OpenActActionItem.java
** Login : <jflesch@GrayBrick>
** Started on  Sun Mar  8 20:12:24 2009 Jerome Flesch
*/
package sairepa.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JTable;

import java.util.List;

import sairepa.gui.IconBox;
import sairepa.gui.RightClickMenu;

import sairepa.model.Model;
import sairepa.view.Viewer;

public class OpenActActionItem extends JMenuItem
  implements ActionListener, RightClickMenu.SelfUpdatingItem
{
  public static final long serialVersionUID = 1;

  private final Viewer viewer;

  public OpenActActionItem(Viewer v)
  {
    super("Ouvrir", IconBox.fileOpen);
    addActionListener(this);
    this.viewer = v;
  }

  public void update() {
    setEnabled(viewer.getSelectedActs().length > 0);
  }

  public void actionPerformed(ActionEvent e) {
    List<Viewer.ViewerObserver> vObs = viewer.getObservers();
    for (int act : viewer.getSelectedActs()) {
      for (Viewer.ViewerObserver obs : vObs)
	obs.requestViewerOpening(viewer, new ActViewerFactory(), viewer.getActList().getFactory(), act);
    }
  }
}
