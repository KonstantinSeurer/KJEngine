/**
 * 
 */
package de.kjEngine.graphics;

import de.kjEngine.util.Disposable;

/**
 * @author konst
 *
 */
public abstract class TextureCube implements Disposable, Descriptor {

	private int width, height;
	private TextureFormat format;

	protected TextureCube(int width, int height, TextureFormat format) {
		this.width = width;
		this.height = height;
		this.format = format;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public TextureFormat getFormat() {
		return format;
	}

	@Override
	public Type getType() {
		return Type.TEXTURE_CUBE;
	}
}
