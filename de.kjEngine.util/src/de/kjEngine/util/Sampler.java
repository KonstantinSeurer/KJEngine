/**
 * 
 */
package de.kjEngine.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author konst
 *
 */
public class Sampler {

	private static class Int {
		int i;
	}

	private boolean running = true;
	private Map<StackTraceElement, Int> data = new HashMap<>();
	private int totalSampleCount;

	public Sampler(Timer timer, Thread thread) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (running) {
					if (timer.time()) {
						StackTraceElement[] stack = thread.getStackTrace();
						for (StackTraceElement e : stack) {
							if (!data.containsKey(e)) {
								data.put(e, new Int());
							}
							data.get(e).i++;
						}
						totalSampleCount++;
					}
				}
			}
		}).start();
	}

	public void stop() {
		running = false;
	}

	public Set<StackTraceElement> getSamples() {
		return data.keySet();
	}

	public int getSampleCount(StackTraceElement e) {
		return data.get(e).i;
	}

	/**
	 * @return the totalSampleCount
	 */
	public int getTotalSampleCount() {
		return totalSampleCount;
	}

	public void print(int count) {
		Set<StackTraceElement> samples = getSamples();
		count = Math.min(count, samples.size());
		for (int i = 0; i < count; i++) {
			int maxCount = 0;
			StackTraceElement maxSample = null;
			for (StackTraceElement sample : samples) {
				int sampleCount = getSampleCount(sample);
				if (sampleCount > maxCount) {
					maxCount = sampleCount;
					maxSample = sample;
				}
			}
			samples.remove(maxSample);
			System.out.println(maxSample + ": " + ((float) maxCount / (float) totalSampleCount));
		}
	}
}
