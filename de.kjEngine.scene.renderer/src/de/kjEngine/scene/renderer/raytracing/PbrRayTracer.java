/**
 * 
 */
package de.kjEngine.scene.renderer.raytracing;

import java.util.Set;

import de.kjEngine.graphics.CommandBuffer;
import de.kjEngine.graphics.GraphicsPipeline;
import de.kjEngine.graphics.shader.PipelineSource;
import de.kjEngine.graphics.shader.ShaderCompilationException;
import de.kjEngine.io.RL;
import de.kjEngine.renderer.Filter;
import de.kjEngine.renderer.RenderList;
import de.kjEngine.renderer.Renderable.RenderImplementation.ID;
import de.kjEngine.scene.Scene;
import de.kjEngine.scene.SceneBVH;
import de.kjEngine.scene.camera.CameraComponent;
import de.kjEngine.scene.camera.CameraComponentUBO;

/**
 * @author konst
 *
 */
public class PbrRayTracer extends Filter {
	
	private static GraphicsPipeline pipeline;
	static {
		try {
			PipelineSource source = PipelineSource.parse(RL.create("jar://scene.renderer/de/kjEngine/scene/renderer/raytracing/pbr.shader"));
			source.add(CameraComponentUBO.LIBRARY_SOURCE);
			source.add(SceneBVH.LIBRARY_SOURCE);
			
			pipeline = createPipeline(source);
		} catch (ShaderCompilationException e) {
			e.printStackTrace();
		}
	}

	public PbrRayTracer(InputProvider inputProvider) {
		super(inputProvider, pipeline);
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	protected void linkImplementation() {
		addFrameBufferToOutput(frameBuffer);
	}

	@Override
	protected void updateDescriptorsImplementation() {
	}

	@Override
	public void getRequiredRenderImplementations(Set<ID> target) {
		target.add(SceneBVH.ID);
		target.add(CameraComponent.UBO);
	}

	@Override
	protected void bindDescriptors(RenderList renderList, CommandBuffer cb) {
		Scene scene = renderList.get(Scene.class);
		
		if (scene == null) {
			return;
		}
		
		cb.bindDescriptorSet(scene.camera.getRenderImplementation(CameraComponentUBO.class).getDescriptorSet(), "camera");
		cb.bindDescriptorSet(scene.getRenderImplementation(SceneBVH.class).descriptorSet, "bvh");
	}
}
