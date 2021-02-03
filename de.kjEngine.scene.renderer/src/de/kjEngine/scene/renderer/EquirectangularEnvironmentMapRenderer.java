package de.kjEngine.scene.renderer;

import java.util.Set;

import de.kjEngine.graphics.CommandBuffer;
import de.kjEngine.graphics.DescriptorSet;
import de.kjEngine.graphics.Graphics;
import de.kjEngine.graphics.GraphicsPipeline;
import de.kjEngine.graphics.shader.PipelineSource;
import de.kjEngine.graphics.shader.ShaderCompilationException;
import de.kjEngine.io.RL;
import de.kjEngine.renderer.Filter;
import de.kjEngine.renderer.RenderList;
import de.kjEngine.renderer.Renderable.RenderImplementation.ID;
import de.kjEngine.scene.Scene;
import de.kjEngine.scene.camera.CameraComponent;
import de.kjEngine.scene.camera.CameraComponentUBO;

/**
 * @author konst
 *
 */
public class EquirectangularEnvironmentMapRenderer extends Filter {
	
	private static GraphicsPipeline pipeline;
	static {
		PipelineSource source = null;
		try {
			source = PipelineSource.parse(RL.create("jar://scene.renderer/de/kjEngine/scene/renderer/equirectangularEnvironmentMap.shader"));
		} catch (ShaderCompilationException e) {
			e.printStackTrace();
		}
		source.add(CameraComponentUBO.LIBRARY_SOURCE);
		
		pipeline = createPipeline(source);
	}

	private DescriptorSet descriptorSet;
	
	public EquirectangularEnvironmentMapRenderer(InputProvider inputProvider) {
		super(inputProvider, pipeline);
		
		descriptorSet = Graphics.createDescriptorSet(pipeline.getSource().getDescriptorSet("data"));
	}

	@Override
	protected void linkImplementation() {
		descriptorSet.set("map", input.get("map"));
		descriptorSet.update();
		
		output.put("result", frameBuffer.getColorAttachment("color"));
	}

	@Override
	protected void bindDescriptors(RenderList renderList, CommandBuffer cb) {
		cb.bindDescriptorSet(descriptorSet, "data");
		
		Scene scene = renderList.get(Scene.class);
		if (scene == null) {
			return;
		}
		
		cb.bindDescriptorSet(scene.camera.getRenderImplementation(CameraComponentUBO.class).getDescriptorSet(), "camera");
	}

	@Override
	protected void updateDescriptorsImplementation() {
	}

	@Override
	public void getRequiredRenderImplementations(Set<ID> target) {
		target.add(CameraComponent.UBO);
	}
}
