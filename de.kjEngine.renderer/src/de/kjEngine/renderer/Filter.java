/**
 * 
 */
package de.kjEngine.renderer;

import de.kjEngine.graphics.CommandBuffer;
import de.kjEngine.graphics.FrameBuffer;
import de.kjEngine.graphics.Graphics;
import de.kjEngine.graphics.GraphicsPipeline;
import de.kjEngine.graphics.shader.ObjectSource;
import de.kjEngine.graphics.shader.PipelineSource;
import de.kjEngine.graphics.shader.ShaderCompilationException;
import de.kjEngine.graphics.shader.ShaderSource;
import de.kjEngine.graphics.shader.ShaderType;
import de.kjEngine.io.RL;
import de.kjEngine.io.ResourceManager;

/**
 * @author konst
 *
 */
public abstract class Filter extends Stage {

	public static GraphicsPipeline createPipeline(PipelineSource source) {
		try {
			source.getShaders()
					.add(ShaderSource.parse(new ObjectSource("vertexShader", ResourceManager.loadTextResource(RL.create("jar://renderer/de/kjEngine/renderer/filterVertexShader.shader"), true))));
		} catch (ShaderCompilationException e) {
			e.printStackTrace();
		}
		ShaderSource fragmentShader = source.getShader(ShaderType.FRAGMENT);
		fragmentShader.getInput().getSettings().put("cullMode", "NONE");
		fragmentShader.getOutput().getSettings().put("blend", "false");
		fragmentShader.getOutput().getSettings().put("depth", "false");
		return Graphics.createGraphicsPipeline(source);
	}

	protected FrameBuffer frameBuffer;
	protected GraphicsPipeline pipeline;

	public Filter(InputProvider inputProvider, GraphicsPipeline pipeline) {
		super(inputProvider);
		this.pipeline = pipeline;
	}

	@Override
	public void renderImplementation(RenderList renderList, CommandBuffer cb) {
		cb.bindFrameBuffer(frameBuffer);
		cb.bindPipeline(pipeline);
		bindDescriptors(renderList, cb);
		cb.draw(6, 1, 0, 0);
		cb.unbindFrameBuffer(frameBuffer);
	}

	protected abstract void bindDescriptors(RenderList renderList, CommandBuffer cb);

	@Override
	public void dispose() {
		frameBuffer.dispose();
		pipeline.dispose();
	}

	@Override
	protected void resizeImplementation(int width, int height) {
		if (frameBuffer != null) {
			frameBuffer.dispose();
		}
		frameBuffer = Graphics.createFramebuffer(width, height, pipeline.getSource().getShader(ShaderType.FRAGMENT).getOutput());
	}
}
