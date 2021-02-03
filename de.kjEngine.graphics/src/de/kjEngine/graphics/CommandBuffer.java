/**
 * 
 */
package de.kjEngine.graphics;

import de.kjEngine.util.Disposable;

/**
 * @author konst
 *
 */
public abstract class CommandBuffer implements Disposable {

	public static final int FLAG_NONE = 0;
	public static final int FLAG_DYNAMIC = 1;
	public static final int FLAG_VULKAN_RECORD_COMMANDS_LIVE = 2;

	public static abstract class Command implements Runnable {
		public abstract void record(CommandBuffer b);
	}

	public static class State {
		public FrameBuffer framebuffer;
		public Pipeline pipeline;
	}

	public State state = new State();
	
	protected CommandBuffer() {
	}

	public abstract void clear();

	public abstract void submit();
	
	public abstract void bindDescriptorSet(DescriptorSet set, String name);
	
	public abstract void bindFrameBuffer(FrameBuffer framebuffer);
	public abstract void unbindFrameBuffer(FrameBuffer framebuffer);
	public abstract void clearFrameBuffer(FrameBuffer framebuffer);
	
	public abstract void copyTexture2D(Texture2D src, Texture2D dst);
	
	public abstract void bindQuery(Query query);
	public abstract void unbindQuery(Query query);
	
	public abstract void bindPipeline(Pipeline pipeline);
	
	public abstract void compute(int width, int height, int length);
	
	public abstract void trace(int width, int height);
	
	public abstract void draw(int vertexCount, int instanceCount, int firstVertex, int firstInstance);
	public abstract void drawIndexed(int indexCount, int instanceCount, int firstIndex, int firstInstance);
	
	public abstract void bindVertexArray(VertexArray vao);

	public abstract void memoryBarrier();
}
