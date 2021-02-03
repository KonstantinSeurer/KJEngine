/**
 * 
 */
package de.kjEngine.graphics;

import de.kjEngine.util.Disposable;

/**
 * @author konst
 *
 */
public class DoubleBufferedDescriptor<T extends Disposable> implements Disposable {
	
	private T a, b;
	private boolean state;

	public DoubleBufferedDescriptor(T a, T b) {
		this.a = a;
		this.b = b;
	}
	
	public T getCurrent() {
		return state ? b : a;
	}
	
	public void swap() {
		state = !state;
	}

	/**
	 * @return the a
	 */
	public T getA() {
		return a;
	}

	/**
	 * @return the b
	 */
	public T getB() {
		return b;
	}

	@Override
	public void dispose() {
		a.dispose();
		b.dispose();
	}
}
