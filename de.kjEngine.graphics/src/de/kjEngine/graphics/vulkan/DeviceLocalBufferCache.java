/**
 * 
 */
package de.kjEngine.graphics.vulkan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author konst
 *
 */
public class DeviceLocalBufferCache {

	public static class BufferDescription {
		
		public final int size;
		public final int usage;
		public final int properties;

		public BufferDescription(int size, int usage, int properties) {
			this.size = size;
			this.usage = usage;
			this.properties = properties;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + properties;
			result = prime * result + size;
			result = prime * result + usage;
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
			BufferDescription other = (BufferDescription) obj;
			if (properties != other.properties)
				return false;
			if (size != other.size)
				return false;
			if (usage != other.usage)
				return false;
			return true;
		}
	}
	
	private static Map<BufferDescription, List<VulkanBuffer>> cache = new HashMap<>();
	
	public static VulkanBuffer get(BufferDescription desc) {
		List<VulkanBuffer> buffers = cache.get(desc);
		if (buffers != null && !buffers.isEmpty()) {
			return buffers.remove(buffers.size() - 1);
		}
		return VulkanUtil.createBuffer(desc.size, desc.usage, desc.properties);
	}
	
	public static void put(BufferDescription desc, VulkanBuffer buffer) {
		List<VulkanBuffer> buffers = cache.get(desc);
		if (buffers == null) {
			buffers = new ArrayList<>();
			cache.put(desc, buffers);
		}
		buffers.add(buffer);
	}
}
