/**
 * 
 */
package de.kjEngine.graphics.vulkan;

import static de.kjEngine.graphics.vulkan.VulkanUtil.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.vulkan.VK10.*;

import java.nio.IntBuffer;
import java.nio.LongBuffer;

import org.lwjgl.vulkan.VkQueryPoolCreateInfo;

import de.kjEngine.graphics.AnySamplesPassedQuery;
import de.kjEngine.graphics.CommandBuffer;
import de.kjEngine.graphics.Graphics;
import de.kjEngine.graphics.NumSamplesPassedQuery;
import de.kjEngine.graphics.Query;

/**
 * @author konst
 *
 */
public class VulkanQuery implements Query, NumSamplesPassedQuery, AnySamplesPassedQuery {

	private long querypool;
	private Type type;

	public VulkanQuery(Type type) {
		this.type = type;

		VkQueryPoolCreateInfo poolCreateInfo = VkQueryPoolCreateInfo.calloc()
				.sType(VK_STRUCTURE_TYPE_QUERY_POOL_CREATE_INFO).pNext(NULL).flags(0);
		switch (type) {
		case ANY_SAMPLES_PASSED:
			poolCreateInfo.queryType(VK_QUERY_TYPE_OCCLUSION);
			break;
		case NUM_SAMPLES_PASSED:
			poolCreateInfo.queryType(VK_QUERY_TYPE_OCCLUSION);
			break;
		}
		poolCreateInfo.queryCount(1);
		poolCreateInfo.pipelineStatistics(0);

		LongBuffer pQuerypool = memAllocLong(1);
		err(vkCreateQueryPool(Graphics.getVkContext().getDevice(), poolCreateInfo, null, pQuerypool));
		querypool = pQuerypool.get(0);

		memFree(pQuerypool);
	}

	@Override
	public void dispose() {
		vkDestroyQueryPool(Graphics.getVkContext().getDevice(), querypool, null);
	}

//	@Override
//	public void bind(CommandBuffer b) {
//		b.append(new Command() {
//
//			@Override
//			public void run() {
//			}
//
//			@Override
//			public void record(CommandBuffer b) {
//				vkCmdBeginQuery(((VulkanCommandBuffer) b).getHandle(), querypool, 0, 0);
//			}
//		});
//	}
//
//	@Override
//	public void unbind(CommandBuffer b) {
//		b.append(new Command() {
//
//			@Override
//			public void run() {
//			}
//
//			@Override
//			public void record(CommandBuffer b) {
//				vkCmdEndQuery(((VulkanCommandBuffer) b).getHandle(), querypool, 0);
//			}
//		});
//	}
//
//	@Override
//	public void getResult32(CommandBuffer cb, IntBuffer pResult) {
//		cb.append(new Command() {
//
//			@Override
//			public void run() {
//				err(vkGetQueryPoolResults(Graphics.getVkContext().getDevice(), querypool, 0, 1, pResult, 4,
//						VK_QUERY_RESULT_WAIT_BIT));
//			}
//
//			@Override
//			public void record(CommandBuffer b) {
//			}
//		});
//	}
//
//	@Override
//	public void getResult64(CommandBuffer cb, LongBuffer pResult) {
//		cb.append(new Command() {
//
//			@Override
//			public void run() {
//				err(vkGetQueryPoolResults(Graphics.getVkContext().getDevice(), querypool, 0, 1, pResult, 8,
//						VK_QUERY_RESULT_WAIT_BIT | VK_QUERY_RESULT_64_BIT));
//			}
//
//			@Override
//			public void record(CommandBuffer b) {
//			}
//		});
//	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public void getSampleCount(CommandBuffer cb, IntBuffer pResult) {
		getResult32(cb, pResult);
	}

	@Override
	public void haveAnySamplesPassed(CommandBuffer cb, BooleanPointer pResult) {
		getResult32(cb, pResult.pointer);
	}

	@Override
	public void getResult32(CommandBuffer cb, IntBuffer pResult) {
	}

	@Override
	public void getResult64(CommandBuffer cb, LongBuffer pResult) {
	}
}
