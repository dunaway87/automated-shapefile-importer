package utils;

public class Timer {
	private long startTime = 0;
	
	public Timer() {
		reset();
	}
	
	public void reset() {
		startTime = System.currentTimeMillis();
	}
	
	
	/**
	 * @return time in milliseconds
	 */
	public long elapsed() {
		return (System.currentTimeMillis() - startTime);
	}

}