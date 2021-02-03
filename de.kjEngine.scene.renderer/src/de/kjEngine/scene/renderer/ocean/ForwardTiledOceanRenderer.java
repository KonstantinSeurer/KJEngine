/**
 * 
 */
package de.kjEngine.scene.renderer.ocean;

import java.util.Set;

import de.kjEngine.graphics.CommandBuffer;
import de.kjEngine.graphics.DescriptorSet;
import de.kjEngine.graphics.FrameBuffer;
import de.kjEngine.graphics.Graphics;
import de.kjEngine.graphics.GraphicsPipeline;
import de.kjEngine.graphics.ShaderBuffer;
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
import de.kjEngine.scene.light.SceneLightBuffer;
import de.kjEngine.scene.ocean.TiledOceanComponent;
import de.kjEngine.scene.ocean.SceneTiledOceanList;

/**
 * @author konst
 *
 */
public class ForwardTiledOceanRenderer extends Stage {
	
	private static final PipelineSource PIPELINE_SOURCE;
	static {
		PipelineSource source = null;
		try {
			source = PipelineSource.parse(RL.create("jar://scene.renderer/de/kjEngine/scene/renderer/ocean/forwardTiled.shader"));
		} catch (ShaderCompilationException e) {
			e.printStackTrace();
		}
		source.add(CameraComponentUBO.LIBRARY_SOURCE);
		source.getDescriptorSets().add(TiledOceanComponent.DESCRIPTOR_SET_SOURCE);
		source.add(SceneLightBuffer.LIBRARY_SOURCE);
		PIPELINE_SOURCE = source;
	}

	public static final String INPUT_TEXTURE_DEPTH_NAME = "depth";
	public static final String INPUT_TEXTURE_BASE_NAME = "base";
	public static final String INPUT_TEXTURE_ENVIRONMENT_NAME = "environment";

	public static final String OUTPUT_TEXTURE_RESULT_NAME = "result";
	public static final String OUTPUT_TEXTURE_DEPTH_NAME = "depth";
	
	private DescriptorSet descriptorSet;
	private ShaderBuffer settingsUbo;
	private FrameBuffer framebuffer;
	private GraphicsPipeline pipeline;

	/**
	 * @param inputProvider
	 * @param pipeline
	 */
	public ForwardTiledOceanRenderer(InputProvider inputProvider) {
		super(inputProvider);
		
		pipeline = Graphics.createGraphicsPipeline(PIPELINE_SOURCE);
		
		settingsUbo = Graphics.createUniformBuffer(PIPELINE_SOURCE, "data", "settings", ShaderBuffer.FLAG_NONE);
		
		descriptorSet = Graphics.createDescriptorSet(PIPELINE_SOURCE.getDescriptorSet("data"));
		descriptorSet.set("settings", settingsUbo);
		descriptorSet.update();
	}

	@Override
	protected void linkImplementation() {
		descriptorSet.set("depth", input.get(INPUT_TEXTURE_DEPTH_NAME));
		descriptorSet.set("base", input.get(INPUT_TEXTURE_BASE_NAME));
		descriptorSet.set("environment", input.get(INPUT_TEXTURE_ENVIRONMENT_NAME));
		descriptorSet.update();

		output.put(OUTPUT_TEXTURE_RESULT_NAME, framebuffer.getColorAttachment("color"));
		output.put(OUTPUT_TEXTURE_DEPTH_NAME, framebuffer.getDepthAttachment());
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
		
		cb.copyTexture2D(input.get(INPUT_TEXTURE_BASE_NAME), framebuffer.getColorAttachment("color"));
		cb.copyTexture2D(input.get(INPUT_TEXTURE_DEPTH_NAME), framebuffer.getDepthAttachment());
		
		cb.bindFrameBuffer(framebuffer);
		
		cb.bindPipeline(pipeline);
		
		cb.bindDescriptorSet(scene.camera.getRenderImplementation(CameraComponentUBO.class).getDescriptorSet(), "camera");
		cb.bindDescriptorSet(descriptorSet, "data");
		cb.bindDescriptorSet(scene.getRenderImplementation(SceneLightBuffer.class).getDescriptorSet(), "lights");
		
		for (TiledOceanComponent ocean : scene.getRenderImplementation(SceneTiledOceanList.class).oceans) {
			cb.bindDescriptorSet(ocean.descriptorSet, "ocean");
			
			cb.draw(ocean.tileCountX * ocean.tileCountZ, 1, 0, 0);
		}
		
		cb.unbindFrameBuffer(framebuffer);
	}

	@Override
	protected void resizeImplementation(int width, int height) {
		if (framebuffer != null) {
			framebuffer.dispose();
		}
		framebuffer = Graphics.createFramebuffer(width, height, PIPELINE_SOURCE.getShader(ShaderType.FRAGMENT).getOutput());
	}
}
