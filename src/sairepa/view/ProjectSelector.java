package sairepa.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

import sairepa.model.Project;

public class ProjectSelector extends JFrame implements ActionListener
{
  public final static long serialVersionUID = 1;

  private JList projectList;
  private JButton okButton = new JButton("Ok");
  private JButton cancelButton = new JButton("Annuler");

  public final static int SIZE_X = 200;
  public final static int SIZE_Y = 200;

  public ProjectSelector(Vector<Project> projects) {
    super("SaiRePa");
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    getRootPane().setLayout(new BorderLayout(5, 5));

    JLabel label = new JLabel("Veuillez selection un projet");
    projectList = new JList(projects);
    JPanel buttonPanel = new JPanel(new GridLayout(1, 2));
    buttonPanel.add(okButton);
    buttonPanel.add(cancelButton);

    okButton.addActionListener(this);
    cancelButton.addActionListener(this);

    getRootPane().add(label, BorderLayout.NORTH);
    getRootPane().add(projectList, BorderLayout.CENTER);
    getRootPane().add(buttonPanel, BorderLayout.SOUTH);

    setLocation((int)(screenSize.getWidth() - SIZE_X) /2,
		(int)(screenSize.getHeight() - SIZE_Y) /2);
    setSize(200, 200);
  }

  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == okButton) {
      selection = projectList.getSelectedValue();
    }

    synchronized(this) {
      this.notifyAll();
    }
  }

  private Object selection;

  public Project promptUser() {
    selection = null;
    setVisible(true);

    try {
      synchronized(this) {
	this.wait();
      }
    } catch(InterruptedException e) {
      throw new RuntimeException(e);
    } finally {
      setVisible(false);
      this.dispose();
    }

    return (Project)selection;
  }
}
