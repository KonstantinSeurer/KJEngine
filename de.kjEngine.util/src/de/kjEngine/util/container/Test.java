/**
 * 
 */
package de.kjEngine.util.container;

import java.util.ArrayList;

import de.kjEngine.util.Time;

/**
 * @author konst
 *
 */
public class Test {

	public static void main(String[] args) {
		int iterations = 200000;
		int size = 1000;
		
		long arrayMin = Long.MAX_VALUE;
		for (int i = 0; i < iterations; i++) {
			long time = Time.nanos();

			Array<String> array = new Array<>();
			for (int j = 0; j < size; j++) {
				array.add("0");
			}

			arrayMin = Math.min(arrayMin, Time.nanos() - time);
		}

		System.out.println(Time.nanosToMillis(arrayMin));

		long listMin = Long.MAX_VALUE;
		for (int i = 0; i < iterations; i++) {
			long time = Time.nanos();

			ArrayList<String> array = new ArrayList<>();
			for (int j = 0; j < size; j++) {
				array.add("0");
			}

			listMin = Math.min(listMin, Time.nanos() - time);
		}

		System.out.println(Time.nanosToMillis(listMin));
		
		System.out.println("Array was " + ((double) listMin / (double) arrayMin) + " times faster");
	}
}
