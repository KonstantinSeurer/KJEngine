/**
 * 
 */
package de.kjEngine.graphics.shader;

import de.kjEngine.graphics.Descriptor;

/**
 * @author konst
 *
 */
public abstract class DescriptorSource {
	
	private String name;
	private int arrayLength;

	public DescriptorSource(String name, int arrayLength) {
		setName(name);
		setArrayLength(arrayLength);
	}
	
	public final boolean isArray() {
		return arrayLength > 1;
	}

	public String getName() {
		return name;
	}

	public DescriptorSource setName(String name) {
		this.name = name;
		return this;
	}

	public int getArrayLength() {
		return arrayLength;
	}

	public DescriptorSource setArrayLength(int arrayLength) {
		if (arrayLength < 1) {
			throw new IllegalArgumentException();
		}
		this.arrayLength = arrayLength;
		return this;
	}

	public abstract Descriptor.Type getType();
}
