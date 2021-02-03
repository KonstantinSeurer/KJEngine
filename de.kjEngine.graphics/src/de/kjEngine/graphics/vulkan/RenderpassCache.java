/**
 * 
 */
package de.kjEngine.graphics.vulkan;

import static de.kjEngine.graphics.vulkan.VulkanUtil.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.vulkan.VK10.*;

import java.nio.LongBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.vulkan.VkAttachmentDescription;
import org.lwjgl.vulkan.VkAttachmentReference;
import org.lwjgl.vulkan.VkRenderPassCreateInfo;
import org.lwjgl.vulkan.VkSubpassDescription;

import de.kjEngine.graphics.Graphics;
import de.kjEngine.graphics.TextureFormat;

/**
 * @author konst
 *
 */
public class RenderpassCache {
	
	private static class FramebufferLayout {
		TextureFormat[] colorAttachments;
		boolean hasDepthAttachment;

		public FramebufferLayout(TextureFormat[] colorAttachments, boolean hasDepthAttachment) {
			this.colorAttachments = colorAttachments;
			this.hasDepthAttachment = hasDepthAttachment;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + Arrays.hashCode(colorAttachments);
			result = prime * result + (hasDepthAttachment ? 1231 : 1237);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			FramebufferLayout other = (FramebufferLayout) obj;
			if (!Arrays.equals(colorAttachments, other.colorAttachments))
				return false;
			if (hasDepthAttachment != other.hasDepthAttachment)
				return false;
			return true;
		}
	}

	private static Map<FramebufferLayout, Long> renderpasses = new HashMap<>();
	
	public static long getRenderpass(boolean hasDepthAttachment, TextureFormat[] colorAttachments) {
		FramebufferLayout layout = new FramebufferLayout(colorAttachments, hasDepthAttachment);
		
		if (!renderpasses.containsKey(layout)) {
			int attachmentCount = layout.colorAttachments.length;
			VkAttachmentReference depthAttachmentRef = null;
			if (layout.hasDepthAttachment) {
				depthAttachmentRef = VkAttachmentReference.calloc();
				depthAttachmentRef.attachment(layout.colorAttachments.length);
				depthAttachmentRef.layout(VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL);
				attachmentCount++;
			}

			VkAttachmentDescription.Buffer attachments = VkAttachmentDescription.calloc(attachmentCount);

			if (layout.hasDepthAttachment) {
				VkAttachmentDescription depthAttachmentDesc = attachments.get(layout.colorAttachments.length);
				depthAttachmentDesc.format(getDepthBufferFormat());
				depthAttachmentDesc.samples(VK_SAMPLE_COUNT_1_BIT);
				depthAttachmentDesc.loadOp(VK_ATTACHMENT_LOAD_OP_DONT_CARE);
				depthAttachmentDesc.storeOp(VK_ATTACHMENT_STORE_OP_STORE);
				depthAttachmentDesc.stencilLoadOp(VK_ATTACHMENT_LOAD_OP_DONT_CARE);
				depthAttachmentDesc.stencilStoreOp(VK_ATTACHMENT_STORE_OP_DONT_CARE);
				depthAttachmentDesc.initialLayout(VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL);
				depthAttachmentDesc.finalLayout(VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL);
				depthAttachmentDesc.flags(0);
			}

			VkAttachmentReference.Buffer colorAttachmentReferences = null;
			if (layout.colorAttachments.length > 0) {
				colorAttachmentReferences = VkAttachmentReference.calloc(layout.colorAttachments.length);
				for (int i = 0; i < layout.colorAttachments.length; i++) {
					VkAttachmentDescription colorAttachmentDesc = attachments.get(i);
					colorAttachmentDesc.format(VulkanUtil.getFormat(getSupportedFormat(layout.colorAttachments[i])));
					colorAttachmentDesc.samples(VK_SAMPLE_COUNT_1_BIT);
					colorAttachmentDesc.loadOp(VK_ATTACHMENT_LOAD_OP_DONT_CARE);
					colorAttachmentDesc.storeOp(VK_ATTACHMENT_STORE_OP_STORE);
					colorAttachmentDesc.stencilLoadOp(VK_ATTACHMENT_LOAD_OP_DONT_CARE);
					colorAttachmentDesc.stencilStoreOp(VK_ATTACHMENT_STORE_OP_DONT_CARE);
					colorAttachmentDesc.initialLayout(VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL);
					colorAttachmentDesc.finalLayout(VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL);
					colorAttachmentDesc.flags(0);

					VkAttachmentReference colorAttachmentReference = colorAttachmentReferences.get(i);
					colorAttachmentReference.attachment(i);
					colorAttachmentReference.layout(VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL);
				}
			}

			VkSubpassDescription subpass = VkSubpassDescription.calloc().flags(0);
			subpass.pipelineBindPoint(VK_PIPELINE_BIND_POINT_GRAPHICS);
			subpass.colorAttachmentCount(layout.colorAttachments.length);
			subpass.pColorAttachments(colorAttachmentReferences);
			subpass.pInputAttachments(null);
			subpass.pPreserveAttachments(null);
			subpass.pDepthStencilAttachment(depthAttachmentRef);

			VkRenderPassCreateInfo renderPassCreateInfo = VkRenderPassCreateInfo.calloc().sType(VK_STRUCTURE_TYPE_RENDER_PASS_CREATE_INFO).pNext(NULL).flags(0);
			renderPassCreateInfo.pAttachments(attachments);

			VkSubpassDescription.Buffer subpasses = VkSubpassDescription.calloc(1).put(0, subpass);
			renderPassCreateInfo.pSubpasses(subpasses);
			renderPassCreateInfo.pDependencies(null);

			LongBuffer pRenderpass = memAllocLong(1);
			err(vkCreateRenderPass(Graphics.getVkContext().getDevice(), renderPassCreateInfo, null, pRenderpass));
			renderpasses.put(layout, pRenderpass.get(0));

			subpass.free();
			attachments.free();
			memFree(pRenderpass);
			subpasses.free();

			if (layout.hasDepthAttachment) {
				depthAttachmentRef.free();
			}

			if (layout.colorAttachments.length > 0) {
				colorAttachmentReferences.free();
			}
		}
		return renderpasses.get(layout);
	}
}
