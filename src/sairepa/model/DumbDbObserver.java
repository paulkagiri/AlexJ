package sairepa.model;

public class DumbDbObserver implements ActList.ActListDbObserver {
	public DumbDbObserver() { }
	public void startOfJobBatch(int nmbJob) { }
	public void jobUpdate(ActList.DbOp job, int currentPosition, int endOfJobPosition) { }
	public void endOfJobBatch() { }
}

