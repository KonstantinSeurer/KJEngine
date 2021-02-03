/**
 * 
 */
package de.kjEngine.graphics;

import de.kjEngine.util.Disposable;

/**
 * @author konst
 *
 */
public abstract class Texture3D extends Texture3DData implements Disposable, Descriptor {

	protected Texture3D(int width, int height, int length, int levels, Texture3DDataProvider data, TextureFormat format, SamplingMode samplingMode, WrappingMode wrappingMode) {
		super(width, height, length, levels, data, format, samplingMode, wrappingMode);
	}

	@Override
	public Type getType() {
		return Type.TEXTURE_3D;
	}

	public abstract Texture3D getImage(int level);
}
