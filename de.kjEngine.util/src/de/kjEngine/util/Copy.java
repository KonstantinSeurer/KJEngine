/**
 * 
 */
package de.kjEngine.util;

/**
 * @author konst
 */
public interface Copy<T extends Copy<T>> extends Cloneable {
	
	/**
	 * @return A copy of this object. Modifications to the new object won't influence the old object. This includes referenced objects.
	 */
	public T deepCopy();
	
	/**
	 * @return A copy of this object. Direct modifications to the new object won't influence the old object. Modifications to referenced objects will result in undefined behavior.
	 */
	public T shallowCopy();
}
