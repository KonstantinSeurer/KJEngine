/**
 * 
 */
package de.kjEngine.graphics.vulkan;

import static de.kjEngine.graphics.vulkan.VulkanUtil.*;
import static org.lwjgl.vulkan.VK10.*;

import de.kjEngine.graphics.IndexBuffer;
import de.kjEngine.graphics.vulkan.DeviceLocalBufferCache.BufferDescription;

/**
 * @author konst
 *
 */
public class VulkanIndexBuffer extends IndexBuffer {

	private long buffer, bufferMemory;
	private int count;
	private BufferDescription desc;

	public VulkanIndexBuffer(int[] data) {
		count = data.length;

		int size = data.length * 4;

		StagingBuffer stagingBuffer = StagingBuffer.get(size);
		stagingBuffer.data.asIntBuffer().put(0, data);

		desc = new BufferDescription(size, VK_BUFFER_USAGE_TRANSFER_DST_BIT | VK_BUFFER_USAGE_INDEX_BUFFER_BIT, VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT);
		VulkanBuffer b = DeviceLocalBufferCache.get(desc);
		buffer = b.getBuffer();
		bufferMemory = b.getMemory();

		copyBuffer(stagingBuffer.buffer.getBuffer(), buffer, 0, 0, size);
	}

	@Override
	public void dispose() {
		DeviceLocalBufferCache.put(desc, new VulkanBufferImpl(buffer, bufferMemory));
	}

	@Override
	public int getCount() {
		return count;
	}

	public long getBufferHandle() {
		return buffer;
	}
}
