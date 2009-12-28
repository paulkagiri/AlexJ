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
import org.xBaseJ.fields.NumField;

public class NumericField extends ActField {
  private int min;
  private int max;
  private final AutoCompleter autoCompleter;

  public NumericField(String fieldName, int size) throws xBaseJException, IOException {
    this(fieldName, size, 0, ((int)Math.pow(10, size))-1);
  }

  public NumericField(String fieldName, int size, int min, int max) throws xBaseJException, IOException {
    super(new NumField(fieldName, size, 0));
    this.min = min;
    this.max = max;
    autoCompleter = new NumericAutoCompleter();
  }

  public void notifyUpdate(ActEntry a, String previousValue) {
    a.setValue(a.getValue().trim(), false);
  }

  private class NumericAutoCompleter implements AutoCompleter {
    private final List<String> suggestions;
    public NumericAutoCompleter() {
      ArrayList<String> s = new ArrayList<String>();
      for (int i = min ; i <= max ; i++)
	s.add(Integer.toString(i));
      suggestions = Collections.unmodifiableList(s);
    }

    public List<String> getSuggestions(ActEntry entry, String initialString) {
      return suggestions;
    }
  }

  @Override
  public boolean hasAutoCompleter() {
    return (max-min) <= 50;
  }

  @Override
  public AutoCompleter getAutoCompleter(Act a) {
    return autoCompleter;
  }

  public boolean validate(ActEntry e) {
    int i;
    if ( e == null || e.getValue() == null )
	return false;
    try {
	i = Integer.valueOf(e.getValue().trim());
    } catch (NumberFormatException exception) {
      return false;
    }
    return (i >= min && i <= max);
  }
}
