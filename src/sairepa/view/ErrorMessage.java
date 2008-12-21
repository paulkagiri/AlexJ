package sairepa.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.OutputStream;
import java.io.PrintStream;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import sairepa.gui.IconBox;

public class ErrorMessage implements ActionListener
{
  private JDialog dialog;
  private static final int SIZE_X = 500;
  private static final int SIZE_Y = 400;

  public static void displayError(Exception e) {
    displayError(null, null, e);
  }

  public static void displayError(String msg, Exception e) {
    displayError(null, msg, e);
  }

  public static void displayError(String msg) {
    displayError(null, msg, null);
  }

  public static void displayError(Component parent, Exception e) {
    displayError(parent, null, e);
  }

  private ErrorMessage(Component parent, Component insidePane) {
    if (parent instanceof Frame) {
      dialog = new JDialog((Frame)parent, "Erreur");
    } else {
      dialog = new JDialog((Dialog)parent, "Erreur");
    }

    JLabel errorLabel = new JLabel("Erreur");
    errorLabel.setIcon(IconBox.warning);

    JPanel south = new JPanel(new BorderLayout());
    south.add(new JLabel(""), BorderLayout.CENTER);
    JButton okButton = new JButton("Ok");
    okButton.setPreferredSize(new java.awt.Dimension(75, 40));
    okButton.addActionListener(this);
    south.add(okButton, BorderLayout.EAST);

    dialog.getContentPane().setLayout(new BorderLayout());
    dialog.getContentPane().add(errorLabel, BorderLayout.NORTH);
    dialog.getContentPane().add(insidePane, BorderLayout.CENTER);
    dialog.getContentPane().add(south, BorderLayout.SOUTH);
    dialog.setSize(SIZE_X, SIZE_Y);
    dialog.setResizable(true);
    dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
  }

  public void setVisible(boolean v) {
    dialog.setVisible(v);
  }

  public void actionPerformed(ActionEvent e) {
    dialog.setVisible(false);
    dialog.dispose();
  }

  public static void displayError(Component parent, String message,
      Exception e) {
    Component insidePanel = null;

    if (message != null && e != null) {
      insidePanel = createErrorPanel(new String[] {
	  message, addDetailHeader(extractStackTrace(e))
	});
    } else if (message == null && e != null) {
      insidePanel = createErrorPanel(new String[] {
	  addDetailHeader(extractStackTrace(e))
	});
    } else {
	insidePanel = createErrorPanel(new String[] {
		message
	    });
    }

    ErrorMessage msg = new ErrorMessage(parent, insidePanel);
    msg.setVisible(true);
  }

  public static String addDetailHeader(String str) {
    return ("Details:\n" +
	    "========\n" +
	    str);
  }

  private static class StackTraceExtractor extends OutputStream {
    private StringBuilder builder;

    public StackTraceExtractor() {
      builder = new StringBuilder();
    }

    public void write(int c) {
      builder.append((char)c);
    }

    public String toString() {
      return builder.toString();
    }

    public void close() {
      builder = new StringBuilder();
    }
  }

  public static String extractStackTrace(Exception e) {
    StackTraceExtractor ste = new StackTraceExtractor();
    e.printStackTrace(new PrintStream(ste));
    return ste.toString();
  }

  public static Component createErrorPanel(String[] msgs) {
    JPanel panel = new JPanel(new GridLayout(msgs.length, 1, 5, 5));

    for (int i = 0 ; i < msgs.length ; i++) {
      JTextArea area = new JTextArea(msgs[i]);
      area.setLineWrap(true);
      area.setWrapStyleWord(true);
      area.setEditable(false);
      panel.add(new JScrollPane(area));
    }

    return panel;
  }
}
