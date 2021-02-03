/**
 * 
 */
package de.kjEngine.graphics.vulkan;

import static org.lwjgl.system.MemoryUtil.*;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.vulkan.VkPushConstantRange;

import de.kjEngine.graphics.BufferAccessor;
import de.kjEngine.graphics.khronos.Std140ShaderBufferAccessor;
import de.kjEngine.graphics.shader.PipelineSource;
import de.kjEngine.graphics.shader.StructSource;
import de.kjEngine.util.Disposable;

/**
 * @author konst
 *
 */
public class VulkanUniforms implements Disposable {
	
	private BufferAccessor accessor;
	private ByteBuffer uniformData;
	private List<ByteBuffer> copyCache = new ArrayList<>();
	
	public VulkanUniforms(PipelineSource source) {
		accessor = new Std140ShaderBufferAccessor(new StructSource("uniforms", source.getUniforms()), source.getStructs());

		uniformData = memAlloc(accessor.getSize());
		accessor.setData(uniformData);
	}
	
	public void getPushConstantRange(VkPushConstantRange range, int stageFlags) {
		range.set(stageFlags, 0, accessor.getSize());
	}

	/**
	 * @return the accessor
	 */
	public BufferAccessor getAccessor() {
		return accessor;
	}
	
	public ByteBuffer getCopy() {
		if (copyCache.isEmpty()) {
			copyCache.add(memAlloc(accessor.getSize()));
		}
		ByteBuffer copy = copyCache.remove(copyCache.size() - 1);
		memCopy(uniformData, copy);
		return copy;
	}
	
	public void releaseCopy(ByteBuffer copy) {
		copyCache.add(copy);
	}
	
	public boolean pollChanged() {
		return accessor.getSize() > 0; // TODO: implement change
	}

	@Override
	public void dispose() {
		memFree(uniformData);
	}
}
