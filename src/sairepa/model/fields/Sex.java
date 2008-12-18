package sairepa.model.fields;

public enum Sex
{
  MALE(0),
  FEMALE(1),
  UNKNOWN(2);


  private int num;

  Sex(int num) {
    this.num = num;
  }

  public int toInteger() {
    return num;
  }

  public String toString() {
    switch(num) {
    case(0): return "male";
    case(1): return "female";
    default: return "unknown";
    }
  }

  public static Sex getSex(String str) {
    if ("m".equals(str.trim().toLowerCase())) {
      return Sex.MALE;
    } else if ("f".equals(str.trim().toLowerCase())) {
      return Sex.FEMALE;
    } else {
      return Sex.UNKNOWN;
    }
  }
}
