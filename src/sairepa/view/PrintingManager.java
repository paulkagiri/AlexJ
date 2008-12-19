package sairepa.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.*;
import java.awt.print.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.standard.MediaSizeName;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.RepaintManager;

import sairepa.model.Util;

/**
 * Printing coffee machine.
 */
public class PrintingManager
{
  private JComponent component;
  private boolean onePage;
  private String title;

  private PrintingManager(JComponent c, String title, boolean onePage) {
    this.component = c;
    this.onePage = onePage;
    this.title = title;
  }

  public static void print(JComponent c, String title, boolean onePage) {
    new PrintingManager(c, title, onePage).print();
  }

  public static void print(JComponent c, boolean onePage) {
    print(c, null, onePage);
  }

  private class PrintableAdapter implements Printable {
    private JComponent toPrint;

    private JDialog tmpDialog = null;;

    public PrintableAdapter() {
      if (title != null) {
	JPanel p = new JPanel(new BorderLayout(10, 10));
	p.add(new JLabel(title), BorderLayout.NORTH);
	p.add(component, BorderLayout.CENTER);
	toPrint = p;
      } else {
	toPrint = component;
      }

      tmpDialog = new JDialog();
      tmpDialog.add(toPrint);
      tmpDialog.pack();
    }

    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
      graphics = graphics.create((int)pageFormat.getImageableX(), (int)pageFormat.getImageableY(),
				 (int)pageFormat.getImageableWidth(), (int)pageFormat.getImageableHeight());

      Rectangle pageSize = graphics.getClipBounds();
      float scaleWidth = (float)pageSize.getWidth() / (float)toPrint.getWidth();
      float scale = scaleWidth;
      if (onePage) {
	float scaleHeight = (float)pageSize.getHeight() / (float)toPrint.getHeight();
	scale = ((scaleWidth < scaleHeight) ? scaleWidth : scaleHeight);
      }

      int resizedImgWidth = (int)(scale * (float)toPrint.getWidth()); // resized img width
      int resizedImgHeight = (int)(scale * (float)toPrint.getHeight()); // resised img total height

      BufferedImage im = new BufferedImage(toPrint.getWidth(), toPrint.getHeight(),
					   BufferedImage.TYPE_INT_RGB);
      makeItWhite(toPrint);
      Graphics imGraph = im.createGraphics();
      toPrint.print(imGraph);
      giveItBackItsColor(toPrint);

      int offsetHeight = pageIndex * ((int)pageSize.getHeight());
      if (offsetHeight >= resizedImgHeight - 10) {
	return NO_SUCH_PAGE;
      }
      Util.check(graphics.drawImage((Image)im, 0, -1 * offsetHeight,
				    resizedImgWidth, resizedImgHeight,
				    Color.WHITE,
				    (ImageObserver)null));
      return(PAGE_EXISTS);
    }

    private LinkedList<Color> colors = new LinkedList<Color>();

    private void makeItWhite(JComponent c) {
      colors.add(c.getBackground());
      c.setBackground(Color.WHITE);

      for (int i = 0 ; i < c.getComponentCount() ; i++) {
	if (!(c.getComponent(i) instanceof JComponent)) {
	  continue;
	}

	JComponent child = (JComponent)c.getComponent(i);
	makeItWhite(child);
      }
    }

    private void giveItBackItsColor(JComponent c) {
      c.setBackground(colors.remove());

      for (int i = 0 ; i < c.getComponentCount() ; i++) {
	if (!(c.getComponent(i) instanceof JComponent)) {
	  continue;
	}

	JComponent child = (JComponent)c.getComponent(i);
	giveItBackItsColor(child);
      }
    }

    public void dispose() {
      tmpDialog.dispose();
    }
  }

  protected void print() {
    PrinterJob job = PrinterJob.getPrinterJob();
    PrintableAdapter a = new PrintableAdapter();
    job.setPrintable(a);
    PrintRequestAttributeSet aset =
    	new HashPrintRequestAttributeSet();
    aset.add(MediaSizeName.ISO_A4);
    if (!job.printDialog()) return;
    try {
      job.print();
    } catch (PrinterException e) {
      ErrorMessage.displayError("Can't print", e);
    } finally {
      a.dispose();
    }
  }
}
