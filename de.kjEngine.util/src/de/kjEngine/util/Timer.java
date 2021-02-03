package de.kjEngine.util;

public class Timer {
	
	private static Timer timer = new Timer(0);
	
	public static void start() {
		timer.reset();
	}
	
	public static long passed() {
		return timer.getPassedTime();
	}
	
	public static void printPassed() {
		Output.log("passed: @ms", passed() / 1000000f);
	}
	
	private long delay;
	private long lastTime;

	public Timer(long delay) {
		setDelay(delay);
		reset();
	}

	public Timer() {
		this(0);
	}

	public long getDelay() {
		return delay;
	}

	public void setDelay(long delay) {
		this.delay = delay;
	}
	
	public void time(Runnable r) {
		if (time()) {
			r.run();
		}
	}
	
	public long getPassedTime() {
		return System.nanoTime() - lastTime;
	}
	
	public long getTimeToGo() {
		return delay - getPassedTime();
	}
	
	public float getProgress() {
		return (float) getPassedTime() / (float) delay;
	}
	
	public void reset() {
		lastTime = System.nanoTime();
	}
	
	public boolean time() {
		if (getTimeToGo() <= 0) {
			reset();
			return true;
		}
		return false;
	}
}
