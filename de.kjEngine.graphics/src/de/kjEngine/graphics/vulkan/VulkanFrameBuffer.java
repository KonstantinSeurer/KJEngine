/**
 * 
 */
package de.kjEngine.graphics.vulkan;

import static de.kjEngine.graphics.vulkan.VulkanUtil.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.vulkan.VK10.*;

import java.nio.LongBuffer;

import org.lwjgl.vulkan.VkClearValue;
import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkFramebufferCreateInfo;
import org.lwjgl.vulkan.VkImageViewCreateInfo;
import org.lwjgl.vulkan.VkSamplerCreateInfo;

import de.kjEngine.graphics.FrameBuffer;
import de.kjEngine.graphics.Graphics;
import de.kjEngine.graphics.SamplingMode;
import de.kjEngine.graphics.Texture2D;
import de.kjEngine.graphics.TextureFormat;
import de.kjEngine.graphics.WrappingMode;
import de.kjEngine.graphics.shader.InterfaceBlockSource;
import de.kjEngine.graphics.vulkan.VulkanUtil.CreateImageResult;

/**
 * @author konst
 *
 */
public class VulkanFrameBuffer extends FrameBuffer {

	private long framebuffer;
	private VulkanTexture2D[] colorAttachments;
	private VulkanTexture2D depthAttachment;
	private VkClearValue clearColor, clearDepth;
	private long renderpass;
	private TextureFormat[] colorAttachmentFormats;
	private boolean hasDepthAttachment;

	public VulkanFrameBuffer(int width, int height, InterfaceBlockSource source) {
		super(width, height, source);
		
		if (source.getSettings().containsKey("depth")) {
			hasDepthAttachment = source.getBoolean("depth");
		}

		colorAttachmentFormats = new TextureFormat[source.getVariables().size()];
		for (int i = 0; i < colorAttachmentFormats.length; i++) {
			colorAttachmentFormats[i] = source.getVariables().get(i).getFormat();
		}

		renderpass = RenderpassCache.getRenderpass(hasDepthAttachment, colorAttachmentFormats);

		colorAttachments = new VulkanTexture2D[colorAttachmentFormats.length];

		clearColor = VkClearValue.calloc();
		clearColor.color().float32(0, 0f);
		clearColor.color().float32(1, 0f);
		clearColor.color().float32(2, 0f);
		clearColor.color().float32(3, 1f);

		clearDepth = VkClearValue.calloc();
		clearDepth.depthStencil().depth(1f);
		clearDepth.depthStencil().stencil(0);
		
		createRenderPass();
		createColorImages();
		createDepthImage();
		createFramebuffer();
	}

	private void createDepthImage() {
		if (!hasDepthAttachment) {
			return;
		}

		int format = getDepthBufferFormat();

		CreateImageResult cir = createImage(Graphics.getVkContext().getDevice(), width, height, 1, format, VK_IMAGE_TILING_OPTIMAL,
				VK_IMAGE_USAGE_TRANSFER_SRC_BIT | VK_IMAGE_USAGE_TRANSFER_DST_BIT | VK_IMAGE_USAGE_DEPTH_STENCIL_ATTACHMENT_BIT | VK_IMAGE_USAGE_SAMPLED_BIT | VK_IMAGE_USAGE_STORAGE_BIT,
				VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT);

		VkImageViewCreateInfo viewCreateInfo = VkImageViewCreateInfo.calloc().sType(VK_STRUCTURE_TYPE_IMAGE_VIEW_CREATE_INFO).pNext(NULL).flags(0);
		viewCreateInfo.image(cir.image);
		viewCreateInfo.viewType(VK_IMAGE_VIEW_TYPE_2D);
		viewCreateInfo.format(format);
		viewCreateInfo.components().r(VK_COMPONENT_SWIZZLE_IDENTITY);
		viewCreateInfo.components().g(VK_COMPONENT_SWIZZLE_IDENTITY);
		viewCreateInfo.components().b(VK_COMPONENT_SWIZZLE_IDENTITY);
		viewCreateInfo.components().a(VK_COMPONENT_SWIZZLE_IDENTITY);
		viewCreateInfo.subresourceRange().aspectMask(VK_IMAGE_ASPECT_DEPTH_BIT);
		viewCreateInfo.subresourceRange().levelCount(1).baseMipLevel(0);
		viewCreateInfo.subresourceRange().layerCount(1).baseArrayLayer(0);

		LongBuffer pView = memAllocLong(1);
		err(vkCreateImageView(Graphics.getVkContext().getDevice(), viewCreateInfo, null, pView));

		VkSamplerCreateInfo samplerCreateInfo = VkSamplerCreateInfo.calloc().sType(VK_STRUCTURE_TYPE_SAMPLER_CREATE_INFO).pNext(NULL).flags(0);
		samplerCreateInfo.magFilter(VK_FILTER_LINEAR);
		samplerCreateInfo.minFilter(VK_FILTER_LINEAR);
		samplerCreateInfo.addressModeU(VK_SAMPLER_ADDRESS_MODE_REPEAT);
		samplerCreateInfo.addressModeV(VK_SAMPLER_ADDRESS_MODE_REPEAT);
		samplerCreateInfo.addressModeW(VK_SAMPLER_ADDRESS_MODE_REPEAT);
		samplerCreateInfo.anisotropyEnable(false);
		samplerCreateInfo.borderColor(VK_BORDER_COLOR_INT_OPAQUE_BLACK);
		samplerCreateInfo.unnormalizedCoordinates(false);
		samplerCreateInfo.compareEnable(false);
		samplerCreateInfo.compareOp(VK_COMPARE_OP_ALWAYS);
		samplerCreateInfo.mipmapMode(VK_SAMPLER_MIPMAP_MODE_LINEAR);
		samplerCreateInfo.mipLodBias(0f).minLod(0f).maxLod(0f);

		LongBuffer pSampler = memAllocLong(1);
		err(vkCreateSampler(Graphics.getVkContext().getDevice(), samplerCreateInfo, null, pSampler));

		depthAttachment = new VulkanTexture2D(width, height, 1, null, SamplingMode.NEAREST, WrappingMode.CLAMP, cir.image, cir.memory, new long[] { pView.get(0) }, pView.get(0), pSampler.get(0),
				format, VK_IMAGE_ASPECT_DEPTH_BIT) {

			@Override
			public void setLayout(VkCommandBuffer cb, int layout) {
				if (layout == VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL) {
					layout = VK_IMAGE_LAYOUT_DEPTH_STENCIL_READ_ONLY_OPTIMAL;
				}
				super.setLayout(cb, layout);
			}
		};

		viewCreateInfo.free();
		memFree(pView);

		memFree(pSampler);
		samplerCreateInfo.free();
	}

	private void createColorImages() {
		for (int i = 0; i < colorAttachments.length; i++) {
			TextureFormat imageFormat = getSupportedFormat(colorAttachmentFormats[i]);
			int vkFormat = getFormat(imageFormat);

			CreateImageResult cir = createImage(Graphics.getVkContext().getDevice(), width, height, 1, vkFormat, VK_IMAGE_TILING_OPTIMAL,
					VK_IMAGE_USAGE_TRANSFER_SRC_BIT | VK_IMAGE_USAGE_TRANSFER_DST_BIT | VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT | VK_IMAGE_USAGE_SAMPLED_BIT | VK_IMAGE_USAGE_STORAGE_BIT,
					VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT);

			VkImageViewCreateInfo viewCreateInfo = VkImageViewCreateInfo.calloc().sType(VK_STRUCTURE_TYPE_IMAGE_VIEW_CREATE_INFO).pNext(NULL).flags(0);
			viewCreateInfo.image(cir.image);
			viewCreateInfo.viewType(VK_IMAGE_VIEW_TYPE_2D);
			viewCreateInfo.format(vkFormat);
			viewCreateInfo.components().r(VK_COMPONENT_SWIZZLE_IDENTITY);
			viewCreateInfo.components().g(VK_COMPONENT_SWIZZLE_IDENTITY);
			viewCreateInfo.components().b(VK_COMPONENT_SWIZZLE_IDENTITY);
			viewCreateInfo.components().a(VK_COMPONENT_SWIZZLE_IDENTITY);
			viewCreateInfo.subresourceRange().aspectMask(VK_IMAGE_ASPECT_COLOR_BIT);
			viewCreateInfo.subresourceRange().levelCount(1).baseMipLevel(0);
			viewCreateInfo.subresourceRange().layerCount(1).baseArrayLayer(0);

			LongBuffer pView = memAllocLong(1);
			err(vkCreateImageView(Graphics.getVkContext().getDevice(), viewCreateInfo, null, pView));
			long view = pView.get(0);

			memFree(pView);
			viewCreateInfo.free();

			VkSamplerCreateInfo samplerCreateInfo = VkSamplerCreateInfo.calloc().sType(VK_STRUCTURE_TYPE_SAMPLER_CREATE_INFO).pNext(NULL).flags(0);
			samplerCreateInfo.magFilter(VK_FILTER_LINEAR);
			samplerCreateInfo.minFilter(VK_FILTER_LINEAR);
			samplerCreateInfo.addressModeU(VK_SAMPLER_ADDRESS_MODE_REPEAT);
			samplerCreateInfo.addressModeV(VK_SAMPLER_ADDRESS_MODE_REPEAT);
			samplerCreateInfo.addressModeW(VK_SAMPLER_ADDRESS_MODE_REPEAT);
			samplerCreateInfo.anisotropyEnable(false);
			samplerCreateInfo.borderColor(VK_BORDER_COLOR_INT_OPAQUE_BLACK);
			samplerCreateInfo.unnormalizedCoordinates(false);
			samplerCreateInfo.compareEnable(false);
			samplerCreateInfo.compareOp(VK_COMPARE_OP_ALWAYS);
			samplerCreateInfo.mipmapMode(VK_SAMPLER_MIPMAP_MODE_LINEAR);
			samplerCreateInfo.mipLodBias(0f).minLod(0f).maxLod(0f);

			LongBuffer pSampler = memAllocLong(1);
			err(vkCreateSampler(Graphics.getVkContext().getDevice(), samplerCreateInfo, null, pSampler));
			long sampler = pSampler.get(0);

			memFree(pSampler);
			samplerCreateInfo.free();

			colorAttachments[i] = new VulkanTexture2D(width, height, 1, imageFormat, SamplingMode.LINEAR, WrappingMode.CLAMP, cir.image, cir.memory, new long[] { view }, view, sampler, vkFormat,
					VK_IMAGE_ASPECT_COLOR_BIT);
		}
	}

	private void createFramebuffer() {
		VkFramebufferCreateInfo framebufferCreateInfo = VkFramebufferCreateInfo.calloc().sType(VK_STRUCTURE_TYPE_FRAMEBUFFER_CREATE_INFO).pNext(NULL).flags(0);
		framebufferCreateInfo.renderPass(renderpass);

		int attachmentCount = colorAttachments.length;
		if (hasDepthAttachment) {
			attachmentCount++;
		}
		LongBuffer pAttachments = memAllocLong(attachmentCount);
		for (int i = 0; i < colorAttachments.length; i++) {
			pAttachments.put(i, colorAttachments[i].imageViews[0]);
		}
		if (hasDepthAttachment) {
			pAttachments.put(colorAttachments.length, depthAttachment.imageViews[0]);
		}
		framebufferCreateInfo.pAttachments(pAttachments);
		framebufferCreateInfo.width(width).height(height);
		framebufferCreateInfo.layers(1);

		LongBuffer pFramebuffer = memAllocLong(1);
		err(vkCreateFramebuffer(Graphics.getVkContext().getDevice(), framebufferCreateInfo, null, pFramebuffer));
		framebuffer = pFramebuffer.get(0);

		memFree(pFramebuffer);
		memFree(pAttachments);
		framebufferCreateInfo.free();
	}

	private void createRenderPass() {

	}

	@Override
	public void dispose() {
		vkDestroyFramebuffer(Graphics.getVkContext().getDevice(), framebuffer, null);
		for (int i = 0; i < colorAttachments.length; i++) {
			colorAttachments[i].dispose();
		}
		if (hasDepthAttachment) {
			depthAttachment.dispose();
		}
		clearColor.free();
		clearDepth.free();
	}

	@Override
	public Texture2D getColorAttachment(String name) {
		for (int i = 0; i < colorAttachments.length; i++) {
			if (name.equals(source.getVariables().get(i).getName())) {
				return colorAttachments[i];
			}
		}
		return null;
	}

	@Override
	public VulkanTexture2D getDepthAttachment() {
		return depthAttachment;
	}

	@Override
	public byte[] getPixels(String name) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @return the framebuffer
	 */
	public long getFramebuffer() {
		return framebuffer;
	}

	/**
	 * @return the renderpass
	 */
	public long getRenderpass() {
		return renderpass;
	}

	/**
	 * @return the colorAttachments
	 */
	public VulkanTexture2D[] getColorAttachments() {
		return colorAttachments;
	}

	/**
	 * @return the clearDepth
	 */
	public VkClearValue getClearDepth() {
		return clearDepth;
	}

	/**
	 * @return the colorAttachmentFormats
	 */
	public TextureFormat[] getColorAttachmentFormats() {
		return colorAttachmentFormats;
	}

	/**
	 * @return the hasDepthAttachment
	 */
	public boolean isHasDepthAttachment() {
		return hasDepthAttachment;
	}
}
