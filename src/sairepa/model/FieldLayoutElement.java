package sairepa.model;

public interface FieldLayoutElement extends Iterable<ActField>
{
  public boolean validate(Act a);
}
