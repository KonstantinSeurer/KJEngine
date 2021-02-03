/**
 * 
 */
package de.kjEngine.graphics;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import de.kjEngine.math.Real;
import de.kjEngine.math.Vec2;
import de.kjEngine.math.Vec4;

/**
 * @author konst
 *
 */
public class Texture2DData {

	private static final float O_D_255 = 1f / 255f;

	public static Texture2DData create(BufferedImage img, SamplingMode samplingMode, WrappingMode wrappingMode) {
		int width = img.getWidth();
		int height = img.getHeight();

		int levels = samplingMode == SamplingMode.LINEAR_NEAREST || samplingMode == SamplingMode.LINEAR_LINEAR ? Real.log(2, Math.min(width, height)) : 1;
		
		switch (img.getType()) {
		case BufferedImage.TYPE_3BYTE_BGR: {
			byte[] pixels = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();

			return new Texture2DData(width, height, levels, new Texture2DDataProvider() {

				@Override
				public void get(int x, int y, Vec4 target) {
					int baseIndex = (x + (height - y - 1) * width) * 3;
					target.x = O_D_255 * signedByteToInt(pixels[baseIndex + 2]);
					target.y = O_D_255 * signedByteToInt(pixels[baseIndex + 1]);
					target.z = O_D_255 * signedByteToInt(pixels[baseIndex]);
					target.w = 1f;
				}
			}, TextureFormat.RGB8, samplingMode, wrappingMode);
		}
		case BufferedImage.TYPE_4BYTE_ABGR: {
			byte[] pixels = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
			
			return new Texture2DData(width, height, levels, new Texture2DDataProvider() {

				@Override
				public void get(int x, int y, Vec4 target) {
					int baseIndex = (x + (height - y - 1) * width) * 4;
					target.x = O_D_255 * signedByteToInt(pixels[baseIndex + 3]);
					target.y = O_D_255 * signedByteToInt(pixels[baseIndex + 2]);
					target.z = O_D_255 * signedByteToInt(pixels[baseIndex + 1]);
					target.w = O_D_255 * signedByteToInt(pixels[baseIndex]);
				}
			}, TextureFormat.RGBA8, samplingMode, wrappingMode);
		}
		case BufferedImage.TYPE_CUSTOM: {
			int type = img.getColorModel().getColorSpace().getType();
			if (type == ColorSpace.TYPE_RGB) {
				return new Texture2DData(width, height, levels, new Texture2DDataProvider() {

					@Override
					public void get(int x, int y, Vec4 target) {
						int pixel = img.getRGB(x, height - y - 1);
						target.x = O_D_255 * ((pixel >> 16) & 0xff);
						target.y = O_D_255 * ((pixel >> 8) & 0xff);
						target.z = O_D_255 * ((pixel >> 0) & 0xff);
						target.w = 1f;
					}
				}, TextureFormat.RGB8, samplingMode, wrappingMode);
			}
		}
		case BufferedImage.TYPE_BYTE_GRAY: {
			byte[] pixels = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();

			return new Texture2DData(width, height, levels, new Texture2DDataProvider() {

				@Override
				public void get(int x, int y, Vec4 target) {
					target.x = O_D_255 * signedByteToInt(pixels[x + (height - y - 1) * width]);
					target.y = 0f;
					target.z = 0f;
					target.w = 1f;
				}
			}, TextureFormat.R8, samplingMode, wrappingMode);
		}
		default: {
			System.err.println("Unsupported format: " + img.getType());

			int[] pixels = new int[width * height];

			img.getRGB(0, 0, width, height, pixels, 0, width);

			return new Texture2DData(width, height, levels, new Texture2DDataProvider() {

				@Override
				public void get(int x, int y, Vec4 target) {
					int pixel = pixels[x + (height - y - 1) * width];
					target.x = O_D_255 * ((pixel >> 24) & 0xff);
					target.y = O_D_255 * ((pixel >> 16) & 0xff);
					target.z = O_D_255 * ((pixel >> 8) & 0xff);
					target.w = O_D_255 * (pixel & 0xff);
				}
			}, TextureFormat.RGBA8, samplingMode, wrappingMode);
		}
		}
	}
	
	private static int signedByteToInt(byte b) {
		if (b < 0) {
			return b + 256;
		}
		return b;
	}

	public final int width, height, levels;
	public final TextureFormat format;
	public final SamplingMode samplingMode;
	public final WrappingMode wrappingMode;
	protected Texture2DDataProvider data;

	public Texture2DData(int width, int height, int levels, Texture2DDataProvider data, TextureFormat format, SamplingMode samplingMode, WrappingMode wrappingMode) {
		this.width = width;
		this.height = height;
		this.levels = levels;
		this.format = format;
		this.data = data;
		this.samplingMode = samplingMode;
		this.wrappingMode = wrappingMode;
	}

	public Texture2DDataProvider getData() {
		return data;
	}

	public Vec4 getColor(Vec2 coord, Vec4 target) {
		return getColor(coord.x, coord.y, target);
	}

	public Vec4 getColor(float x, float y, Vec4 target) {
		int px = (int) (x * width);
		int py = (int) (y * height);
		data.get(px, py, target);
		return target;
	}
}
