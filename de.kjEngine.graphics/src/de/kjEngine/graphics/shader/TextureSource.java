/**
 * 
 */
package de.kjEngine.graphics.shader;

import de.kjEngine.graphics.Descriptor.Type;

/**
 * @author konst
 *
 */
public class TextureSource extends DescriptorSource {

	public static TextureSource parse(String source) throws ShaderCompilationException {
		if (!source.startsWith("texture")) {
			throw new IllegalArgumentException();
		}
		String[] pts = source.split(" ");

		String name = "";
		if (pts.length > 1) {
			name = pts[1].trim();
		}

		String type = "texture2D";
		if (pts.length > 0) {
			type = pts[0].trim();
		}
		
		int dimensions = 0;
		if (type.startsWith("texture1D")) {
			dimensions = 1;
		} else if (type.startsWith("texture2D")) {
			dimensions = 2;
		} else if (type.startsWith("texture3D")) {
			dimensions = 3;
		}

		int arrayLength = 1;
		if (type.contains("[")) {
			arrayLength = Integer.parseInt(type.substring(type.indexOf('[') + 1, type.indexOf(']')));
		}
		
		return new TextureSource(name, arrayLength, dimensions);
	}

	private int dimensions;

	public TextureSource(String name, int arrayLength, int dimensions) {
		super(name, arrayLength);
		setDimensions(dimensions);
	}
	
	public TextureSource(String name, int dimensions) {
		this(name, 1, dimensions);
	}

	/**
	 * @return the dimensions
	 */
	public int getDimensions() {
		return dimensions;
	}

	/**
	 * @param dimensions the dimensions to set
	 */
	public void setDimensions(int dimensions) {
		if (dimensions < 0 || dimensions > 3) {
			throw new IllegalArgumentException("dimensions=" + dimensions);
		}
		this.dimensions = dimensions;
	}

	@Override
	public Type getType() {
		switch (dimensions) {
		case 1:
			return Type.TEXTURE_1D;
		case 2:
			return Type.TEXTURE_2D;
		case 3:
			return Type.TEXTURE_3D;
		}
		return null;
	}
}
