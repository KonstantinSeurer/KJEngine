/**
 * 
 */
package de.kjEngine.graphics.shader;

/**
 * @author konst
 *
 */
public interface DataType {

	public static enum Type {
		PRIMITIVE, ARRAY, STRUCT
	}
	
	public Type getType();
}
