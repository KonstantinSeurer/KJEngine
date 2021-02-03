/**
 * 
 */
package de.kjEngine.util.container;

/**
 * @author konst
 *
 */
public interface WriteArray<T> {

	public T add(T e);
	
	public void add(ReadArray<T> e);

	public T add(int i, T e);
	
	public void add(int i, ReadArray<T> e);

	public void remove(T e);
	
	public void remove(ReadArray<T> e);
	
	public T remove(int i);
	
	public void remove(int i, int count);
	
	public void removeAll(BooleanFunction<T> remove);
	
	public void clear(boolean eraseElements);
	
	public T set(int i, T e);
	
	public WriteArray<T> view(int offset, int length);
}
