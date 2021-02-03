/**
 * 
 */
package de.kjEngine.util;

/**
 * @author konst
 *
 */
public abstract class ExecutableLoop implements Runnable {
	
	private int start, end;

	public ExecutableLoop(int start, int end) {
		this.start = start;
		this.end = end;
	}

	@Override
	public void run() {
		for (int i = start; i < end; i++) {
			run(i);
		}
	}
	
	protected abstract void run(int index);
}
