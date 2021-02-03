/**
 * 
 */
package de.kjEngine.util;

/**
 * @author konst
 *
 */
public class MultiThreadedExecutor implements Executor {
	
	private SingleThreadedExecutor[] threads;

	public MultiThreadedExecutor(int threadCount) {
		threads = new SingleThreadedExecutor[threadCount];
		for (int i = 0; i < threadCount; i++) {
			threads[i] = new SingleThreadedExecutor();
		}
	}

	@Override
	public Executor queue(Runnable method) {
		SingleThreadedExecutor e = threads[0];
		for (SingleThreadedExecutor t : threads) {
			if (t.getImplementation().getQueueSize() < e.getImplementation().getQueueSize()) {
				e = t;
			}
		}
		e.queue(method);
		return this;
	}

	@Override
	public Executor flush() {
		for (SingleThreadedExecutor e : threads) {
			e.flush();
		}
		return this;
	}

	@Override
	public Executor sync() {
		for (SingleThreadedExecutor e : threads) {
			e.sync();
		}
		return this;
	}
}
