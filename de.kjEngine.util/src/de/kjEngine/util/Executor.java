/**
 * 
 */
package de.kjEngine.util;

/**
 * @author konst
 *
 */
public interface Executor {

	public Executor queue(Runnable method);

	public Executor flush();

	public Executor sync();
}
