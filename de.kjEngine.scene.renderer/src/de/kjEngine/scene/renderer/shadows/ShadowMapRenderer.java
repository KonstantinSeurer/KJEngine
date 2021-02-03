package de.kjEngine.scene.renderer.shadows;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.kjEngine.graphics.CommandBuffer;
import de.kjEngine.renderer.Pipeline;
import de.kjEngine.renderer.PrepassStage;
import de.kjEngine.renderer.RenderList;
import de.kjEngine.renderer.Renderable.RenderImplementation.ID;
import de.kjEngine.scene.Scene;
import de.kjEngine.scene.light.SceneShadowMapList;
import de.kjEngine.scene.light.ShadowMapComponent;

public class ShadowMapRenderer implements PrepassStage {

	public static interface PipelineProvider {
		
		public Pipeline get(int resolution);
		
		public void getRequiredRenderImplementations(Set<ID> target);
	}
	
	private final Map<Integer, Pipeline> pipelines = new HashMap<>();
	private final PipelineProvider pipelineProvider;

	public ShadowMapRenderer(PipelineProvider pipelineProvider) {
		this.pipelineProvider = pipelineProvider;
	}

	@Override
	public void updateDescriptors() {
		for (Pipeline pipeline : pipelines.values()) {
			pipeline.updateDescriptors();
		}
	}

	@Override
	public void render(RenderList renderList, CommandBuffer cb) {
		Scene scene = renderList.get(Scene.class);
		if (scene == null) {
			return;
		}
		for (ShadowMapComponent<?> map : scene.getRenderImplementation(SceneShadowMapList.class).shadowMaps) {
			RenderList newRenderList = new RenderList(renderList);
			newRenderList.add(map);
			
			Pipeline pipeline = pipelines.get(map.resolution);
			if (pipeline == null) {
				pipeline = pipelineProvider.get(map.resolution);
				pipelines.put(map.resolution, pipeline);
			}
			
			pipeline.reset();
			pipeline.render(newRenderList, cb);
		}
	}

	@Override
	public void dispose() {
		for (Pipeline pipeline : pipelines.values()) {
			pipeline.dispose();
		}
	}

	@Override
	public void getRequiredRenderImplementations(Set<ID> target) {
		pipelineProvider.getRequiredRenderImplementations(target);
		target.add(SceneShadowMapList.ID);
	}
}
