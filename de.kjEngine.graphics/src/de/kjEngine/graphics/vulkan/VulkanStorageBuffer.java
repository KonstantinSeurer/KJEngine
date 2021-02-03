/**
 * 
 */
package de.kjEngine.graphics.vulkan;

import static de.kjEngine.graphics.vulkan.VulkanUtil.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.vulkan.VK10.*;

import java.nio.ByteBuffer;
import java.util.List;

import org.lwjgl.PointerBuffer;

import de.kjEngine.graphics.BufferAccessor;
import de.kjEngine.graphics.Graphics;
import de.kjEngine.graphics.ShaderBuffer;
import de.kjEngine.graphics.khronos.Std140ShaderBufferAccessor;
import de.kjEngine.graphics.khronos.Std430ShaderBufferAccessor;
import de.kjEngine.graphics.shader.BufferSource;
import de.kjEngine.graphics.shader.StructSource;

/**
 * @author konst
 *
 */
public class VulkanStorageBuffer extends ShaderBuffer implements VulkanBuffer {

	private long buffer, bufferMemory;
	private ByteBuffer data;
	private BufferAccessor accessor;

	public VulkanStorageBuffer(BufferSource source, List<StructSource> definedStructs, int flags) {
		super(source, definedStructs, flags);

		switch (source.getLayout()) {
		case PACKED:
			accessor = new Std430ShaderBufferAccessor(source.asStruct(), definedStructs);
			break;
		case STANDARD:
			accessor = new Std140ShaderBufferAccessor(source.asStruct(), definedStructs);
			break;
		}

		VulkanBuffer b = createBuffer(accessor.getSize(), VK_BUFFER_USAGE_STORAGE_BUFFER_BIT, VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT);
		buffer = b.getBuffer();
		bufferMemory = b.getMemory();

		PointerBuffer ppData = memAllocPointer(1);
		err(vkMapMemory(Graphics.getVkContext().getDevice(), bufferMemory, 0, accessor.getSize(), 0, ppData));
		data = memByteBuffer(ppData.get(0), accessor.getSize());
		memFree(ppData);
		
		accessor.setData(data);
	}

	@Override
	public void dispose() {
		vkDestroyBuffer(Graphics.getVkContext().getDevice(), buffer, null);
		vkFreeMemory(Graphics.getVkContext().getDevice(), bufferMemory, null);
	}

	@Override
	public void update() {
	}

	@Override
	public long getBuffer() {
		return buffer;
	}

	@Override
	public Type getType() {
		return Type.STORAGE_BUFFER;
	}

	@Override
	public long getMemory() {
		return bufferMemory;
	}

	@Override
	public BufferAccessor getAccessor() {
		return accessor;
	}
}
