package sairepa.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import sairepa.model.ActListFactory;
import sairepa.model.ActListFactoryLayout;
import sairepa.model.Model;

public class TabSelecter extends JPanel
{
  public final static long serialVersionUID = 1;
  private ArrayList<TabSelecterObserver> observers;

  private final static int SPACE_BETWEEN_FACTORY_SET = 30;

  public TabSelecter(ActListFactoryLayout actListFactories, ViewerFactory[] viewerFactories) {
    super(new BorderLayout());

    observers = new ArrayList<TabSelecterObserver>();

    ActListFactory[][] allFactories = actListFactories.getFactories();
    String[] factorySetNames = actListFactories.getFactorySetNames();

    JPanel global = new JPanel(new BorderLayout(SPACE_BETWEEN_FACTORY_SET,
						SPACE_BETWEEN_FACTORY_SET));
    JPanel veryGlobal = global;

    for (int i = 0 ; i < allFactories.length ; i++) {

      JPanel sub = new JPanel(new GridLayout(allFactories[i].length, 1, 10, 10));
      for (ActListFactory actListFactory : allFactories[i]) {
	JPanel subsub = new JPanel(new GridLayout(viewerFactories.length, 1));
	//JLabel title = new JLabel(actListFactory.toString());
	//subsub.add(title);
	for (ViewerFactory viewerFactory : viewerFactories) {
	  TabSelecterButton button = new TabSelecterButton(actListFactory, viewerFactory);
	  subsub.add(button);
	}
	subsub.setBorder(BorderFactory.createTitledBorder(actListFactory.toString()));
	sub.add(subsub);
      }
      sub.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 1),
						     factorySetNames[i]));

      global.add(sub, BorderLayout.CENTER);
      sub = new JPanel(new BorderLayout(SPACE_BETWEEN_FACTORY_SET,
					SPACE_BETWEEN_FACTORY_SET));
      global.add(sub, BorderLayout.SOUTH);
      global = sub;
    }

    JTextArea headerLabel = new JTextArea(actListFactories.getModel().getClientFile().getZipCode()
					  + " " + actListFactories.getModel().getClientFile().getCommune());
    headerLabel.setEditable(false);
    headerLabel.setLineWrap(true);
    headerLabel.setWrapStyleWord(true);
    headerLabel.setBackground(new Color(220, 220, 220));

    veryGlobal.add(headerLabel, BorderLayout.NORTH);
    this.add(veryGlobal, BorderLayout.NORTH);
    this.add(new JLabel(""), BorderLayout.CENTER);
  }

  public class TabSelecterButton extends JButton implements ActionListener {
    public final static long serialVersionUID = 1;

    private ActListFactory actListFactory;
    private ViewerFactory viewerFactory;

    protected TabSelecterButton(ActListFactory actListFactory, ViewerFactory viewerFactory) {
      super(viewerFactory.getName());
      setHorizontalAlignment(JButton.LEFT);

      this.actListFactory = actListFactory;
      this.viewerFactory = viewerFactory;
      addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
      for (TabSelecterObserver obs : observers) {
	obs.requestTabOpening(actListFactory, viewerFactory);
      }
    }
  }

  public static interface TabSelecterObserver {
    public Viewer requestTabOpening(ActListFactory actListFactory, ViewerFactory viewerFactory);
  }

  public void addObserver(TabSelecterObserver obs) {
    observers.add(obs);
  }

  public void deleteObserver(TabSelecterObserver obs) {
    observers.remove(obs);
  }
}
