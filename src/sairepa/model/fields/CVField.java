package sairepa.model.fields;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sairepa.model.Act;
import sairepa.model.ActEntry;
import sairepa.model.ActField;
import sairepa.model.AutoCompleter;

import org.xBaseJ.xBaseJException;
import org.xBaseJ.fields.CharField;

public class CVField extends ActField {
  public CVField(String name) throws xBaseJException, IOException {
    super(new CharField(name, 1));
  }

  public void notifyUpdate(ActEntry e, String previousValue) {
    super.notifyUpdate(e, previousValue);
    e.setValue(e.getValue().toUpperCase(), false);
  }

  private final AutoCompleter autoCompleter = new CVAutoCompleter();

  private static class CVAutoCompleter implements AutoCompleter {
    private final List<String> suggestions;
    public CVAutoCompleter() {
      ArrayList<String> s = new ArrayList<String>();
      s.add("C");
      s.add("V");
      s.add("-");
      suggestions = Collections.unmodifiableList(s);
    }

    public List<String> getSuggestions(ActEntry entry, String initialString) {
      return suggestions;
    }
  }

  @Override
  public boolean hasAutoCompleter() {
    return true;
  }

  @Override
  public AutoCompleter getAutoCompleter(Act a) {
    return autoCompleter;
  }

  public boolean validate(ActEntry e) {
    if (!super.validate(e))
      return false;

    if (!"C".equals(e.getValue())
	&& !"V".equals(e.getValue())
	&& !"-".equals(e.getValue())) {
      return false;
    }

    return true;
  }
}
