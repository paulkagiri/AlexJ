package sairepa.model;

public class ActField
{
  private String name;

  public ActField(String fieldName) {
    this.name = fieldName;
  }

  public String getName() {
    return name;
  }

  protected void notifyUpdate(ActEntry e) {
    // by default, do nothing.
  }
}
