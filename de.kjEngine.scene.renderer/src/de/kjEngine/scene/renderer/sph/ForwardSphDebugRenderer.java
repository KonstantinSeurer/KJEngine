/**
 * 
 */
package de.kjEngine.scene.renderer.sph;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.kjEngine.graphics.CommandBuffer;
import de.kjEngine.graphics.FrameBuffer;
import de.kjEngine.graphics.Graphics;
import de.kjEngine.graphics.GraphicsPipeline;
import de.kjEngine.graphics.Texture2D;
import de.kjEngine.graphics.shader.PipelineSource;
import de.kjEngine.graphics.shader.ShaderCompilationException;
import de.kjEngine.graphics.shader.ShaderType;
import de.kjEngine.io.RL;
import de.kjEngine.renderer.RenderList;
import de.kjEngine.renderer.Renderable.RenderImplementation.ID;
import de.kjEngine.renderer.Stage;
import de.kjEngine.scene.Scene;
import de.kjEngine.scene.camera.CameraComponent;
import de.kjEngine.scene.camera.CameraComponentUBO;
import de.kjEngine.scene.model.Model;
import de.kjEngine.scene.model.ModelVAO;
import de.kjEngine.scene.physics.sph.GPUSPH;

/**
 * @author konst
 *
 */
public class ForwardSphDebugRenderer extends Stage {
	
	private FrameBuffer frameBuffer;
	private GraphicsPipeline pipeline;
	
	private Texture2D depth, base;
	
	private List<GPUSPH> renderQueue = new ArrayList<>();

	/**
	 * @param inputProvider
	 */
	public ForwardSphDebugRenderer(InputProvider inputProvider) {
		super(inputProvider);
		
		try {
			PipelineSource source = PipelineSource.parse(RL.create("jar://scene.renderer/de/kjEngine/core/scene/renderer/sph/forwardDebug.shader"));
			source.getDescriptorSets().add(GPUSPH.DESCRIPTOR_SET_SOURCE);
			source.getDescriptorSets().add(CameraComponentUBO.DESCRIPTOR_SET_SOURCE);
			source.add(GPUSPH.LIBRARY_SOURCE);
			pipeline = Graphics.createGraphicsPipeline(source);
		} catch (ShaderCompilationException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void dispose() {
		pipeline.dispose();
		frameBuffer.dispose();
	}

	@Override
	protected void renderImplementation(RenderList renderList, CommandBuffer cb) {
		cb.bindFrameBuffer(frameBuffer);
		cb.copyTexture2D(depth, frameBuffer.getDepthAttachment());
		cb.copyTexture2D(base, frameBuffer.getColorAttachment("result"));
		
		cb.bindPipeline(pipeline);
		
		Model sphere = Model.getDefaultCube();
		
		cb.bindVertexArray(sphere.getRenderImplementation(ModelVAO.class).getVao());
		
		cb.bindDescriptorSet(renderList.get(Scene.class).camera.getRenderImplementation(CameraComponentUBO.class).getDescriptorSet(), "camera");
		
		for (int i = 0; i < renderQueue.size(); i++) {
			GPUSPH sph = renderQueue.get(i);
			
			cb.bindDescriptorSet(sph.getDescriptorSet(), "data");
			
			cb.drawIndexed(sphere.getIndexCount(), sph.getParticleCount(), 0, 0);
		}
		
		cb.unbindFrameBuffer(frameBuffer);
		
		renderQueue.clear();
	}

	@Override
	protected void linkImplementation() {
		addFrameBufferToOutput(frameBuffer);
		
		depth = input.get("depth");
		base = input.get("base");
	}

	@Override
	protected void updateDescriptorsImplementation() {
	}

	@Override
	public void getRequiredRenderImplementations(Set<ID> target) {
		target.add(CameraComponent.UBO);
		target.add(Model.VAO);
	}

	/**
	 * @return the renderQueue
	 */
	public List<GPUSPH> getRenderQueue() {
		return renderQueue;
	}

	@Override
	protected void resizeImplementation(int width, int height) {
		frameBuffer = Graphics.createFramebuffer(width, height, pipeline.getSource().getShader(ShaderType.FRAGMENT).getOutput());
	}
}
