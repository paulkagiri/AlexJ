package sairepa.model;

/**
 * Used to watch the progression of loadings/savings.
 */
public interface ProgressionObserver
{
  public void setProgression(int progression, String txt);

  public final static ProgressionObserver DUMB_OBSERVER = new ProgressionObserver() {
      public void setProgression(int i, String s) { }
    };
}
