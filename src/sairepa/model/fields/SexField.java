package sairepa.model.fields;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sairepa.model.ActEntry;
import sairepa.model.ActField;
import sairepa.model.AutoCompleter;

import org.xBaseJ.xBaseJException;
import org.xBaseJ.fields.CharField;

public class SexField extends ActField {
  private SexAutoCompleter sexAutoCompleter;

  public SexField(String name) throws xBaseJException, IOException {
    super(new CharField(name, 1));
    this.sexAutoCompleter = new SexAutoCompleter();
  }

  public void notifyUpdate(ActEntry e, String previousValue) {
    super.notifyUpdate(e, previousValue);
    e.setValue(e.getValue().toUpperCase(), false);
  }

  public boolean validate(ActEntry e) {
    if (!super.validate(e))
      return false;

    if (!"M".equals(e.getValue())
	&& !"F".equals(e.getValue())
	&& !"-".equals(e.getValue())) {
      return false;
    }

    return true;
  }

  private static class SexAutoCompleter implements AutoCompleter {
    private final List<String> suggestions;
    public SexAutoCompleter() {
      ArrayList<String> s = new ArrayList<String>();
      s.add("F");
      s.add("M");
      s.add("-");
      suggestions = Collections.unmodifiableList(s);
    }

    public List<String> getSuggestions(ActEntry entry, String initialString) {
      return suggestions;
    }
  }

  public AutoCompleter getAutoCompleter() {
    return sexAutoCompleter;
  }

  public static Sex getSex(ActEntry e) {
    if ("M".equals(e.getValue())) {
      return Sex.MALE;
    } else if ("F".equals(e.getValue())) {
      return Sex.FEMALE;
    } else {
      return Sex.UNKNOWN;
    }
  }
}
