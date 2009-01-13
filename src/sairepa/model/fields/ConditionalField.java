package sairepa.model.fields;

import java.io.IOException;

import sairepa.model.*;

import org.xBaseJ.xBaseJException;
import org.xBaseJ.fields.Field;

/**
 * Similar to the State pattern.
 */
public class ConditionalField extends ActField
{
  private final Test test;
  private final ActField ifTrue;
  private final ActField ifFalse;
  private ActField current;

  /**
   * @param fieldPrototype is the same if the test is false or true
   * @param ifTrue can't be null
   * @param ifFalse can't be null
   */
  public ConditionalField(Field fieldPrototype, Test test, ActField ifTrue,
			  ActField ifFalse) throws xBaseJException, IOException {
    super(fieldPrototype);
    this.test = test;
    this.ifTrue = ifTrue;
    this.ifFalse = ifFalse;
    this.current = ifTrue;
  }

  @Override
  public void hasFocus(ActEntry e) {
    super.hasFocus(e);

    ActField a = test(e.getAct());
    a.hasFocus(e);
  }

  @Override
  public void notifyUpdate(ActEntry e, String previousValue) {
    super.notifyUpdate(e, previousValue);

    ActField a = test(e.getAct());
    a.notifyUpdate(e, previousValue);
  }

  @Override
  public void notifyUpdate(ActField f, ActEntry theirEntry, String previousValue) {
    super.notifyUpdate(f, theirEntry, previousValue);

    ActField a = test(theirEntry.getAct());
    a.notifyUpdate(f, theirEntry, previousValue);
  }

  @Override
  public boolean validate(ActEntry e) {
    if (!super.validate(e))
      return false;
    ActField a = test(e.getAct());
    return a.validate(e);
  }

  private ActField test(Act a) {
    return (current = (test.test(a) ? ifTrue : ifFalse));
  }
}
