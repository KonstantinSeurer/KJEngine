/**
 * 
 */
package de.kjEngine.util;

/**
 * @author konst
 *
 */
public class SingleThreadedExecutor implements Executor {

	private Thread thread;
	private DefaultExecutor executer = new DefaultExecutor();
	private Object sync = new Object();

	public SingleThreadedExecutor() {
		thread = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					synchronized (sync) {
						try {
							sync.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					synchronized (executer) {
						executer.sync();
					}
				}
			}
		});
		thread.start();
	}

	@Override
	public Executor queue(Runnable method) {
		synchronized (executer) {
			executer.queue(method);
		}
		return this;
	}

	@Override
	public Executor flush() {
		synchronized (sync) {
			sync.notify();
		}
		return this;
	}

	@Override
	public Executor sync() {
		while (true) {
			synchronized (executer) {
				if (executer.hasFinished()) {
					break;
				}
			}
		}
		return this;
	}

	public DefaultExecutor getImplementation() {
		return executer;
	}
}
