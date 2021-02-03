/**
 * 
 */
package de.kjEngine.graphics.vulkan;

import static de.kjEngine.graphics.vulkan.VulkanUtil.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.vulkan.VK10.*;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.PointerBuffer;

import de.kjEngine.graphics.Graphics;

/**
 * @author konst
 *
 */
public class StagingBuffer {

	private static Map<Integer, StagingBuffer> buffers = new HashMap<>();

	public static StagingBuffer get(int size) {
		if (buffers.containsKey(size)) {
			return buffers.get(size);
		}

		StagingBuffer b = new StagingBuffer(size);
		buffers.put(size, b);
		return b;
	}

	VulkanBuffer buffer;
	ByteBuffer data;

	private StagingBuffer(int size) {
		buffer = createBuffer(size, VK_BUFFER_USAGE_TRANSFER_SRC_BIT, VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT);

		PointerBuffer ppData = memAllocPointer(1);
		err(vkMapMemory(Graphics.getVkContext().getDevice(), buffer.getMemory(), 0, size, 0, ppData));
		data = memByteBuffer(ppData.get(0), size);
		memFree(ppData);
	}
}
