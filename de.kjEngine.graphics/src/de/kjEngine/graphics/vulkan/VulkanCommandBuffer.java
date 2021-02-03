/**
 * 
 */
package de.kjEngine.graphics.vulkan;

import static de.kjEngine.graphics.vulkan.VulkanUtil.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.vulkan.VK10.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.PointerBuffer;
import org.lwjgl.vulkan.VkClearAttachment;
import org.lwjgl.vulkan.VkClearRect;
import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkCommandBufferAllocateInfo;
import org.lwjgl.vulkan.VkCommandBufferBeginInfo;
import org.lwjgl.vulkan.VkCommandPoolCreateInfo;
import org.lwjgl.vulkan.VkImageBlit;
import org.lwjgl.vulkan.VkMemoryBarrier;
import org.lwjgl.vulkan.VkRect2D;
import org.lwjgl.vulkan.VkRenderPassBeginInfo;
import org.lwjgl.vulkan.VkSubmitInfo;
import org.lwjgl.vulkan.VkViewport;

import de.kjEngine.graphics.CommandBuffer;
import de.kjEngine.graphics.Descriptor;
import de.kjEngine.graphics.DescriptorSet;
import de.kjEngine.graphics.FrameBuffer;
import de.kjEngine.graphics.Graphics;
import de.kjEngine.graphics.Pipeline;
import de.kjEngine.graphics.Query;
import de.kjEngine.graphics.Texture2D;
import de.kjEngine.graphics.VertexArray;
import de.kjEngine.graphics.shader.PipelineSource;
import de.kjEngine.graphics.vulkan.VulkanDescriptorSet.InternDescriptor;

/**
 * @author konst
 *
 */
public class VulkanCommandBuffer extends CommandBuffer {

	private List<Runnable> commands = new ArrayList<>();
	private boolean change = false;

	private boolean dynamic;

	private long commandpool;
	private VkCommandBuffer commandbuffer;

	public VulkanCommandBuffer(boolean dynamic) {
		this.dynamic = dynamic;

		VkCommandPoolCreateInfo commandPoolCreateInfo = VkCommandPoolCreateInfo.calloc().sType(VK_STRUCTURE_TYPE_COMMAND_POOL_CREATE_INFO).pNext(NULL);
		commandPoolCreateInfo.queueFamilyIndex(Graphics.getVkContext().getGraphicsQueueFamilyIndex());
		int cpflags = VK_COMMAND_POOL_CREATE_RESET_COMMAND_BUFFER_BIT;
		if (dynamic) {
			cpflags |= VK_COMMAND_POOL_CREATE_TRANSIENT_BIT;
		}
		commandPoolCreateInfo.flags(cpflags);

		LongBuffer pCommandpool = memAllocLong(1);
		err(vkCreateCommandPool(Graphics.getVkContext().getDevice(), commandPoolCreateInfo, null, pCommandpool));
		commandpool = pCommandpool.get(0);

		memFree(pCommandpool);
		commandPoolCreateInfo.free();

		VkCommandBufferAllocateInfo commandBufferAllocateInfo = VkCommandBufferAllocateInfo.calloc().sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO).pNext(NULL);
		commandBufferAllocateInfo.commandPool(commandpool);
		commandBufferAllocateInfo.level(VK_COMMAND_BUFFER_LEVEL_PRIMARY);
		commandBufferAllocateInfo.commandBufferCount(1);

		PointerBuffer pCommandbuffer = memAllocPointer(1);
		err(vkAllocateCommandBuffers(Graphics.getVkContext().getDevice(), commandBufferAllocateInfo, pCommandbuffer));
		commandbuffer = new VkCommandBuffer(pCommandbuffer.get(0), Graphics.getVkContext().getDevice());

		memFree(pCommandbuffer);
		commandBufferAllocateInfo.free();
	}

	@Override
	public void dispose() {
		vkDestroyCommandPool(Graphics.getVkContext().getDevice(), commandpool, null);
	}

	public VkCommandBuffer getHandle() {
		return commandbuffer;
	}

	@Override
	public void clear() {
		commands.clear();
		change = true;
	}

	@Override
	public void submit() {
		if (change) {
			VkCommandBufferBeginInfo beginInfo = VkCommandBufferBeginInfo.calloc().sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO).pNext(NULL);
			int flags = VK_COMMAND_BUFFER_USAGE_SIMULTANEOUS_USE_BIT;
			if (dynamic) {
				flags |= VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT;
			}
			beginInfo.flags(flags);
			beginInfo.pInheritanceInfo(null);

			err(vkBeginCommandBuffer(commandbuffer, beginInfo));

			for (Runnable c : commands) {
				c.run();
			}

			flushMemoryBarriers();

			err(vkEndCommandBuffer(commandbuffer));

			beginInfo.free();

			change = false;
		}

		VkSubmitInfo submitInfo = VkSubmitInfo.calloc().sType(VK_STRUCTURE_TYPE_SUBMIT_INFO).pNext(NULL);

		VulkanSemaphoreChain semaphoreChain = Graphics.getVkContext().getSemaphoreChain();

		long waitSemaphore = semaphoreChain.peek();
		LongBuffer pWait = null;
		if (waitSemaphore != VK_NULL_HANDLE) {
			pWait = memAllocLong(1).put(0, waitSemaphore);
			submitInfo.pWaitSemaphores(pWait);
		} else {
			submitInfo.pWaitSemaphores(null);
		}

		IntBuffer pWaitStages = memAllocInt(1).put(0, VK_PIPELINE_STAGE_ALL_GRAPHICS_BIT | VK_PIPELINE_STAGE_COMPUTE_SHADER_BIT);
		submitInfo.pWaitDstStageMask(pWaitStages);

		PointerBuffer commandbuffers = memAllocPointer(1).put(0, commandbuffer.address());
		submitInfo.pCommandBuffers(commandbuffers);

		long signal = semaphoreChain.push();
		LongBuffer pSignal = memAllocLong(1).put(0, signal);
		submitInfo.pSignalSemaphores(pSignal);

		err(vkQueueSubmit(Graphics.getVkContext().getGraphicsQueue(), submitInfo, VK_NULL_HANDLE));

		memFree(pSignal);
		memFree(commandbuffers);
		memFree(pWaitStages);
		if (pWait != null) {
			memFree(pWait);
		}
		submitInfo.free();
	}

	@Override
	public void bindDescriptorSet(DescriptorSet set, String name) {
		Pipeline pipeline = state.pipeline;

		if (set == null)
			throw new NullPointerException();

		commands.add(new Runnable() {

			@Override
			public void run() {
				VulkanDescriptorSet s = (VulkanDescriptorSet) set;
				for (int i = 0; i < s.descriptors.length; i++) {
					InternDescriptor desc = s.descriptors[i];
					if (desc == null) {
						continue;
					}
					for (int j = 0; j < desc.descriptors.length; j++) {
						Descriptor d = desc.descriptors[j];
						if (d != null) {
							switch (d.getType()) {
							case IMAGE_2D:
								((VulkanImage2D) d).texture.setLayout(commandbuffer, VK_IMAGE_LAYOUT_GENERAL);
								break;
							case IMAGE_3D:
								break;
							case STORAGE_BUFFER:
								break;
							case TEXTURE_1D:
								break;
							case TEXTURE_2D:
								((VulkanTexture2D) d).setLayout(commandbuffer, VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL);
								break;
							case TEXTURE_3D:
								break;
							case TEXTURE_CUBE:
								break;
							case UNIFORM_BUFFER:
								break;
							case ACCELERATION_STRUCTURE:
								break;
							}
						}
					}
				}

				LongBuffer pDescriptorSets = memAllocLong(1).put(0, s.set);
				int bindPoint = 0;
				switch (pipeline.getType()) {
				case COMPUTE:
					bindPoint = VK_PIPELINE_BIND_POINT_COMPUTE;
					break;
				case GRAPHICS:
					bindPoint = VK_PIPELINE_BIND_POINT_GRAPHICS;
					break;
				case RAYTRACING:
					break;
				}

				flushMemoryBarriers();

				PipelineSource pipelineSource = pipeline.getSource();

				for (int i = 0; i < pipelineSource.getDescriptorSets().size(); i++) {
					if (pipelineSource.getDescriptorSets().get(i).getName().equals(name)) {
						vkCmdBindDescriptorSets(commandbuffer, bindPoint, ((VulkanPipeline) pipeline).getLayout(), i, pDescriptorSets, null);
						break;
					}
				}

				memFree(pDescriptorSets);
			}
		});
		change = true;
	}

	@Override
	public void bindFrameBuffer(FrameBuffer framebuffer) {
		state.framebuffer = framebuffer;

		commands.add(new Runnable() {

			@Override
			public void run() {
				VkRenderPassBeginInfo renderPassBeginInfo = VkRenderPassBeginInfo.calloc().sType(VK_STRUCTURE_TYPE_RENDER_PASS_BEGIN_INFO).pNext(NULL);
				renderPassBeginInfo.renderArea().offset().x(0);
				renderPassBeginInfo.renderArea().offset().y(0);
				renderPassBeginInfo.renderArea().extent().width(framebuffer.getWidth());
				renderPassBeginInfo.renderArea().extent().height(framebuffer.getHeight());
				renderPassBeginInfo.pClearValues(null);

				if (framebuffer == Graphics.getContext().getScreenBuffer()) {
					VulkanContext context = Graphics.getVkContext();
					renderPassBeginInfo.renderPass(context.getRenderpass());
					renderPassBeginInfo.framebuffer(context.getCurrentFramebuffer());
				} else {
					VulkanFrameBuffer frameBuffer = (VulkanFrameBuffer) framebuffer;
					renderPassBeginInfo.renderPass(frameBuffer.getRenderpass());
					renderPassBeginInfo.framebuffer(frameBuffer.getFramebuffer());

					for (int i = 0; i < frameBuffer.getColorAttachments().length; i++) {
						frameBuffer.getColorAttachments()[i].setLayout(commandbuffer, VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL);
					}

					if (frameBuffer.getDepthAttachment() != null) {
						frameBuffer.getDepthAttachment().setLayout(commandbuffer, VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL);
					}
				}

				flushMemoryBarriers();
				vkCmdBeginRenderPass(commandbuffer, renderPassBeginInfo, VK_SUBPASS_CONTENTS_INLINE);

				renderPassBeginInfo.free();
			}
		});
		change = true;
	}

	@Override
	public void unbindFrameBuffer(FrameBuffer framebuffer) {
		state.framebuffer = null;

		commands.add(new Runnable() {

			@Override
			public void run() {
				flushMemoryBarriers();
				vkCmdEndRenderPass(commandbuffer);
			}
		});
		change = true;
	}

	@Override
	public void clearFrameBuffer(FrameBuffer framebuffer) {
		commands.add(new Runnable() {

			@Override
			public void run() {
				VkClearRect.Buffer pRects = VkClearRect.calloc(1);
				pRects.get(0).baseArrayLayer(0);
				pRects.get(0).layerCount(1);
				pRects.get(0).rect().extent().width(framebuffer.getWidth());
				pRects.get(0).rect().extent().height(framebuffer.getHeight());
				pRects.get(0).rect().offset().x(0);
				pRects.get(0).rect().offset().y(0);

				boolean depthAttachment = false;
				if (framebuffer.getSource().getSettings().containsKey("depth")) {
					depthAttachment = framebuffer.getSource().getBoolean("depth");
				}

				int attachmentCount = framebuffer.getSource().getVariables().size();
				if (depthAttachment) {
					attachmentCount++;
				}
				VkClearAttachment.Buffer pAttachments = VkClearAttachment.calloc(attachmentCount);

				for (int i = 0; i < framebuffer.getSource().getVariables().size(); i++) {
					pAttachments.get(i).aspectMask(VK_IMAGE_ASPECT_COLOR_BIT);
					pAttachments.get(i).colorAttachment(i);
					pAttachments.get(i).clearValue().color().float32(0, framebuffer.getClearColor().x);
					pAttachments.get(i).clearValue().color().float32(1, framebuffer.getClearColor().y);
					pAttachments.get(i).clearValue().color().float32(2, framebuffer.getClearColor().z);
					pAttachments.get(i).clearValue().color().float32(3, framebuffer.getClearColor().w);
				}

				if (depthAttachment) {
					pAttachments.get(framebuffer.getSource().getVariables().size()).aspectMask(VK_IMAGE_ASPECT_DEPTH_BIT);
					pAttachments.get(framebuffer.getSource().getVariables().size()).clearValue().depthStencil().set(1f, 0);
				}

				flushMemoryBarriers();
				vkCmdClearAttachments(commandbuffer, pAttachments, pRects);

				pAttachments.free();
				pRects.free();
			}
		});
		change = true;
	}

	@Override
	public void bindQuery(Query query) {
		commands.add(new Runnable() {

			@Override
			public void run() {
			}
		});
		change = true;
	}

	@Override
	public void unbindQuery(Query query) {
		commands.add(new Runnable() {

			@Override
			public void run() {
			}
		});
		change = true;
	}

	@Override
	public void bindPipeline(Pipeline pipeline) {
		state.pipeline = pipeline;
		FrameBuffer framebuffer = state.framebuffer;

		commands.add(new Runnable() {

			@Override
			public void run() {
				flushMemoryBarriers();
				vkCmdBindPipeline(commandbuffer, ((VulkanPipeline) pipeline).getBindPoint(), ((VulkanPipeline) pipeline).getPipeline());

				if (pipeline.getType() == Pipeline.Type.GRAPHICS) {
					VkViewport.Buffer pViewports = VkViewport.calloc(1);
					pViewports.get(0).set(0f, 0f, framebuffer.getWidth(), framebuffer.getHeight(), 0f, 1f);
					vkCmdSetViewport(commandbuffer, 0, pViewports);
					pViewports.free();

					VkRect2D.Buffer pScissors = VkRect2D.calloc(1);
					pScissors.get(0).offset().set(0, 0);
					pScissors.get(0).extent().set(framebuffer.getWidth(), framebuffer.getHeight());
					vkCmdSetScissor(commandbuffer, 0, pScissors);
				}
			}
		});
		change = true;
	}

	@Override
	public void compute(int width, int height, int length) {
		VulkanPipeline pipeline = (VulkanPipeline) state.pipeline;
		VulkanUniforms uniforms = pipeline.getUniforms();
		final ByteBuffer uniformData = uniforms.pollChanged() ? uniforms.getCopy() : null;

		commands.add(new Runnable() {

			@Override
			public void run() {
				flushMemoryBarriers();
				if (uniformData != null) {
					vkCmdPushConstants(commandbuffer, pipeline.getLayout(), VK_SHADER_STAGE_COMPUTE_BIT, 0, uniformData);
					uniforms.releaseCopy(uniformData);
				}
				vkCmdDispatch(commandbuffer, width, height, length);
			}
		});
		change = true;
	}

	@Override
	public void trace(int width, int height) {
		VulkanPipeline pipeline = (VulkanPipeline) state.pipeline;
		VulkanUniforms uniforms = pipeline.getUniforms();
		final ByteBuffer uniformData = uniforms.pollChanged() ? uniforms.getCopy() : null;
		commands.add(new Runnable() {

			@Override
			public void run() {
				flushMemoryBarriers();
				if (uniformData != null) {
					vkCmdPushConstants(commandbuffer, pipeline.getLayout(), VK_SHADER_STAGE_COMPUTE_BIT, 0, uniformData);
					uniforms.releaseCopy(uniformData);
				}
				if (Graphics.getVkContext().supportsRayTracing()) {

				} else {
					vkCmdDispatch(commandbuffer, width, height, 1);
				}
			}
		});
		change = true;
	}

	@Override
	public void draw(int vertexCount, int instanceCount, int firstVertex, int firstInstance) {
		VulkanGraphicsPipeline pipeline = (VulkanGraphicsPipeline) state.pipeline;
		VulkanUniforms uniforms = pipeline.getUniforms();
		final ByteBuffer uniformData = uniforms.pollChanged() ? uniforms.getCopy() : null;

		commands.add(new Runnable() {

			@Override
			public void run() {
				flushMemoryBarriers();
				if (uniformData != null) {
					vkCmdPushConstants(commandbuffer, pipeline.layout, VK_SHADER_STAGE_ALL_GRAPHICS, 0, uniformData);
					uniforms.releaseCopy(uniformData);
				}
				vkCmdDraw(commandbuffer, vertexCount, instanceCount, firstVertex, firstInstance);
			}
		});
		change = true;
	}

	@Override
	public void drawIndexed(int indexCount, int instanceCount, int firstIndex, int firstInstance) {
		VulkanGraphicsPipeline pipeline = (VulkanGraphicsPipeline) state.pipeline;
		VulkanUniforms uniforms = pipeline.getUniforms();
		final ByteBuffer uniformData = uniforms.pollChanged() ? uniforms.getCopy() : null;

		commands.add(new Runnable() {

			@Override
			public void run() {
				flushMemoryBarriers();
				if (uniformData != null) {
					vkCmdPushConstants(commandbuffer, pipeline.layout, VK_SHADER_STAGE_ALL_GRAPHICS, 0, uniformData);
					uniforms.releaseCopy(uniformData);
				}
				vkCmdDrawIndexed(commandbuffer, indexCount, instanceCount, firstIndex, 0, firstInstance);
			}
		});
		change = true;
	}

	@Override
	public void bindVertexArray(VertexArray vao) {
		commands.add(new Runnable() {

			@Override
			public void run() {
				VulkanVertexArray v = (VulkanVertexArray) vao;

				flushMemoryBarriers();

				vkCmdBindVertexBuffers(commandbuffer, 0, v.pBuffers, v.pOffsets);
				if (v.indexBuffer != null) {
					vkCmdBindIndexBuffer(commandbuffer, v.indexBuffer.getBufferHandle(), 0, VK_INDEX_TYPE_UINT32);
				}
			}
		});
		change = true;
	}

	@Override
	public void copyTexture2D(Texture2D src, Texture2D dst) {
		commands.add(new Runnable() {

			@Override
			public void run() {
				VulkanTexture2D s = (VulkanTexture2D) src;
				VulkanTexture2D d = (VulkanTexture2D) dst;

				s.setLayout(commandbuffer, VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL);
				d.setLayout(commandbuffer, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL);

				VkImageBlit.Buffer pRegions = VkImageBlit.calloc(1);

				pRegions.get(0).srcOffsets().get(0).set(0, 0, 0);
				pRegions.get(0).dstOffsets().get(0).set(0, 0, 0);
				pRegions.get(0).srcOffsets().get(1).set(src.width, src.height, 1);
				pRegions.get(0).dstOffsets().get(1).set(dst.width, dst.height, 1);
				pRegions.get(0).srcSubresource().set(s.aspect, 0, 0, 1);
				pRegions.get(0).dstSubresource().set(d.aspect, 0, 0, 1);

				flushMemoryBarriers();

				vkCmdBlitImage(commandbuffer, s.image, VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL, d.image, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL, pRegions, VK_FILTER_NEAREST);

				pRegions.free();
			}
		});
		change = true;
	}

	@Override
	public void memoryBarrier() {
		commands.add(new Runnable() {

			@Override
			public void run() {
				int access = 0;
				access |= VK_ACCESS_MEMORY_READ_BIT;
				access |= VK_ACCESS_MEMORY_WRITE_BIT;
				access |= VK_ACCESS_SHADER_READ_BIT;
				access |= VK_ACCESS_SHADER_WRITE_BIT;

				VkMemoryBarrier.Buffer pMemoryBarriers = VkMemoryBarrier.calloc(1);
				pMemoryBarriers.get(0).set(VK_STRUCTURE_TYPE_MEMORY_BARRIER, NULL, access, access);
				vkCmdPipelineBarrier(commandbuffer, VK_PIPELINE_STAGE_ALL_COMMANDS_BIT, VK_PIPELINE_STAGE_ALL_COMMANDS_BIT, 0, pMemoryBarriers, null, null);
				pMemoryBarriers.free();
			}
		});
		change = true;
	}
}
