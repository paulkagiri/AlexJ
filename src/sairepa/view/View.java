package sairepa.view;

import sairepa.model.Model;

public class View
{
  private MainWindow mainWindow;

  public View(Model model) {
    mainWindow = new MainWindow(model);
  }

  public void init() {
    mainWindow.setVisible(true);
  }

  public void close() {
    mainWindow.setVisible(false);
    mainWindow.dispose();
  }
}
