package sairepa.model.fields;

import sairepa.model.AutoCompleter;
import sairepa.model.Model;
import sairepa.model.PrncvDb;
import sairepa.model.Util;

public interface Conventionalizer
{
  public String conventionalize(String name, Sex sex);
  public AutoCompleter getAutoCompleter(String fieldName, Sex sex);

  public final static Conventionalizer CONV_FIRST_NAME = new Conventionalizer() {
      public String conventionalize(String name, Sex sex) {
	String conv = Util.conventionalizeFirstName(name, sex);
	if ("".equals(conv.trim()) || "-".equals(conv.trim())) return Util.trim(conv);
	return Model.getPrncvDb().getPrncv(conv, sex);
      }
      public AutoCompleter getAutoCompleter(String fieldName, Sex sex) {
	PrncvDb pdb = Model.getPrncvDb();
	return pdb.createAutoCompleter(sex);
      }
  };

  public final static Conventionalizer CONV_LAST_NAME = new Conventionalizer()  {
      public String conventionalize(String name, Sex sex) {
	return Util.conventionalizeLastName(name, sex);
      }
      public AutoCompleter getAutoCompleter(String fieldName, Sex sex) {
	return new AutoCompleter.DefaultAutoCompleter(fieldName);
      }
  };
}
