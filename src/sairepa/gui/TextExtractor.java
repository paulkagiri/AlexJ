/*
** TextExtractor.java
** Login : <jflesch@zeus.kwain.net>
** Started on  Sun Feb 22 16:17:18 2009 Jerome Flesch
*/
package sairepa.gui;

import javax.swing.text.JTextComponent;

public interface TextExtractor
{
  public String getText();

  public static class FieldTextExtractor implements TextExtractor {
    public JTextComponent field;
    public FieldTextExtractor(JTextComponent field) { this.field = field; }
    public String getText() { return field.getText(); }
  }
}
