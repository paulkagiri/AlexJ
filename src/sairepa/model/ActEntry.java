package sairepa.model;

public class ActEntry
{
  private Act act;
  private ActField field;
  private String value;

  public ActEntry(Act act, ActField field) {
    this(act, field, "");
  }

  public ActEntry(Act act, ActField field, String value) {
    this.act = act;
    this.field = field;
    this.value = value;
  }

  public Act getAct() {
    return act;
  }

  public ActField getField() {
    return field;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value, boolean notify) {
    this.value = value;

    if (notify) {
      field.notifyUpdate(this);
    }
  }

  public void setValue(String value) {
    setValue(value, true);
  }
}
