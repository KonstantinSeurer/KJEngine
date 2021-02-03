/**
 * 
 */
package de.kjEngine.util;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @author konst
 *
 */
public class DefaultExecutor implements Executor {
	
	private Queue<Runnable> queue = new LinkedList<>();

	public DefaultExecutor() {
	}

	@Override
	public Executor queue(Runnable method) {
		queue.add(method);
		return this;
	}

	@Override
	public Executor flush() {
		return this;
	}

	@Override
	public Executor sync() {
		Runnable r;
		while ((r = queue.poll()) != null) {
			r.run();
		}
		return this;
	}
	
	public boolean hasFinished() {
		return queue.isEmpty();
	}
	
	public int getQueueSize() {
		return queue.size();
	}
}
