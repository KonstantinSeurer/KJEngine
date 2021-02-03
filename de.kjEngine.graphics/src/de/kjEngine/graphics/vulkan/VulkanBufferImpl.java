/**
 * 
 */
package de.kjEngine.graphics.vulkan;

/**
 * @author konst
 *
 */
public class VulkanBufferImpl implements VulkanBuffer {
	
	private final long buffer, memory;

	public VulkanBufferImpl(long buffer, long memory) {
		this.buffer = buffer;
		this.memory = memory;
	}

	@Override
	public long getBuffer() {
		return buffer;
	}

	@Override
	public long getMemory() {
		return memory;
	}
}
