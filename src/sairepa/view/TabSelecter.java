package sairepa.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import sairepa.model.ActListFactory;
import sairepa.model.Model;

public class TabSelecter extends JPanel
{
  public final static long serialVersionUID = 1;
  private ArrayList<TabSelecterObserver> observers;

  public TabSelecter(ActListFactory[] actListFactories, ViewerFactory[] viewerFactories) {
    super(new BorderLayout());

    observers = new ArrayList<TabSelecterObserver>();

    JPanel sub = new JPanel(new GridLayout(actListFactories.length, 1, 20, 20));

    for (ActListFactory actListFactory : actListFactories) {
      JPanel subsub = new JPanel(new GridLayout(viewerFactories.length + 1, 1));

      JLabel title = new JLabel(actListFactory.toString());
      subsub.add(title);

      for (ViewerFactory viewerFactory : viewerFactories) {
	TabSelecterButton button = new TabSelecterButton(actListFactory, viewerFactory);
	subsub.add(button);
      }

      sub.add(subsub);
    }

    this.add(sub, BorderLayout.NORTH);
    this.add(new JLabel(""), BorderLayout.CENTER);
  }

  public class TabSelecterButton extends JButton implements ActionListener {
    public final static long serialVersionUID = 1;

    private ActListFactory actListFactory;
    private ViewerFactory viewerFactory;

    protected TabSelecterButton(ActListFactory actListFactory, ViewerFactory viewerFactory) {
      super(viewerFactory.getName(), viewerFactory.getIcon());
      setHorizontalAlignment(JButton.LEFT);

      this.actListFactory = actListFactory;
      this.viewerFactory = viewerFactory;
      addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
      for (TabSelecterObserver obs : observers) {
	obs.tabSelected(actListFactory, viewerFactory);
      }
    }
  }

  public static interface TabSelecterObserver {
    public void tabSelected(ActListFactory actListFactory, ViewerFactory viewerFactory);
  }

  public void addObserver(TabSelecterObserver obs) {
    observers.add(obs);
  }

  public void deleteObserver(TabSelecterObserver obs) {
    observers.remove(obs);
  }
}
