/**
 * 
 */
package de.kjEngine.scene.renderer.shadows;

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
import de.kjEngine.scene.light.ShadowMapComponent;
import de.kjEngine.scene.ocean.SceneTiledOceanList;
import de.kjEngine.scene.ocean.TiledOceanComponent;

/**
 * @author konst
 *
 */
public class ShadowDepthMapTiledOceanRenderer extends Stage {

	private static final PipelineSource PIPELINE_SOURCE;
	static {
		PipelineSource source = null;
		try {
			source = PipelineSource.parse(RL.create("jar://scene.renderer/de/kjEngine/scene/renderer/shadows/depthMapTiledOcean.shader"));
		} catch (ShaderCompilationException e) {
			e.printStackTrace();
		}
		source.addDescriptorSet(CameraComponentUBO.DESCRIPTOR_SET_SOURCE.shallowCopy().setName("sceneCamera"));
		source.addDescriptorSet(CameraComponentUBO.DESCRIPTOR_SET_SOURCE.shallowCopy().setName("lightCamera"));
		source.getDescriptorSets().add(TiledOceanComponent.DESCRIPTOR_SET_SOURCE);
		PIPELINE_SOURCE = source;
	}

	private FrameBuffer frameBuffer;
	private GraphicsPipeline pipeline;

	private Texture2D depth;

	/**
	 * @param inputProvider
	 * @param pipeline
	 */
	public ShadowDepthMapTiledOceanRenderer(InputProvider inputProvider) {
		super(inputProvider);

		pipeline = Graphics.createGraphicsPipeline(PIPELINE_SOURCE);
	}

	@Override
	protected void linkImplementation() {
		depth = input.get("depth");

		addFrameBufferToOutput(frameBuffer);
	}

	@Override
	protected void updateDescriptorsImplementation() {
	}

	@Override
	public void getRequiredRenderImplementations(Set<ID> target) {
		target.add(CameraComponent.UBO);
		target.add(Scene.OCEAN_LIST);
	}

	@Override
	public void dispose() {
	}

	@Override
	protected void renderImplementation(RenderList renderList, CommandBuffer cb) {
		Scene scene = renderList.get(Scene.class);

		if (depth != null) {
			cb.copyTexture2D(depth, frameBuffer.getDepthAttachment());
		}

		cb.bindFrameBuffer(frameBuffer);

		if (depth == null) {
			cb.clearFrameBuffer(frameBuffer);
		}

		cb.bindPipeline(pipeline);

		cb.bindDescriptorSet(renderList.get(ShadowMapComponent.class).camera.getRenderImplementation(CameraComponentUBO.class).getDescriptorSet(), "lightCamera");
		cb.bindDescriptorSet(scene.camera.getRenderImplementation(CameraComponentUBO.class).getDescriptorSet(), "sceneCamera");

		for (TiledOceanComponent ocean : scene.getRenderImplementation(SceneTiledOceanList.class).oceans) {
			cb.bindDescriptorSet(ocean.descriptorSet, "ocean");

			cb.draw(ocean.tileCountX * ocean.tileCountZ, 1, 0, 0);
		}

		cb.unbindFrameBuffer(frameBuffer);
	}

	@Override
	protected void resizeImplementation(int width, int height) {
		if (frameBuffer != null) {
			frameBuffer.dispose();
		}
		frameBuffer = Graphics.createFramebuffer(width, height, PIPELINE_SOURCE.getShader(ShaderType.FRAGMENT).getOutput());
	}
}
