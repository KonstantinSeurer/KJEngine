/**
 * 
 */
package de.kjEngine.graphics.vulkan;

import de.kjEngine.graphics.Texture2D;
import de.kjEngine.graphics.Texture2DDataProvider;

/**
 * @author konst
 *
 */
public class VulkanImage2D extends Texture2D {
	
	VulkanTexture2D texture;
	int level;

	/**
	 * @param width
	 * @param height
	 * @param levels
	 * @param data
	 * @param format
	 * @param samplingMode
	 * @param wrappingMode
	 */
	public VulkanImage2D(VulkanTexture2D texture, int level) {
		super(texture.width, texture.height, 1, texture.getData(), texture.format, texture.samplingMode, texture.wrappingMode);
		this.texture = texture;
		this.level = level;
	}

	@Override
	public void dispose() {
		texture.dispose();
	}

	@Override
	public void updateMipmaps() {
		texture.updateMipmaps();
	}

	@Override
	public void setData(Texture2DDataProvider data) {
		texture.setData(data);
	}

	@Override
	public Texture2D getImage(int level) {
		return texture.getImage(level);
	}

	@Override
	public Type getType() {
		return Type.IMAGE_2D;
	}

	@Override
	public Texture2D deepCopy() {
		return shallowCopy();
	}

	@Override
	public Texture2D shallowCopy() {
		return new VulkanImage2D(texture, level);
	}
}
