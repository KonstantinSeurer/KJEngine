/**
 * 
 */
package de.kjEngine.graphics;

import de.kjEngine.util.Disposable;

/**
 * @author konst
 *
 */
public interface Descriptor extends Disposable {
	
	public static enum Type {
		UNIFORM_BUFFER, STORAGE_BUFFER, TEXTURE_2D, TEXTURE_1D, TEXTURE_CUBE, TEXTURE_3D, IMAGE_3D, IMAGE_2D, ACCELERATION_STRUCTURE
	}

	public Type getType();
}
