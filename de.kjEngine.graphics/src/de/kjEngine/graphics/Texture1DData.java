/**
 * 
 */
package de.kjEngine.graphics;

import de.kjEngine.math.Vec4;

/**
 * @author konst
 *
 */
public class Texture1DData {

	public final int width, levels;
	public final TextureFormat format;
	public final SamplingMode samplingMode;
	public final WrappingMode wrappingMode;
	protected Texture1DDataProvider data;

	public Texture1DData(int width, int levels, Texture1DDataProvider data, TextureFormat format, SamplingMode samplingMode, WrappingMode wrappingMode) {
		this.width = width;
		this.levels = levels;
		this.format = format;
		this.data = data;
		this.samplingMode = samplingMode;
		this.wrappingMode = wrappingMode;
	}

	public Texture1DDataProvider getData() {
		return data;
	}

	public Vec4 getColor(float x, Vec4 target) {
		int px = (int) (x * width);
		data.get(px, target);
		return target;
	}
}
