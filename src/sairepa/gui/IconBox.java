package sairepa.gui;

import java.net.URL;
import javax.swing.ImageIcon;

public class IconBox
{
  public final static ImageIcon minTabClose = loadIcon("tab-close.png");

  private IconBox() { }

  private static ImageIcon loadIcon(final String fileName) {
    URL url;
    Class daClass;
    ClassLoader classLoader;

    daClass = IconBox.class;

    if (daClass == null) {
      throw new RuntimeException("Icon '"+fileName+"' not found ! (Class)");
    }

    classLoader = daClass.getClassLoader();

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
