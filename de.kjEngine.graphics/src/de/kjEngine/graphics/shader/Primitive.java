/**
 * 
 */
package de.kjEngine.graphics.shader;

/**
 * @author konst
 *
 */
public enum Primitive implements DataType {
	FLOAT, VEC2, VEC3, VEC4, MAT2, MAT3, MAT4;

	@Override
	public Type getType() {
		return Type.PRIMITIVE;
	}
}
