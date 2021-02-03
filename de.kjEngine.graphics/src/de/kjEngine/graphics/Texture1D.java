/**
 * 
 */
package de.kjEngine.graphics;

import de.kjEngine.util.Disposable;

/**
 * @author konst
 *
 */
public abstract class Texture1D extends Texture1DData implements Disposable, Descriptor {
	
	public Texture1D(int width, int levels, Texture1DDataProvider data, TextureFormat format, SamplingMode samplingMode, WrappingMode wrappingMode) {
		super(width, levels, data, format, samplingMode, wrappingMode);
	}

	@Override
	public Type getType() {
		return Type.TEXTURE_1D;
	}
}
