package sairepa.view;

import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextField;

import sairepa.model.ActList;
import sairepa.model.ActListFactory;

public class ActViewerFactory implements ViewerFactory
{
  public final static String NAME = "Fiche";

  public ActViewerFactory() { }

  public String getName() {
    return NAME;
  }

  public Viewer createViewer(MainWindow mainWindow, ActList list) {
    return new ActViewer(mainWindow, list);
  }
}
