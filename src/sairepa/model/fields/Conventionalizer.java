package sairepa.model.fields;

import sairepa.model.Model;
import sairepa.model.Util;

public interface Conventionalizer
{
  public String conventionalize(String name, Sex sex);

  public final static Conventionalizer CONV_FIRST_NAME = new Conventionalizer() {
    public String conventionalize(String name, Sex sex) {
      String conv = Util.conventionalizeFirstName(name, sex);
      if ("".equals(conv.trim()) || "-".equals(conv.trim())) return conv.trim();
      return Model.getPrncvDb().getPrncv(conv, sex);
    }
  };

  public final static Conventionalizer CONV_LAST_NAME = new Conventionalizer()  {
    public String conventionalize(String name, Sex sex) {
      return Util.conventionalizeLastName(name, sex);
    }
  };
}
