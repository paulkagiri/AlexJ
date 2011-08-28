package sairepa.gui;

import java.net.URL;
import javax.swing.ImageIcon;

public class IconBox
{
  public final static ImageIcon minTabClose = loadIcon("tab-close.png");
  public final static ImageIcon actList     = loadIcon("act-list.png");
  public final static ImageIcon act         = loadIcon("act.png");
  public final static ImageIcon up          = loadIcon("up.png");
  public final static ImageIcon down        = loadIcon("down.png");
  public final static ImageIcon search      = loadIcon("search.png");
  public final static ImageIcon quit        = loadIcon("quit.png");
  public final static ImageIcon print       = loadIcon("print.png");
  public final static ImageIcon warning     = loadIcon("warning.png");
  public final static ImageIcon fileSave    = loadIcon("filesave.png");
  public final static ImageIcon fileOpen    = loadIcon("fileopen.png");
  public final static ImageIcon open        = loadIcon("open.png");
  public final static ImageIcon copy        = loadIcon("copy.png");
  public final static ImageIcon cut         = loadIcon("cut.png");
  public final static ImageIcon paste       = loadIcon("paste.png");
  public final static ImageIcon add         = loadIcon("list-add.png");
  public final static ImageIcon remove      = loadIcon("list-remove.png");


  private IconBox() { }

  private static ImageIcon loadIcon(final String fileName) {
    URL url;
    Class daClass;
    ClassLoader classLoader;

    classLoader = IconBox.class.getClassLoader();
    if (classLoader == null) {
      throw new RuntimeException("Icon '"+fileName+"' not found ! (ClassLoader)");
    }

    url = classLoader.getResource(fileName);
    if (url == null) {
      throw new RuntimeException("Icon '"+fileName+"' not found ! (Resource)");
    }

    return new ImageIcon(url);
  }
}
