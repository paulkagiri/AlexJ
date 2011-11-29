package sairepa.model;

public class DumbDbObserver implements ActList.ActListDbObserver {
	public DumbDbObserver() { }
	public void startOfJobBatch(String description, int nmbJob) {
		System.out.println("WARNING: DumbDbObserver used ("+description+") !");
	}
	public void jobUpdate(ActList.DbOp job, int currentPosition, int endOfJobPosition) {
		System.out.println("WARNING: DumbDbObserver used !");
	}
	public void endOfJobBatch() {
		System.out.println("WARNING: DumbDbObserver used !");
	}
}

