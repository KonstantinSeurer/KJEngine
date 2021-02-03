/**
 * 
 */
package de.kjEngine.graphics.shader;

import de.kjEngine.graphics.TextureFormat;
import de.kjEngine.graphics.Descriptor.Type;

/**
 * @author konst
 *
 */
public class ImageSource extends DescriptorSource {

	public static ImageSource parse(String source) throws ShaderCompilationException {
		if (!source.contains("image")) {
			throw new IllegalArgumentException();
		}
		String[] pts = source.split(" ");

		String name = pts[pts.length - 1].trim();
		String type = pts[pts.length - 2].trim();

		TextureFormat format = TextureFormat.RGBA8;
		boolean read = true;
		boolean write = true;

		for (int i = 0; i < pts.length - 2; i++) {
			String part = pts[i].trim();
			switch (part) {
			case "readonly":
				read = true;
				write = false;
				break;
			case "writeonly":
				read = false;
				write = true;
				break;
			default:
				boolean found = false;
				for (TextureFormat f : TextureFormat.values()) {
					if (f.toString().equals(part)) {
						format = f;
						found = true;
						break;
					}
				}
				if (!found) {
					throw new UnexpectedValueException(0, part);
				}
			}
		}
		
		int dimensions = 0;
		if (type.startsWith("image1D")) {
			dimensions = 1;
		} else if (type.startsWith("image2D")) {
			dimensions = 2;
		} else if (type.startsWith("image3D")) {
			dimensions = 3;
		}
		
		int arrayLength = 1;
		if (type.contains("[")) {
			arrayLength = Integer.parseInt(type.substring(type.indexOf('['), type.indexOf(']')));
		}

		return new ImageSource(name, arrayLength, dimensions, format, read, write);
	}

	private int dimensions;
	private TextureFormat format;
	private boolean read, write;

	/**
	 * @param name
	 */
	public ImageSource(String name, int arrayLength, int dimensions, TextureFormat format, boolean read, boolean write) {
		super(name, arrayLength);
		setDimensions(dimensions);
		this.format = format;
		this.read = read;
		this.write = write;
	}
	
	public ImageSource(String name, int dimensions, TextureFormat format, boolean read, boolean write) {
		this(name, 1, dimensions, format, read, write);
	}

	/**
	 * @return the dimensions
	 */
	public int getDimensions() {
		return dimensions;
	}

	/**
	 * @return the format
	 */
	public TextureFormat getFormat() {
		return format;
	}

	/**
	 * @return the read
	 */
	public boolean isRead() {
		return read;
	}

	/**
	 * @return the write
	 */
	public boolean isWrite() {
		return write;
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

	/**
	 * @param format the format to set
	 */
	public void setFormat(TextureFormat format) {
		this.format = format;
	}

	/**
	 * @param read the read to set
	 */
	public void setRead(boolean read) {
		this.read = read;
	}

	/**
	 * @param write the write to set
	 */
	public void setWrite(boolean write) {
		this.write = write;
	}

	@Override
	public Type getType() {
		switch (dimensions) {
		case 1:
			return null;
		case 2:
			return Type.IMAGE_2D;
		case 3:
			return Type.IMAGE_3D;
		}
		return null;
	}
}
