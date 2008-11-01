package sairepa.model;

import java.util.List;

public class Act
{
  private List<ActEntry> entries;

  public Act(List<ActEntry> entries) {
    this.entries = entries;
  }

  public List<ActEntry> getEntries() {
    return entries;
  }

  public void update() {
    // TODO : Update DB
  }
}
