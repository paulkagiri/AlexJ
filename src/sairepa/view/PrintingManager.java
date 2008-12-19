package sairepa.view;

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
import javax.swing.RepaintManager;

/**
 * Printing coffee machine.
 */
public class PrintingManager
{
  private JComponent c;
  private boolean onePage;

  private PrintingManager(JComponent c, boolean onePage) {
    this.c = c;
    this.onePage = onePage;
  }

  public static void print(JComponent c, boolean onePage) {
    new PrintingManager(c, onePage).print();
  }

  private class PrintableAdapter implements Printable {


    public PrintableAdapter() {

    }

    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
      graphics.translate((int)pageFormat.getImageableX(), (int)pageFormat.getImageableY());

      BufferedImage im = new BufferedImage(c.getWidth(), c.getHeight(),
					   BufferedImage.TYPE_INT_RGB);
      makeItWhite(c);
      c.print(im.createGraphics());
      giveItBackItsColor(c);

      Rectangle dstSize = graphics.getClipBounds();
      float scaleWidth = (float)dstSize.getWidth() / (float)c.getWidth();
      float scale = scaleWidth;
      if (onePage) {
	float scaleHeight = (float)dstSize.getHeight() / (float)c.getHeight();
	scale = ((scaleWidth < scaleHeight) ? scaleWidth : scaleHeight);
      }
      int offsetHeight = (int)(pageIndex * (dstSize.getHeight() / scale));

      if (offsetHeight >= c.getHeight() - 100) {
	return NO_SUCH_PAGE;
      }

      graphics.drawImage((Image)im, 0, -offsetHeight,
			 (int)dstSize.getWidth(),
			 (int)dstSize.getHeight(),
			 Color.WHITE,
			 (ImageObserver)null);
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
  }

  protected void print() {
    PrinterJob job = PrinterJob.getPrinterJob();
    job.setPrintable(new PrintableAdapter());
    PrintRequestAttributeSet aset =
    	new HashPrintRequestAttributeSet();
    aset.add(MediaSizeName.ISO_A4);
    if (!job.printDialog()) return;
    try {
      job.print();
    } catch (PrinterException e) {
      ErrorMessage.displayError("Can't print", e);
    }
  }
}
