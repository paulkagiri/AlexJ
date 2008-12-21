package sairepa.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import sairepa.model.ProgressionObserver;

public class SplashScreen extends JDialog implements ProgressionObserver
{
  public final static long serialVersionUID = 1;
  public final static int SIZE_X = 300;
  public final static int SIZE_Y = 150;
  private JPanel p = new JPanel(new BorderLayout());
  private JProgressBar bar = new JProgressBar(0, 100);

  public SplashScreen() {
    super();
    bar.setStringPainted(true);
    setProgression(0, "");

    getContentPane().setLayout(new GridLayout(1, 1));
    final JLabel appNameLabel = new JLabel(sairepa.Main.APPLICATION_NAME);
    appNameLabel.setFont(new Font("Dialog", Font.BOLD, 32));
    appNameLabel.setHorizontalAlignment(JLabel.CENTER);
    p.add(appNameLabel, BorderLayout.CENTER);
    p.add(bar, BorderLayout.SOUTH);
    getContentPane().add(p);

    setUndecorated(true);
    setResizable(false);

    setSize(SIZE_X, SIZE_Y);
    final Dimension screenSize =
      Toolkit.getDefaultToolkit().getScreenSize();
    setLocation(screenSize.width/2  - (SIZE_X/2),
		screenSize.height/2 - (SIZE_Y/2));
  }

  public void start() {
    setVisible(true);
  }

  public void setProgression(int progression, String txt) {
    bar.setValue(progression);
    bar.setString(txt);
    bar.repaint();
    p.paintImmediately(0,0, p.getWidth(), p.getHeight());
  }

  public void stop() {
    setVisible(false);
    dispose();
  }
}
