/**
 * 
 */
package de.kjEngine.graphics.vulkan;

import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.vulkan.VK10.*;

import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.PointerBuffer;
import org.lwjgl.vulkan.KHRSwapchain;
import org.lwjgl.vulkan.NVGLSLShader;
import org.lwjgl.vulkan.VkBufferCopy;
import org.lwjgl.vulkan.VkBufferCreateInfo;
import org.lwjgl.vulkan.VkBufferImageCopy;
import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkCommandBufferAllocateInfo;
import org.lwjgl.vulkan.VkCommandBufferBeginInfo;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkFormatProperties;
import org.lwjgl.vulkan.VkImageCreateInfo;
import org.lwjgl.vulkan.VkImageFormatProperties;
import org.lwjgl.vulkan.VkImageMemoryBarrier;
import org.lwjgl.vulkan.VkMemoryAllocateInfo;
import org.lwjgl.vulkan.VkMemoryRequirements;
import org.lwjgl.vulkan.VkPhysicalDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceMemoryProperties;
import org.lwjgl.vulkan.VkSubmitInfo;

import de.kjEngine.graphics.Descriptor;
import de.kjEngine.graphics.Graphics;
import de.kjEngine.graphics.TextureFormat;
import de.kjEngine.graphics.shader.BlendFactor;
import de.kjEngine.graphics.shader.Primitive;

/**
 * @author konst
 *
 */
public class VulkanUtil {

	public static void err(int err) {
		if (err != VK_SUCCESS) {
			String name = "null";
			switch (err) {
			case VK_NOT_READY:
				name = "VK_NOT_READY";
				break;
			case VK_TIMEOUT:
				name = "VK_TIMEOUT";
				break;
			case VK_EVENT_SET:
				name = "VK_EVENT_SET";
				break;
			case VK_EVENT_RESET:
				name = "VK_EVENT_RESET";
				break;
			case VK_INCOMPLETE:
				name = "VK_INCOMPLETE";
				break;
			case KHRSwapchain.VK_SUBOPTIMAL_KHR:
				name = "VK_SUBOPTIMAL_KHR";
				break;
			case VK_ERROR_DEVICE_LOST:
				name = "VK_ERROR_DEVICE_LOST";
				break;
			case VK_ERROR_EXTENSION_NOT_PRESENT:
				name = "VK_ERROR_EXTENSION_NOT_PRESENT";
				break;
			case VK_ERROR_FEATURE_NOT_PRESENT:
				name = "VK_ERROR_FEATURE_NOT_PRESENT";
				break;
			case VK_ERROR_FORMAT_NOT_SUPPORTED:
				name = "VK_ERROR_FORMAT_NOT_SUPPORTED";
				break;
			case VK_ERROR_FRAGMENTED_POOL:
				name = "VK_ERROR_FRAGMENTED_POOL";
				break;
			case VK_ERROR_INCOMPATIBLE_DRIVER:
				name = "VK_ERROR_INCOMPATIBLE_DRIVER";
				break;
			case VK_ERROR_INITIALIZATION_FAILED:
				name = "VK_ERROR_INITIALIZATION_FAILED";
				break;
			case VK_ERROR_LAYER_NOT_PRESENT:
				name = "VK_ERROR_LAYER_NOT_PRESENT";
				break;
			case VK_ERROR_MEMORY_MAP_FAILED:
				name = "VK_ERROR_MEMORY_MAP_FAILED";
				break;
			case VK_ERROR_OUT_OF_DEVICE_MEMORY:
				name = "VK_ERROR_OUT_OF_DEVICE_MEMORY";
				break;
			case VK_ERROR_OUT_OF_HOST_MEMORY:
				name = "VK_ERROR_OUT_OF_HOST_MEMORY";
				break;
			case VK_ERROR_TOO_MANY_OBJECTS:
				name = "VK_ERROR_TOO_MANY_OBJECTS";
				break;
			case NVGLSLShader.VK_ERROR_INVALID_SHADER_NV:
				name = "VK_ERROR_INVALID_SHADER_NV";
				break;
			}
			System.err.println("Vulkan error: " + Integer.toUnsignedLong(err) + "(" + name + ")");
			Thread.dumpStack();
			System.exit(1);
		}
	}

	public static int getBlendFactor(BlendFactor f) {
		switch (f) {
		case DESTINATION_ALPHA:
			return VK_BLEND_FACTOR_DST_ALPHA;
		case DESTINATION_COLOR:
			return VK_BLEND_FACTOR_DST_COLOR;
		case ONE:
			return VK_BLEND_FACTOR_ONE;
		case ONE_MINUS_DESTINATION_ALPHA:
			return VK_BLEND_FACTOR_ONE_MINUS_DST_ALPHA;
		case ONE_MINUS_DESTINATION_COLOR:
			return VK_BLEND_FACTOR_ONE_MINUS_DST_COLOR;
		case ONE_MINUS_SOURCE_ALPHA:
			return VK_BLEND_FACTOR_ONE_MINUS_SRC_ALPHA;
		case ONE_MINUS_SOURCE_COLOR:
			return VK_BLEND_FACTOR_ONE_MINUS_SRC_COLOR;
		case SOURCE_ALPHA:
			return VK_BLEND_FACTOR_SRC_ALPHA;
		case SOURCE_COLOR:
			return VK_BLEND_FACTOR_SRC_COLOR;
		case ZERO:
			return VK_BLEND_FACTOR_ZERO;
		}
		return 0;
	}

	public static class CreateImageResult {
		public long image, memory;
	}

	public static CreateImageResult createImage(VkDevice device, int width, int height, int levels, int format, int tiling, int usage, int properties) {
		VkImageCreateInfo imageCreateInfo = VkImageCreateInfo.calloc().sType(VK_STRUCTURE_TYPE_IMAGE_CREATE_INFO).pNext(NULL).flags(0);
		imageCreateInfo.imageType(VK_IMAGE_TYPE_2D);
		imageCreateInfo.extent().width(width).height(height).depth(1);
		imageCreateInfo.mipLevels(levels);
		imageCreateInfo.arrayLayers(1);
		imageCreateInfo.format(format);
		imageCreateInfo.tiling(tiling);
		imageCreateInfo.initialLayout(VK_IMAGE_LAYOUT_UNDEFINED);
		imageCreateInfo.usage(usage);
		imageCreateInfo.sharingMode(VK_SHARING_MODE_EXCLUSIVE);
		imageCreateInfo.samples(VK_SAMPLE_COUNT_1_BIT);

		LongBuffer pImage = memAllocLong(1);
		err(vkCreateImage(Graphics.getVkContext().getDevice(), imageCreateInfo, null, pImage));
		long image = pImage.get(0);

		memFree(pImage);
		imageCreateInfo.free();

		VkMemoryRequirements memoryRequirements = VkMemoryRequirements.calloc();
		vkGetImageMemoryRequirements(Graphics.getVkContext().getDevice(), image, memoryRequirements);

		VkMemoryAllocateInfo allocateInfo = VkMemoryAllocateInfo.calloc().sType(VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO).pNext(NULL);
		allocateInfo.allocationSize(memoryRequirements.size());
		allocateInfo.memoryTypeIndex(findMemoryType(memoryRequirements.memoryTypeBits(), properties));

		LongBuffer pImageMemory = memAllocLong(1);
		err(vkAllocateMemory(Graphics.getVkContext().getDevice(), allocateInfo, null, pImageMemory));
		long imageMemory = pImageMemory.get(0);

		memFree(pImageMemory);
		allocateInfo.free();
		memoryRequirements.free();

		err(vkBindImageMemory(Graphics.getVkContext().getDevice(), image, imageMemory, 0));

		CreateImageResult result = new CreateImageResult();
		result.image = image;
		result.memory = imageMemory;

		return result;
	}

	public static int findMemoryType(int type, int properties) {
		VkPhysicalDeviceMemoryProperties memoryProperties = VkPhysicalDeviceMemoryProperties.calloc();
		vkGetPhysicalDeviceMemoryProperties(Graphics.getVkContext().getPhysicalDevice(), memoryProperties);

		for (int i = 0; i < memoryProperties.memoryTypeCount(); i++) {
			if ((type & (1 << i)) != 0 && (memoryProperties.memoryTypes().get(i).propertyFlags() & properties) == properties) {
				memoryProperties.free();
				return i;
			}
		}

		memoryProperties.free();

		System.err.println("No memory type found!");
		Thread.dumpStack();
		return -1;
	}

	public static VulkanBuffer createBuffer(long size, int usage, int properties) {
		VkBufferCreateInfo bufferCreateInfo = VkBufferCreateInfo.calloc().sType(VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO).pNext(NULL).flags(0);
		bufferCreateInfo.size(size);
		bufferCreateInfo.usage(usage);
		bufferCreateInfo.sharingMode(VK_SHARING_MODE_EXCLUSIVE);

		LongBuffer pBuffer = memAllocLong(1);
		err(vkCreateBuffer(Graphics.getVkContext().getDevice(), bufferCreateInfo, null, pBuffer));
		long buffer = pBuffer.get(0);

		VkMemoryRequirements memoryRequirements = VkMemoryRequirements.calloc();
		vkGetBufferMemoryRequirements(Graphics.getVkContext().getDevice(), buffer, memoryRequirements);

		VkMemoryAllocateInfo allocateInfo = VkMemoryAllocateInfo.calloc().sType(VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO).pNext(NULL);
		allocateInfo.allocationSize(memoryRequirements.size());
		allocateInfo.memoryTypeIndex(findMemoryType(memoryRequirements.memoryTypeBits(), properties));

		LongBuffer pBufferMemory = memAllocLong(1);
		err(vkAllocateMemory(Graphics.getVkContext().getDevice(), allocateInfo, null, pBufferMemory));
		long bufferMemory = pBufferMemory.get(0);

		err(vkBindBufferMemory(Graphics.getVkContext().getDevice(), buffer, bufferMemory, 0));

		memFree(pBuffer);
		bufferCreateInfo.free();
		memoryRequirements.free();
		allocateInfo.free();
		memFree(pBufferMemory);

		return new VulkanBuffer() {

			@Override
			public long getMemory() {
				return bufferMemory;
			}

			@Override
			public long getBuffer() {
				return buffer;
			}
		};
	}

	public static void copyBuffer(long src, long dst, long srcOff, long dstOff, long size) {
		VkCommandBuffer cmd = beginSingleTimeCommandBuffer();

		VkBufferCopy.Buffer copy = VkBufferCopy.calloc(1);
		copy.get(0).srcOffset(srcOff).dstOffset(dstOff);
		copy.get(0).size(size);

		vkCmdCopyBuffer(cmd, src, dst, copy);

		endSingleTimeCommandBuffer(cmd);

		copy.free();
	}

	public static int getFormat(Primitive type) {
		switch (type) {
		case FLOAT:
			return VK_FORMAT_R32_SFLOAT;
		case MAT2:
			break;
		case MAT3:
			break;
		case MAT4:
			break;
		case VEC2:
			return VK_FORMAT_R32G32_SFLOAT;
		case VEC3:
			return VK_FORMAT_R32G32B32_SFLOAT;
		case VEC4:
			return VK_FORMAT_R32G32B32A32_SFLOAT;
		}
		return -1;
	}

	private static VkCommandBuffer SINGLE_TIME_COMMAND_BUFFER = null;

	public static VkCommandBuffer beginSingleTimeCommandBuffer() {
		if (SINGLE_TIME_COMMAND_BUFFER == null) {
			VkCommandBufferAllocateInfo allocateInfo = VkCommandBufferAllocateInfo.calloc().sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO).pNext(NULL);
			allocateInfo.commandPool(Graphics.getVkContext().getDynamicCommandPool());
			allocateInfo.commandBufferCount(1);
			allocateInfo.level(VK_COMMAND_BUFFER_LEVEL_PRIMARY);

			PointerBuffer pCommandBuffer = memAllocPointer(1);
			err(vkAllocateCommandBuffers(Graphics.getVkContext().getDevice(), allocateInfo, pCommandBuffer));
			SINGLE_TIME_COMMAND_BUFFER = new VkCommandBuffer(pCommandBuffer.get(0), Graphics.getVkContext().getDevice());

			allocateInfo.free();
			memFree(pCommandBuffer);
		}

		VkCommandBufferBeginInfo beginInfo = VkCommandBufferBeginInfo.calloc().sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO).pNext(NULL).flags(VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT);
		err(vkBeginCommandBuffer(SINGLE_TIME_COMMAND_BUFFER, beginInfo));
		beginInfo.free();

		return SINGLE_TIME_COMMAND_BUFFER;
	}

	public static void endSingleTimeCommandBuffer(VkCommandBuffer cmd) {
		err(vkEndCommandBuffer(cmd));

		VkSubmitInfo submitInfo = VkSubmitInfo.calloc().sType(VK_STRUCTURE_TYPE_SUBMIT_INFO).pNext(NULL);
		PointerBuffer pCommandBuffer = memAllocPointer(1).put(cmd.address()).flip();
		submitInfo.pCommandBuffers(pCommandBuffer);
		LongBuffer pWaitSemaphores = null;
		long prevSemaphore = Graphics.getVkContext().getSemaphoreChain().peek();
		if (prevSemaphore != VK_NULL_HANDLE) {
			pWaitSemaphores = memAllocLong(1).put(0, prevSemaphore);
		}
		submitInfo.pWaitSemaphores(pWaitSemaphores);
		LongBuffer pSignalSemaphores = memAllocLong(1).put(0, Graphics.getVkContext().getSemaphoreChain().push());
		submitInfo.pSignalSemaphores(pSignalSemaphores);
		IntBuffer pWaitStages = memAllocInt(1).put(0, VK_PIPELINE_STAGE_ALL_GRAPHICS_BIT | VK_PIPELINE_STAGE_COMPUTE_SHADER_BIT | VK_PIPELINE_STAGE_TRANSFER_BIT);
		submitInfo.pWaitDstStageMask(pWaitStages);

		err(vkQueueSubmit(Graphics.getVkContext().getGraphicsQueue(), submitInfo, VK_NULL_HANDLE));
		err(vkQueueWaitIdle(Graphics.getVkContext().getGraphicsQueue()));

		memFree(pCommandBuffer);
		if (pWaitSemaphores != null) {
			memFree(pWaitSemaphores);
		}
		memFree(pSignalSemaphores);
		memFree(pWaitStages);
		submitInfo.free();
	}

	public static void transitionImageLayout(long image, int oldLayout, int newLayout, int baseLevel, int levels) {
		VkCommandBuffer cmd = beginSingleTimeCommandBuffer();
		transitionImageLayout(cmd, image, oldLayout, newLayout, baseLevel, levels);
		endSingleTimeCommandBuffer(cmd);
	}

	private static class ImageMemoryBarrier {
		VkCommandBuffer commandBuffer;
		long image;
		int oldLayout, newLayout;
		int baseLevel;
		int levels;

		public ImageMemoryBarrier(VkCommandBuffer commandBuffer, long image, int oldLayout, int newLayout, int baseLevel, int levels) {
			this.commandBuffer = commandBuffer;
			this.image = image;
			this.oldLayout = oldLayout;
			this.newLayout = newLayout;
			this.baseLevel = baseLevel;
			this.levels = levels;
		}
	}

	private static List<ImageMemoryBarrier> imageBarriers = new ArrayList<>();

	public static void transitionImageLayout(VkCommandBuffer cmd, long image, int oldLayout, int newLayout, int baseLevel, int levels) {
		imageBarriers.add(new ImageMemoryBarrier(cmd, image, oldLayout, newLayout, baseLevel, levels));
	}

	public static void flushMemoryBarriers() {
		if (!imageBarriers.isEmpty()) {
			int oldStage = 0, newStage = 0;

			VkCommandBuffer cb = null;

			VkImageMemoryBarrier.Buffer barriers = VkImageMemoryBarrier.calloc(imageBarriers.size());
			for (int i = 0; i < imageBarriers.size(); i++) {
				ImageMemoryBarrier b = imageBarriers.get(i);
				VkImageMemoryBarrier barrier = barriers.get(i);
				barrier.sType(VK_STRUCTURE_TYPE_IMAGE_MEMORY_BARRIER).pNext(NULL);
				barrier.oldLayout(b.oldLayout).newLayout(b.newLayout);
				barrier.srcQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED);
				barrier.dstQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED);
				barrier.image(b.image);
				barrier.subresourceRange().aspectMask(VK_IMAGE_ASPECT_COLOR_BIT);
				barrier.subresourceRange().levelCount(b.levels).baseMipLevel(b.baseLevel);
				barrier.subresourceRange().layerCount(1).baseArrayLayer(0);
				barrier.srcAccessMask(getAccessMaskFromLayout(b.oldLayout));
				barrier.dstAccessMask(getAccessMaskFromLayout(b.newLayout));

				oldStage |= getStageFromLayout(b.oldLayout);
				newStage |= getStageFromLayout(b.newLayout);
				
				cb = b.commandBuffer;
			}

			vkCmdPipelineBarrier(cb, oldStage, newStage, 0, null, null, barriers);

			barriers.free();
			
			imageBarriers.clear();
		}
	}

	private static int getStageFromLayout(int layout) {
		switch (layout) {
		case VK_IMAGE_LAYOUT_UNDEFINED:
			return VK_PIPELINE_STAGE_TOP_OF_PIPE_BIT;
		case VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL:
		case VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL:
			return VK_PIPELINE_STAGE_TRANSFER_BIT;
		case VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL:
		case KHRSwapchain.VK_IMAGE_LAYOUT_PRESENT_SRC_KHR:
			return VK_PIPELINE_STAGE_ALL_GRAPHICS_BIT;
		case VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL:
		case VK_IMAGE_LAYOUT_DEPTH_STENCIL_READ_ONLY_OPTIMAL:
			return VK_PIPELINE_STAGE_ALL_GRAPHICS_BIT | VK_PIPELINE_STAGE_COMPUTE_SHADER_BIT;
		case VK_IMAGE_LAYOUT_GENERAL:
			return VK_PIPELINE_STAGE_ALL_GRAPHICS_BIT | VK_PIPELINE_STAGE_COMPUTE_SHADER_BIT;
		case VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL:
			return VK_PIPELINE_STAGE_EARLY_FRAGMENT_TESTS_BIT | VK_PIPELINE_STAGE_LATE_FRAGMENT_TESTS_BIT;
		}
		System.err.println("Unsupported image layout: " + layout);
		return 0;
	}

	private static int getAccessMaskFromLayout(int layout) {
		switch (layout) {
		case VK_IMAGE_LAYOUT_UNDEFINED:
			return 0x0;
		case VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL:
			return VK_ACCESS_TRANSFER_WRITE_BIT;
		case VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL:
			return VK_ACCESS_TRANSFER_READ_BIT;
		case VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL:
			return VK_ACCESS_COLOR_ATTACHMENT_READ_BIT | VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT;
		case VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL:
		case VK_IMAGE_LAYOUT_DEPTH_STENCIL_READ_ONLY_OPTIMAL:
			return VK_ACCESS_SHADER_READ_BIT;
		case KHRSwapchain.VK_IMAGE_LAYOUT_PRESENT_SRC_KHR:
			return VK_ACCESS_COLOR_ATTACHMENT_READ_BIT | VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT;
		case VK_IMAGE_LAYOUT_GENERAL:
			return VK_ACCESS_COLOR_ATTACHMENT_READ_BIT | VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT | VK_ACCESS_DEPTH_STENCIL_ATTACHMENT_READ_BIT | VK_ACCESS_DEPTH_STENCIL_ATTACHMENT_WRITE_BIT
					| VK_ACCESS_HOST_READ_BIT | VK_ACCESS_HOST_WRITE_BIT | VK_ACCESS_INPUT_ATTACHMENT_READ_BIT | VK_ACCESS_MEMORY_READ_BIT | VK_ACCESS_MEMORY_WRITE_BIT | VK_ACCESS_SHADER_READ_BIT
					| VK_ACCESS_SHADER_WRITE_BIT | VK_ACCESS_TRANSFER_READ_BIT | VK_ACCESS_TRANSFER_WRITE_BIT;
		case VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL:
			return VK_ACCESS_DEPTH_STENCIL_ATTACHMENT_READ_BIT | VK_ACCESS_DEPTH_STENCIL_ATTACHMENT_WRITE_BIT;
		}
		System.err.println("Unsupported image layout: " + layout);
		return 0;
	}

	public static void copyBufferToImage(VkCommandBuffer cmd, long buffer, long image, int width, int height) {
		VkBufferImageCopy.Buffer copy = VkBufferImageCopy.calloc(1);
		copy.bufferOffset(0);
		copy.bufferRowLength(0);
		copy.bufferImageHeight(0);
		copy.imageSubresource().aspectMask(VK_IMAGE_ASPECT_COLOR_BIT);
		copy.imageSubresource().mipLevel(0);
		copy.imageSubresource().baseArrayLayer(0).layerCount(1);
		copy.imageOffset().set(0, 0, 0);
		copy.imageExtent().set(width, height, 1);

		vkCmdCopyBufferToImage(cmd, buffer, image, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL, copy);

		copy.free();
	}

	public static void copyBufferToImage(long buffer, long image, int width, int height) {
		VkCommandBuffer cmd = beginSingleTimeCommandBuffer();
		copyBufferToImage(cmd, buffer, image, width, height);
		endSingleTimeCommandBuffer(cmd);
	}

	private static Map<TextureFormat, TextureFormat> formatLookupTable = new HashMap<>();
	private static Map<TextureFormat, TextureFormat[]> formatPriorities = new HashMap<>();

	static {
		formatPriorities.put(TextureFormat.R8, new TextureFormat[] { TextureFormat.R8, TextureFormat.RG8, TextureFormat.RGB8, TextureFormat.RGBA8 });
		formatPriorities.put(TextureFormat.RG8, new TextureFormat[] { TextureFormat.RG8, TextureFormat.RGB8, TextureFormat.RGBA8 });
		formatPriorities.put(TextureFormat.RGB8, new TextureFormat[] { TextureFormat.RGB8, TextureFormat.RGBA8 });
		formatPriorities.put(TextureFormat.RGBA8, new TextureFormat[] { TextureFormat.RGBA8 });

		formatPriorities.put(TextureFormat.R16F, new TextureFormat[] { TextureFormat.R16F, TextureFormat.RG16F, TextureFormat.RGB16F, TextureFormat.RGBA16F, TextureFormat.R32F, TextureFormat.RG32F,
				TextureFormat.RGB32F, TextureFormat.RGBA32F });
		formatPriorities.put(TextureFormat.RG16F,
				new TextureFormat[] { TextureFormat.RG16F, TextureFormat.RGB16F, TextureFormat.RGBA16F, TextureFormat.RG32F, TextureFormat.RGB32F, TextureFormat.RGBA32F });
		formatPriorities.put(TextureFormat.RGB16F, new TextureFormat[] { TextureFormat.RGB16F, TextureFormat.RGBA16F, TextureFormat.RGB32F, TextureFormat.RGBA32F });
		formatPriorities.put(TextureFormat.RGBA16F, new TextureFormat[] { TextureFormat.RGBA16F, TextureFormat.RGBA32F });

		formatPriorities.put(TextureFormat.R32F, new TextureFormat[] { TextureFormat.R32F, TextureFormat.RG32F, TextureFormat.RGB32F, TextureFormat.RGBA32F, TextureFormat.R16F, TextureFormat.RG16F,
				TextureFormat.RGB16F, TextureFormat.RGBA16F });
		formatPriorities.put(TextureFormat.RG32F,
				new TextureFormat[] { TextureFormat.RG32F, TextureFormat.RGB32F, TextureFormat.RGBA32F, TextureFormat.RG16F, TextureFormat.RGB16F, TextureFormat.RGBA16F });
		formatPriorities.put(TextureFormat.RGB32F, new TextureFormat[] { TextureFormat.RGB32F, TextureFormat.RGBA32F, TextureFormat.RGB16F, TextureFormat.RGBA16F });
		formatPriorities.put(TextureFormat.RGBA32F, new TextureFormat[] { TextureFormat.RGBA32F, TextureFormat.RGBA16F });
	}

	public static TextureFormat getSupportedFormat(TextureFormat format) {
		if (formatLookupTable.containsKey(format)) {
			return formatLookupTable.get(format);
		}
		TextureFormat[] list = formatPriorities.get(format);
		for (int i = 0; i < list.length; i++) {
			if (isFormatSupported(getFormat(list[i]))) {
				formatLookupTable.put(format, list[i]);
				return list[i];
			}
		}
		throw new RuntimeException();
	}

	public static int getFormat(TextureFormat format) {
		switch (format) {
		case R16F:
			return VK_FORMAT_R16_SFLOAT;
		case R8:
			return VK_FORMAT_R8_UNORM;
		case RG16F:
			return VK_FORMAT_R16G16_SFLOAT;
		case RGB16F:
			return VK_FORMAT_R16G16B16_SFLOAT;
		case RGB32F:
			return VK_FORMAT_R32G32B32_SFLOAT;
		case RGB8:
			return VK_FORMAT_R8G8B8_UNORM;
		case RGBA16F:
			return VK_FORMAT_R16G16B16A16_SFLOAT;
		case RGBA8:
			return VK_FORMAT_R8G8B8A8_UNORM;
		case RGBA32F:
			return VK_FORMAT_R32G32B32A32_SFLOAT;
		case R32F:
			return VK_FORMAT_R32_SFLOAT;
		case RG32F:
			return VK_FORMAT_R32G32_SFLOAT;
		case RG8:
			return VK_FORMAT_R8G8_UNORM;
		}
		return 0;
	}

	public static int findSupportedFormat(int tiling, int features, int... candidates) {
		VkFormatProperties props = VkFormatProperties.calloc();
		int result = 0;
		VkPhysicalDevice physicalDevice = Graphics.getVkContext().getPhysicalDevice();
		for (int i = 0; i < candidates.length; i++) {
			vkGetPhysicalDeviceFormatProperties(physicalDevice, candidates[i], props);
			if (tiling == VK_IMAGE_TILING_LINEAR) {
				if ((props.linearTilingFeatures() & features) == features) {
					result = candidates[i];
					break;
				}
			} else if (tiling == VK_IMAGE_TILING_OPTIMAL) {
				if ((props.optimalTilingFeatures() & features) == features) {
					result = candidates[i];
					break;
				}
			}
		}
		props.free();
		if (result == 0) {
			System.out.println("No supported format found!");
		}
		return result;
	}

	private static boolean isFormatSupported(int format) {
		VkImageFormatProperties properties = VkImageFormatProperties.calloc();
		int result = vkGetPhysicalDeviceImageFormatProperties(Graphics.getVkContext().getPhysicalDevice(), format, VK_IMAGE_TYPE_2D, VK_IMAGE_TILING_OPTIMAL, VK_IMAGE_USAGE_SAMPLED_BIT, 0,
				properties);
		properties.free();
		return result != VK_ERROR_FORMAT_NOT_SUPPORTED;
	}

	public static int getDescriptorType(Descriptor.Type type) {
		switch (type) {
		case TEXTURE_2D:
			return VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER;
		case UNIFORM_BUFFER:
			return VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER;
		case STORAGE_BUFFER:
			return VK_DESCRIPTOR_TYPE_STORAGE_BUFFER;
		case TEXTURE_1D:
			return VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER;
		case TEXTURE_CUBE:
			return VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER;
		case TEXTURE_3D:
			return VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER;
		case IMAGE_2D:
			return VK_DESCRIPTOR_TYPE_STORAGE_IMAGE;
		case IMAGE_3D:
			return VK_DESCRIPTOR_TYPE_STORAGE_IMAGE;
		}
		return 0;
	}

	private static int depthBufferFormat = -1;

	public static int getDepthBufferFormat() {
		if (depthBufferFormat == -1) {
			depthBufferFormat = findSupportedFormat(VK_IMAGE_TILING_OPTIMAL, VK_FORMAT_FEATURE_DEPTH_STENCIL_ATTACHMENT_BIT, VK_FORMAT_D32_SFLOAT, VK_FORMAT_D32_SFLOAT_S8_UINT,
					VK_FORMAT_D24_UNORM_S8_UINT, VK_FORMAT_D16_UNORM, VK_FORMAT_D16_UNORM_S8_UINT);
		}
		return depthBufferFormat;
	}
}
