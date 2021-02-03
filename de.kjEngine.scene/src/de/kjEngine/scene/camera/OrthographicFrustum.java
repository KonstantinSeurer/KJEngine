/**
 * 
 */
package de.kjEngine.scene.camera;

import de.kjEngine.math.Mat4;

/**
 * @author konst
 *
 */
public class OrthographicFrustum extends Frustum {
	
	private float width = 2f, height = 2f, length = 2f;

	public OrthographicFrustum() {
		Mat4.orthographic(width, height, length, projection);
	}

	/**
	 * @return the width
	 */
	public float getWidth() {
		return width;
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth(float width) {
		this.width = width;
		Mat4.orthographic(width, height, length, projection);
	}

	/**
	 * @return the height
	 */
	public float getHeight() {
		return height;
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight(float height) {
		this.height = height;
		Mat4.orthographic(width, height, length, projection);
	}

	/**
	 * @return the length
	 */
	public float getLength() {
		return length;
	}

	/**
	 * @param length the length to set
	 */
	public void setLength(float length) {
		this.length = length;
		Mat4.orthographic(width, height, length, projection);
	}

	public void setSize(float width, float height, float length) {
		this.width = width;
		this.height = height;
		this.length = length;
		Mat4.orthographic(width, height, length, projection);
	}
}
