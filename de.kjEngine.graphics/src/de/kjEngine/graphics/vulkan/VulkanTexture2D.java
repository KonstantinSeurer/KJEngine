/**
 * 
 */
package de.kjEngine.graphics.vulkan;

import static de.kjEngine.graphics.vulkan.VulkanUtil.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.vulkan.VK10.*;

import java.nio.LongBuffer;

import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkImageBlit;
import org.lwjgl.vulkan.VkImageViewCreateInfo;
import org.lwjgl.vulkan.VkSamplerCreateInfo;

import de.kjEngine.graphics.Graphics;
import de.kjEngine.graphics.SamplingMode;
import de.kjEngine.graphics.Texture2D;
import de.kjEngine.graphics.Texture2DData;
import de.kjEngine.graphics.Texture2DDataProvider;
import de.kjEngine.graphics.TextureFormat;
import de.kjEngine.graphics.WrappingMode;
import de.kjEngine.graphics.vulkan.VulkanUtil.CreateImageResult;
import de.kjEngine.math.HalfFloat;
import de.kjEngine.math.Vec4;
import de.kjEngine.util.ByteWriter;
import de.kjEngine.util.Endian;

/**
 * @author konst
 *
 */
public class VulkanTexture2D extends Texture2D {

	long image;
	long imageMemory;
	long[] imageViews;
	long samplerView;
	long sampler;
	int layout = VK_IMAGE_LAYOUT_UNDEFINED, vkFormat;
	int aspect;

	public VulkanTexture2D(Texture2DData data) {
		super(data.width, data.height, data.levels, data.getData(), VulkanUtil.getSupportedFormat(data.format), data.samplingMode, data.wrappingMode);

		vkFormat = VulkanUtil.getFormat(format);

		CreateImageResult cir = createImage(Graphics.getVkContext().getDevice(), width, height, levels, vkFormat, VK_IMAGE_TILING_OPTIMAL,
				VK_IMAGE_USAGE_TRANSFER_SRC_BIT | VK_IMAGE_USAGE_TRANSFER_DST_BIT | VK_IMAGE_USAGE_SAMPLED_BIT | VK_IMAGE_USAGE_STORAGE_BIT, VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT);
		image = cir.image;
		imageMemory = cir.memory;

		imageViews = new long[levels];

		aspect = VK_IMAGE_ASPECT_COLOR_BIT;

		for (int i = 0; i < levels; i++) {
			VkImageViewCreateInfo viewCreateInfo = VkImageViewCreateInfo.calloc().sType(VK_STRUCTURE_TYPE_IMAGE_VIEW_CREATE_INFO).pNext(NULL).flags(0);
			viewCreateInfo.image(image);
			viewCreateInfo.viewType(VK_IMAGE_VIEW_TYPE_2D);
			viewCreateInfo.format(vkFormat);
			viewCreateInfo.subresourceRange().aspectMask(aspect);
			viewCreateInfo.subresourceRange().baseArrayLayer(0).layerCount(1);
			viewCreateInfo.subresourceRange().baseMipLevel(i).levelCount(1);

			LongBuffer pView = memAllocLong(1);
			err(vkCreateImageView(Graphics.getVkContext().getDevice(), viewCreateInfo, null, pView));
			imageViews[i] = pView.get(0);

			memFree(pView);
			viewCreateInfo.free();
		}

		VkImageViewCreateInfo viewCreateInfo = VkImageViewCreateInfo.calloc().sType(VK_STRUCTURE_TYPE_IMAGE_VIEW_CREATE_INFO).pNext(NULL).flags(0);
		viewCreateInfo.image(image);
		viewCreateInfo.viewType(VK_IMAGE_VIEW_TYPE_2D);
		viewCreateInfo.format(vkFormat);
		viewCreateInfo.subresourceRange().aspectMask(aspect);
		viewCreateInfo.subresourceRange().baseArrayLayer(0).layerCount(1);
		viewCreateInfo.subresourceRange().baseMipLevel(0).levelCount(levels);

		LongBuffer pView = memAllocLong(1);
		err(vkCreateImageView(Graphics.getVkContext().getDevice(), viewCreateInfo, null, pView));
		samplerView = pView.get(0);

		memFree(pView);
		viewCreateInfo.free();

		VkSamplerCreateInfo samplerCreateInfo = VkSamplerCreateInfo.calloc().sType(VK_STRUCTURE_TYPE_SAMPLER_CREATE_INFO).pNext(NULL).flags(0);
		switch (samplingMode) {
		case LINEAR:
		case LINEAR_LINEAR:
		case LINEAR_NEAREST:
			samplerCreateInfo.magFilter(VK_FILTER_LINEAR);
			samplerCreateInfo.minFilter(VK_FILTER_LINEAR);
			break;
		case NEAREST:
			samplerCreateInfo.magFilter(VK_FILTER_NEAREST);
			samplerCreateInfo.minFilter(VK_FILTER_NEAREST);
			break;
		}
		switch (wrappingMode) {
		case CLAMP:
			samplerCreateInfo.addressModeU(VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_EDGE);
			samplerCreateInfo.addressModeV(VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_EDGE);
			samplerCreateInfo.addressModeW(VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_EDGE);
			break;
		case MIRRORED_REPEAT:
			samplerCreateInfo.addressModeU(VK_SAMPLER_ADDRESS_MODE_MIRRORED_REPEAT);
			samplerCreateInfo.addressModeV(VK_SAMPLER_ADDRESS_MODE_MIRRORED_REPEAT);
			samplerCreateInfo.addressModeW(VK_SAMPLER_ADDRESS_MODE_MIRRORED_REPEAT);
			break;
		case REPEAT:
			samplerCreateInfo.addressModeU(VK_SAMPLER_ADDRESS_MODE_REPEAT);
			samplerCreateInfo.addressModeV(VK_SAMPLER_ADDRESS_MODE_REPEAT);
			samplerCreateInfo.addressModeW(VK_SAMPLER_ADDRESS_MODE_REPEAT);
			break;
		}
		samplerCreateInfo.anisotropyEnable(true);
		samplerCreateInfo.maxAnisotropy(4f);
		samplerCreateInfo.borderColor(VK_BORDER_COLOR_INT_OPAQUE_BLACK);
		samplerCreateInfo.unnormalizedCoordinates(false);
		samplerCreateInfo.compareEnable(false);
		samplerCreateInfo.compareOp(VK_COMPARE_OP_ALWAYS);
		switch (samplingMode) {
		case LINEAR_LINEAR:
			samplerCreateInfo.mipmapMode(VK_SAMPLER_MIPMAP_MODE_LINEAR);
			break;
		case LINEAR_NEAREST:
			samplerCreateInfo.mipmapMode(VK_SAMPLER_MIPMAP_MODE_NEAREST);
			break;
		default:
			samplerCreateInfo.mipmapMode(VK_SAMPLER_MIPMAP_MODE_LINEAR);
			break;
		}
		samplerCreateInfo.mipLodBias(0f).minLod(0f).maxLod((float) levels);

		LongBuffer pSampler = memAllocLong(1);
		err(vkCreateSampler(Graphics.getVkContext().getDevice(), samplerCreateInfo, null, pSampler));
		sampler = pSampler.get(0);

		memFree(pSampler);
		samplerCreateInfo.free();

		if (this.data != null) {
			setData(this.data);
		}
	}

	public VulkanTexture2D(int width, int height, int levels, TextureFormat format, SamplingMode samplingMode, WrappingMode wrappingMode, long image, long imageMemory, long[] imageViews,
			long samplerView, long sampler, int vkFormat, int aspect) {
		super(width, height, levels, null, format, samplingMode, wrappingMode);
		this.vkFormat = vkFormat;

		this.image = image;
		this.imageMemory = imageMemory;
		this.imageViews = imageViews;
		this.samplerView = samplerView;
		this.sampler = sampler;
		this.aspect = aspect;
	}

	@Override
	public void dispose() {
		VkDevice device = Graphics.getVkContext().getDevice();
		vkDestroySampler(device, sampler, null);
		boolean destroySamplerView = true;
		for (long view : imageViews) {
			if (view == samplerView) {
				destroySamplerView = false;
			}
			vkDestroyImageView(device, view, null);
		}
		if (destroySamplerView) {
			vkDestroyImageView(device, samplerView, null);
		}
		vkDestroyImage(device, image, null);
		vkFreeMemory(device, imageMemory, null);
	}

	public void setLayout(VkCommandBuffer cb, int layout) {
		if (this.layout != layout) {
			transitionImageLayout(cb, image, this.layout, layout, 0, levels);
			this.layout = layout;
		}
	}

	@Override
	public void updateMipmaps() {
	}

	@Override
	public void setData(Texture2DDataProvider data) {
		byte[] buffer = null;
		Vec4 color = Vec4.create();
		switch (format) {
		case R16F:
			buffer = new byte[width * height * 2];
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					data.get(x, y, color);
					ByteWriter.write(buffer, (x + y * width) << 1, HalfFloat.floatToHalfFloat(color.x), Endian.LITTLE);
				}
			}
			break;
		case R8:
			buffer = new byte[width * height];
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					data.get(x, y, color);
					buffer[x + y * width] = (byte) (color.x * 255f);
				}
			}
			break;
		case RG16F:
			buffer = new byte[width * height * 4];
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					data.get(x, y, color);
					int baseIndex = (x + y * width) << 2;
					ByteWriter.write(buffer, baseIndex, HalfFloat.floatToHalfFloat(color.x), Endian.LITTLE);
					ByteWriter.write(buffer, baseIndex + 2, HalfFloat.floatToHalfFloat(color.y), Endian.LITTLE);
				}
			}
			break;
		case RGB16F:
			buffer = new byte[width * height * 6];
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					data.get(x, y, color);
					int baseIndex = (x + y * width) * 6;
					ByteWriter.write(buffer, baseIndex, HalfFloat.floatToHalfFloat(color.x), Endian.LITTLE);
					ByteWriter.write(buffer, baseIndex + 2, HalfFloat.floatToHalfFloat(color.y), Endian.LITTLE);
					ByteWriter.write(buffer, baseIndex + 4, HalfFloat.floatToHalfFloat(color.z), Endian.LITTLE);
				}
			}
			break;
		case RGB32F:
			buffer = new byte[width * height * 12];
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					int baseIndex = (x + y * width) * 12;
					data.get(x, y, color);
					ByteWriter.write(buffer, baseIndex, color.x);
					ByteWriter.write(buffer, baseIndex + 4, color.y);
					ByteWriter.write(buffer, baseIndex + 8, color.z);
				}
			}
			break;
		case RGB8:
			buffer = new byte[width * height * 3];
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					int baseIndex = (x + y * width) * 3;
					data.get(x, y, color);
					buffer[baseIndex] = (byte) (color.x * 255f);
					buffer[baseIndex + 1] = (byte) (color.y * 255f);
					buffer[baseIndex + 2] = (byte) (color.z * 255f);
				}
			}
			break;
		case RGBA16F:
			buffer = new byte[width * height * 8];
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					data.get(x, y, color);
					int baseIndex = (x + y * width) << 3;
					ByteWriter.write(buffer, baseIndex, HalfFloat.floatToHalfFloat(color.x), Endian.LITTLE);
					ByteWriter.write(buffer, baseIndex + 2, HalfFloat.floatToHalfFloat(color.y), Endian.LITTLE);
					ByteWriter.write(buffer, baseIndex + 4, HalfFloat.floatToHalfFloat(color.z), Endian.LITTLE);
					ByteWriter.write(buffer, baseIndex + 6, HalfFloat.floatToHalfFloat(color.w), Endian.LITTLE);
				}
			}
			break;
		case RGBA8:
			buffer = new byte[width * height * 4];
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					int baseIndex = (x + y * width) * 4;
					data.get(x, y, color);
					buffer[baseIndex] = (byte) (color.x * 255f);
					buffer[baseIndex + 1] = (byte) (color.y * 255f);
					buffer[baseIndex + 2] = (byte) (color.z * 255f);
					buffer[baseIndex + 3] = (byte) (color.w * 255f);
				}
			}
			break;
		case RGBA32F:
			buffer = new byte[width * height * 16];
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					int baseIndex = (x + y * width) * 16;
					data.get(x, y, color);
					ByteWriter.write(buffer, baseIndex, color.x);
					ByteWriter.write(buffer, baseIndex + 4, color.y);
					ByteWriter.write(buffer, baseIndex + 8, color.z);
					ByteWriter.write(buffer, baseIndex + 12, color.w);
				}
			}
			break;
		case R32F:
			buffer = new byte[width * height * 4];
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					int baseIndex = (x + y * width) << 2;
					data.get(x, y, color);
					ByteWriter.write(buffer, baseIndex, color.x);
				}
			}
			break;
		case RG32F:
			buffer = new byte[width * height * 8];
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					int baseIndex = (x + y * width) << 3;
					data.get(x, y, color);
					ByteWriter.write(buffer, baseIndex, color.x);
					ByteWriter.write(buffer, baseIndex + 4, color.y);
				}
			}
			break;
		case RG8:
			buffer = new byte[width * height * 2];
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					int baseIndex = (x + y * width) * 2;
					data.get(x, y, color);
					buffer[baseIndex] = (byte) (color.x * 255f);
					buffer[baseIndex + 1] = (byte) (color.y * 255f);
				}
			}
			break;
		}

		StagingBuffer sb = StagingBuffer.get(buffer.length);
		sb.data.put(0, buffer);

		VkCommandBuffer cb = beginSingleTimeCommandBuffer();

		setLayout(cb, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL);

		copyBufferToImage(cb, sb.buffer.getBuffer(), image, width, height);

		int mipMapWidth = width, mipMapHeight = height;

		for (int i = 0; i < levels - 1; i++) {
			transitionImageLayout(cb, image, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL, VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL, i, 1);
			flushMemoryBarriers();

			VkImageBlit.Buffer pRegions = VkImageBlit.calloc(1);

			pRegions.get(0).srcOffsets().get(0).set(0, 0, 0);
			pRegions.get(0).dstOffsets().get(0).set(0, 0, 0);
			pRegions.get(0).srcOffsets().get(1).set(mipMapWidth, mipMapHeight, 1);
			pRegions.get(0).dstOffsets().get(1).set(mipMapWidth / 2, mipMapHeight / 2, 1);
			pRegions.get(0).srcSubresource().set(aspect, i, 0, 1);
			pRegions.get(0).dstSubresource().set(aspect, i + 1, 0, 1);

			vkCmdBlitImage(cb, image, VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL, image, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL, pRegions, VK_FILTER_NEAREST);

			pRegions.free();

			mipMapWidth /= 2;
			mipMapHeight /= 2;
		}

		endSingleTimeCommandBuffer(cb);
	}

	@Override
	public Texture2D getImage(int level) {
		return new VulkanImage2D(this, level);
	}

	@Override
	public Texture2D deepCopy() {
		return new VulkanTexture2D(this);
	}

	@Override
	public Texture2D shallowCopy() {
		return new VulkanTexture2D(width, height, levels, format, samplingMode, wrappingMode, image, imageMemory, imageViews, samplerView, sampler, vkFormat, aspect);
	}
}
