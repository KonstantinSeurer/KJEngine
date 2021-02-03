/**
 * 
 */
package de.kjEngine.util;

/**
 * @author konst
 *
 */
public class Time {
	
	private static long startTimeMillis, startTimeNanos;
	
	static {
		startTimeNanos = System.nanoTime();
		startTimeMillis = (long) nanosToMillis(startTimeNanos);
	}

	public static long millis() {
		return System.currentTimeMillis() - startTimeMillis;
	}
	
	public static long nanos() {
		return System.nanoTime() - startTimeNanos;
	}
	
	public static float seconds() {
		return nanosToSeconds(nanos());
	}
	
	public static float nanosToMillis(long nanos) {
		return nanos * 0.000001f;
	}
	
	public static float nanosToSeconds(long nanos) {
		return nanos * 0.000000001f;
	}
	
	public static float millisToSeconds(long millis) {
		return millis * 0.001f;
	}

	/**
	 * @param seconds
	 * @return
	 */
	public static long secondsToNanos(float seconds) {
		return (long) (1000000000f * seconds);
	}
	
	public static long secondsToMillis(float seconds) {
		return (long) (1000f * seconds);
	}

	public static long millisToNanos(long millis) {
		return millis * 1000000L;
	}
}
