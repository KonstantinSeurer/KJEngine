/**
 * 
 */
package de.kjEngine.graphics;

import de.kjEngine.graphics.shader.InterfaceBlockSource;
import de.kjEngine.math.Vec4;
import de.kjEngine.util.Disposable;

/**
 * @author konst
 *
 */
public abstract class FrameBuffer implements Disposable {
	
	protected int width, height;
	protected InterfaceBlockSource source;
	protected Vec4 clearColor = Vec4.create();

	public FrameBuffer(int width, int height, InterfaceBlockSource source) {
		this.width = width;
		this.height = height;
		this.source = source;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	/**
	 * @return the source
	 */
	public InterfaceBlockSource getSource() {
		return source;
	}

	/**
	 * @return the clearColor
	 */
	public Vec4 getClearColor() {
		return clearColor;
	}

	public abstract Texture2D getColorAttachment(String attachment);
	public abstract Texture2D getDepthAttachment();
	
	public abstract byte[] getPixels(String attachment);
}
