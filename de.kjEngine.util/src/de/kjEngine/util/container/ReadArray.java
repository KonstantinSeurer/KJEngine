/**
 * 
 */
package de.kjEngine.util.container;

/**
 * @author konst
 *
 */
public interface ReadArray<T> extends Iterable<T> {
	
	public int length();
	
	public boolean isEmpty();
	
	public boolean isNotEmpty();
	
	public boolean contains(T e);
	
	public int indexOf(T e);
	
	public T get(int i);
	
	public T get(BooleanFunction<T> find);
	
	public ReadArray<T> view(int offset, int length);
	
	public T[] get(T[] target);
}
