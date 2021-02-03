/**
 * 
 */
package de.kjEngine.graphics;

import de.kjEngine.util.Copy;
import de.kjEngine.util.Disposable;

/**
 * @author konst
 *
 */
public abstract class Texture2D extends Texture2DData implements Disposable, Descriptor, Copy<Texture2D> {

	protected Texture2D(int width, int height, int levels, Texture2DDataProvider data, TextureFormat format, SamplingMode samplingMode, WrappingMode wrappingMode) {
		super(width, height, levels, data, format, samplingMode, wrappingMode);
	}

	@Override
	public Type getType() {
		return Type.TEXTURE_2D;
	}
	
	public abstract void updateMipmaps();

	public abstract void setData(Texture2DDataProvider data);
	
	public abstract Texture2D getImage(int level);
}
