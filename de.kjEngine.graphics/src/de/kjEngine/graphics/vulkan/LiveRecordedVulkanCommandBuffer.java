/**
 * 
 */
package de.kjEngine.graphics.vulkan;

import de.kjEngine.graphics.CommandBuffer;
import de.kjEngine.graphics.DescriptorSet;
import de.kjEngine.graphics.FrameBuffer;
import de.kjEngine.graphics.Pipeline;
import de.kjEngine.graphics.Query;
import de.kjEngine.graphics.Texture2D;
import de.kjEngine.graphics.VertexArray;

/**
 * @author konst
 *
 */
public class LiveRecordedVulkanCommandBuffer extends CommandBuffer {

	/**
	 * 
	 */
	public LiveRecordedVulkanCommandBuffer() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

	@Override
	public void submit() {
		// TODO Auto-generated method stub

	}

	@Override
	public void bindDescriptorSet(DescriptorSet set, String name) {
		// TODO Auto-generated method stub

	}

	@Override
	public void bindFrameBuffer(FrameBuffer framebuffer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unbindFrameBuffer(FrameBuffer framebuffer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clearFrameBuffer(FrameBuffer framebuffer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void bindQuery(Query query) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unbindQuery(Query query) {
		// TODO Auto-generated method stub

	}

	@Override
	public void bindPipeline(Pipeline pipeline) {
		// TODO Auto-generated method stub

	}

	@Override
	public void compute(int width, int height, int length) {
		// TODO Auto-generated method stub

	}

	@Override
	public void trace(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void draw(int vertexCount, int instanceCount, int firstVertex, int firstInstance) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawIndexed(int indexCount, int instanceCount, int firstIndex, int firstInstance) {
		// TODO Auto-generated method stub

	}

	@Override
	public void bindVertexArray(VertexArray vao) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void copyTexture2D(Texture2D src, Texture2D dst) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void memoryBarrier() {
		// TODO Auto-generated method stub
		
	}
}
