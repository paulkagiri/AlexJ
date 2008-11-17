package sairepa.model;

import java.io.File;

/**
 * Exactly the same than BaptismListFactory except the filename is different
 */
public class ConfirmationListFactory extends BaptismListFactory
{
  public ConfirmationListFactory(File projectDir)
      throws java.io.FileNotFoundException {
    super(projectDir, "confirm5.dbf");
  }

  public String toString() {
    return "Confirmations";
  }
}
