package sairepa.view;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import sairepa.model.ProgressionObserver;
import sairepa.model.ActList;
import sairepa.model.Util;

public class SplashScreen extends JDialog implements ProgressionObserver
{
	public final static long serialVersionUID = 1;
	public final static int SIZE_X = 450;
	public final static int SIZE_Y = 150;
	private JPanel p = new JPanel(new BorderLayout());
	private JProgressBar bar = new JProgressBar(0, 100);

	private SplashScreenThread thread = null;
	private Thread rth = null;

	private JLabel label;

	public SplashScreen(MainWindow owner, String shortTxt, String longTxt) {
		this(owner, shortTxt, longTxt, null);
	}

	public SplashScreen(MainWindow owner, String shortTxt, String longTxt, Font font) {
		super(owner, true);
		setAlwaysOnTop(true);
		buildUI(shortTxt, longTxt, font);
	}

	public SplashScreen(String shortTxt, String longTxt) {
		this(shortTxt, longTxt, null);
	}

	public SplashScreen(String shortTxt, String longTxt, Font font) {
		super();
		buildUI(shortTxt, longTxt, font);
	}

	public void buildUI(String shortTxt, String longTxt, Font font) {
		bar.setStringPainted(true);

		getContentPane().setLayout(new GridLayout(1, 1));
		label = new JLabel("");
		setDescription(shortTxt, longTxt);
		if (font != null)
			label.setFont(font);
		label.setHorizontalAlignment(JLabel.CENTER);
		p.add(label, BorderLayout.CENTER);
		p.add(bar, BorderLayout.SOUTH);
		p.setBorder(BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
		getContentPane().add(p);

		setUndecorated(true);
		setResizable(false);
		setSize(SIZE_X, SIZE_Y);
		final Dimension screenSize =
			Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(screenSize.width/2  - (SIZE_X/2),
				screenSize.height/2 - (SIZE_Y/2));

		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	}

	public void setDescription(String shortTxt, String longTxt) {
		if (longTxt != null && !"".equals(longTxt))
			label.setText("<html>" + shortTxt + "<br>" + longTxt+"</html>");
		else
			label.setText(shortTxt);
	}

	protected class SplashScreenThread implements Runnable {
		private boolean loop;

		public SplashScreenThread() {
			loop = true;
		}

		private int progression = 0;
		private String txt = "";

		public void run() {
			String oldTxt = "";
			int oldProgression = 0;
			boolean upd = true;

			System.out.println("Displaying splashscreen");
			setVisible(true);

			while (loop) {
				synchronized(this) {
					if (oldProgression != progression || (oldTxt != txt && !oldTxt.equals(txt))) {
						bar.setValue(progression);
						bar.setString(txt);
						oldProgression = progression;
						oldTxt = txt;
						upd = true;
					}
				}
				if (upd) {
					System.out.println("SplashScreen: " + Long.toString(Thread.currentThread().getId()) + " ;"
							+ " progression: " + Integer.toString(progression) + " : " + txt);
					repaint();
					bar.repaint();
					p.repaint();
					p.paintImmediately(0,0, p.getWidth(), p.getHeight());
					upd = false;
				}
				try {
					Thread.sleep(100);
				} catch(InterruptedException e) {
					System.out.println("Interrupted: " + e.toString());
					/* \_o< */
				}
			}

			setCursor(Cursor.getDefaultCursor());
			System.out.println("End of splashscreen");
			setVisible(false);
			dispose();
		}

		public void updateData(int progression, String txt) {
			synchronized(this) {
				this.txt = txt;
				this.progression = progression;
			}
		}

		public void stop() {
			loop = false;
		}
	}

	public void start() {
		Util.check(thread == null);
		thread = new SplashScreenThread();
		(rth = new Thread(thread)).start();
	}

	/**
	 * @param progression in pourcent
	 */
	public void setProgression(int progression, String txt) {
		thread.updateData(progression, txt);
	}

	public void stop() {
		thread.stop();
		rth.interrupt();
		thread = null;
		rth = null;
	}

	public static class DbObserver implements ActList.ActListDbObserver {
		private SplashScreen ss;
		private int nmbJobs;
		private int currentJob;
		private boolean visible;
		private ActList.DbOp lastJob;
		int nbStart;

		public DbObserver() {
			nbStart = 0;
			visible = false;
		}

		@Override
		public void startOfJobBatch(String description, int nmbJobs) {
			if (nbStart == 0) {
				ss = new SplashScreen("Chargement : ", description);
				this.nmbJobs = nmbJobs; /* we count from 0 here */
				this.currentJob = -1;
				this.visible = false;
				this.lastJob = null;
			}
			else
			{
				ss.setDescription("Chargement : ", description);
			}
			nbStart++;
		}

		@Override
		public void jobUpdate(ActList.DbOp job, int currentPosition, int endOfJobPosition) {
			if ( job != lastJob ) {
				lastJob = job;
				currentJob++;
			}

			int fullJobProgression = 100 / nmbJobs;
			int jobProgression = (currentPosition * fullJobProgression) / endOfJobPosition;
			int progression = (currentJob * fullJobProgression) + jobProgression;

			String txt = null;
			switch(job) {

				case DB_QUERY:
					txt = "Interrogation de la base de donn\351es";
					break;

				case DB_FETCH:
					txt = "R\351cuperation des donn\351es";
					break;

				case DB_SORT:
					txt = "Tri";
					break;

			}

			sairepa.model.Util.check(txt != null);

			if (!visible) {
				ss.start();
				visible = true;
			}

			ss.setProgression(progression, txt);
		}

		@Override
		public void endOfJobBatch() {
			nbStart--;
			if (nbStart == 0) {
				ss.stop();
				ss = null;
			}
		}
	}
}
