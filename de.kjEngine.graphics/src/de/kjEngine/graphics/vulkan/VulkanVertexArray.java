/**
 * 
 */
package de.kjEngine.graphics.vulkan;

import static org.lwjgl.system.MemoryUtil.*;

import java.nio.LongBuffer;

import de.kjEngine.graphics.IndexBuffer;
import de.kjEngine.graphics.VertexArray;
import de.kjEngine.graphics.VertexBuffer;

/**
 * @author konst
 *
 */
public class VulkanVertexArray extends VertexArray {

	VertexBuffer[] vertexBuffers;
	VulkanIndexBuffer indexBuffer;
	LongBuffer pBuffers, pOffsets;

	public VulkanVertexArray() {
	}

	@Override
	public void dispose() {
		memFree(pBuffers);
		memFree(pOffsets);
	}

	@Override
	public void setIndexBuffer(IndexBuffer ib) {
		indexBuffer = (VulkanIndexBuffer) ib;
	}

	@Override
	public void setVertexBuffers(VertexBuffer[] vbs) {
		vertexBuffers = vbs;
		pBuffers = memAllocLong(vbs.length);
		pOffsets = memAllocLong(vbs.length);
		for (int i = 0; i < vbs.length; i++) {
			pBuffers.put(i, ((VulkanVertexBuffer) vbs[i]).getBufferHandle());
			pOffsets.put(i, 0);
		}
	}

	@Override
	public int getVertexBufferCount() {
		return vertexBuffers.length;
	}
}
