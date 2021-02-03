/**
 * 
 */
package de.kjEngine.util.container;

/**
 * @author konst
 *
 */
public interface ReadWriteArray<T> extends ReadArray<T>, WriteArray<T> {

	public ReadWriteArray<T> view(int offset, int length);
}
