package sairepa.model;

/**
 * Used to watch the progression of loadings/savings.
 */
public interface ProgressionObserver
{
  public void setProgression(int progression, String txt);
}
