/**
 * 
 */
package de.kjEngine.graphics.vulkan;

import de.kjEngine.graphics.BottomLevelAccelerationStructure;
import de.kjEngine.graphics.IndexBuffer;
import de.kjEngine.graphics.VertexBuffer;
import de.kjEngine.graphics.shader.PrimitiveType;

/**
 * @author konst
 *
 */
public class EmulatedVulkanBottomLevelAccelerationStructure extends BottomLevelAccelerationStructure {

	/**
	 * @param vertexBuffer
	 * @param indexBuffer
	 * @param topology
	 */
	public EmulatedVulkanBottomLevelAccelerationStructure(VertexBuffer vertexBuffer, IndexBuffer indexBuffer, PrimitiveType topology) {
		super(vertexBuffer, indexBuffer, topology);
	}

	@Override
	public void dispose() {
	}

	@Override
	public void update() {
	}
}
