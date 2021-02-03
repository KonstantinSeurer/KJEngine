/**
 * 
 */
package de.kjEngine.graphics;

/**
 * @author konst
 *
 */
public class Texture3DData {

	public final int width, height, length;
	public final int levels;
	public final TextureFormat format;
	public final SamplingMode samplingMode;
	public final WrappingMode wrappingMode;
	protected Texture3DDataProvider data;

	public Texture3DData(int width, int height, int length, int levels, Texture3DDataProvider data, TextureFormat format, SamplingMode samplingMode, WrappingMode wrappingMode) {
		this.width = width;
		this.height = height;
		this.length = length;
		this.format = format;
		this.levels = levels;
		this.data = data;
		this.samplingMode = samplingMode;
		this.wrappingMode = wrappingMode;
	}

	public Texture3DDataProvider getData() {
		return data;
	}
}
