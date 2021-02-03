/**
 * 
 */
package de.kjEngine.math;

/**
 * @author konst
 *
 */
public class Average {
	
	private int count;
	private float average;

	public Average() {
	}
	
	public void reset() {
		count = 0;
		average = 0f;
	}
	
	public void add(float value) {
		average = (average * count + value) / (count + 1);
		count++;
	}

	public float getAverage() {
		return average;
	}
}
