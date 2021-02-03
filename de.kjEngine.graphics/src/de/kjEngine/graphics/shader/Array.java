/**
 * 
 */
package de.kjEngine.graphics.shader;

/**
 * @author konst
 *
 */
public class Array implements DataType {
	
	private DataType elementType;
	private int length;

	public Array(DataType elementType, int length) {
		this.elementType = elementType;
		this.length = length;
	}

	/**
	 * @return the elementType
	 */
	public DataType getElementType() {
		return elementType;
	}

	/**
	 * @return the length
	 */
	public int getLength() {
		return length;
	}

	/**
	 * @param elementType the elementType to set
	 */
	public void setElementType(DataType elementType) {
		this.elementType = elementType;
	}

	/**
	 * @param length the length to set
	 */
	public void setLength(int length) {
		this.length = length;
	}

	@Override
	public Type getType() {
		return Type.ARRAY;
	}
}
