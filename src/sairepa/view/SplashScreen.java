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

public class SplashScreen extends JDialog implements ProgressionObserver
{
	public final static long serialVersionUID = 1;
	public final static int SIZE_X = 300;
	public final static int SIZE_Y = 150;
	private JPanel p = new JPanel(new BorderLayout());
	private JProgressBar bar = new JProgressBar(0, 100);

	private SplashScreenThread thread = null;
	private Thread rth = null;

	public SplashScreen(String txt) {
		this(txt, null);
	}

	public SplashScreen(String txt, Font font) {
		super();
		bar.setStringPainted(true);

		getContentPane().setLayout(new GridLayout(1, 1));
		final JLabel appNameLabel = new JLabel(txt);
		if (font != null)
			appNameLabel.setFont(font);
		appNameLabel.setHorizontalAlignment(JLabel.CENTER);
		p.add(appNameLabel, BorderLayout.CENTER);
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

	protected class SplashScreenThread implements Runnable {
		private boolean loop = true;

		public SplashScreenThread() {
		}

		private int progression = 0;
		private String txt = "";

		public void run() {
			setVisible(true);

			while (loop) {
				synchronized(this) {
					bar.setValue(progression);
					bar.setString(txt);
				}

				repaint();
				bar.repaint();
				p.repaint();
				p.paintImmediately(0,0, p.getWidth(), p.getHeight());
				try {
					Thread.sleep(1000);
				} catch(InterruptedException e) {
					/* \_o< */
				}
			}

			setCursor(Cursor.getDefaultCursor());
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
		thread = new SplashScreenThread();
		(rth = new Thread(thread)).start();
	}

	/**
	 * @param progression in pourcent
	 */
	public void setProgression(int progression, String txt) {
		System.out.println("SplashScreen: progression: " + Integer.toString(progression) + " : " + txt);
		thread.updateData(progression, txt);
		rth.interrupt();
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
		private ActList.DbHandling lastJob;

		public DbObserver() {
		}

		@Override
			public void startOfJobBatch(int nmbJobs) {
				ss = new SplashScreen("Chargement"); /* TODO(Jflesch): L10n */
				this.nmbJobs = nmbJobs; /* we count from 0 here */
				this.currentJob = -1;
				this.visible = false;
				this.lastJob = null;
			}

		@Override
			public void jobUpdate(ActList.DbHandling job, int currentPosition, int endOfJobPosition) {
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
						txt = "Interrogation de la base de donn\351es"; /* TODO(Jflesch): l10n */
						break;

					case DB_FETCH:
						txt = "R\351cuperation des donn\351es"; /* TODO(Jflesch): l10n */
						break;

					case DB_SORT:
						txt = "Triage"; /* TODO(Jflesch): l10n */
						break;

				}

				sairepa.model.Util.check(txt != null);

				if (!visible)
					ss.start();

				ss.setProgression(progression, txt);
			}

		@Override
			public void endOfJobBatch() {
				ss.stop();
				ss = null;
			}
	}
}
