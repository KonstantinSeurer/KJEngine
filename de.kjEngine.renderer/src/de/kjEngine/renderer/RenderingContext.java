package de.kjEngine.renderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.kjEngine.graphics.CommandBuffer;
import de.kjEngine.graphics.Graphics;
import de.kjEngine.graphics.Texture2D;
import de.kjEngine.renderer.Renderable.RenderImplementation;
import de.kjEngine.renderer.Renderable.RenderImplementation.ID;
import de.kjEngine.renderer.Renderable.RenderImplementation.Registry;

public class RenderingContext {

	public static final String FEATURE_RASTERIZATION = "rasterization";
	public static final String FEATURE_RAYTRACING = "raytracing";
	public static final String FEATURE_SHADOW_MAPPING = "shadowmapping";
	public static final String FEATURE_LIGHT_LIST = "lightlist";

	private static CommandBuffer cb = Graphics.createCommandBuffer(CommandBuffer.FLAG_DYNAMIC);

	private static Map<String, Pipeline> pipelines = new HashMap<>();
	private static Pipeline pipeline;
	private static List<RenderImplementation.ID> requiredRenderImplementations = new ArrayList<>();

	public static void init(Set<RenderImplementation.ID> implementations) {
		for (ID id : implementations) {
			if (!requiredRenderImplementations.contains(id)) {
				requiredRenderImplementations.add(id);
				addDependencies(id, requiredRenderImplementations.size() - 1, 0);
			}
		}
	}
	
	private static void addDependencies(ID id, int insertionIndex, int recursionDepth) {
		if (recursionDepth > 100) {
			throw new RuntimeException("Looping dependency id=" + id);
		}
		Set<ID> dependencies = Registry.getDependency(id);
		if (dependencies == null) {
			return;
		}
		for (ID dependency : dependencies) {
			if (!requiredRenderImplementations.contains(dependency)) {
				requiredRenderImplementations.add(insertionIndex, dependency);
				addDependencies(dependency, insertionIndex, recursionDepth + 1);
			} else if (requiredRenderImplementations.indexOf(dependency) > insertionIndex) {
				requiredRenderImplementations.remove(dependency);
				requiredRenderImplementations.add(insertionIndex, dependency);
				addDependencies(dependency, insertionIndex, recursionDepth + 1);
			}
		}
	}

	public static void resize(int width, int height) {
		for (Pipeline pipeline : pipelines.values()) {
			Graphics.getContext().finish();
			pipeline.getFinalStage().prepareResize();
			pipeline.getFinalStage().resize(width, height);
			Graphics.getContext().finish();
			pipeline.getFinalStage().link();
			Graphics.getContext().finish();
		}
	}

	public static Texture2D render(RenderList renderList) {
		if (pipeline == null) {
			return null;
		}

		pipeline.reset();

		cb.clear();

		pipeline.render(renderList, cb);
		return pipeline.getFinalStage().getOutput().get("result");
	}

	public static void flush() {
		if (pipeline == null) {
			return;
		}

		pipeline.updateDescriptors();

		cb.submit();
	}

	public static void dispose() {
		cb.dispose();
	}

	/**
	 * @return the pipeline
	 */
	public static Pipeline getPipeline() {
		return pipeline;
	}

	/**
	 * @param the name of the pipeline
	 */
	public static void setPipeline(String name) {
		pipeline = pipelines.get(name);
	}

	public static void addPipeline(String name, Pipeline pipeline) {
		pipelines.put(name, pipeline);
	}

	public static List<RenderImplementation.ID> getRequiredRenderImplementations() {
		return requiredRenderImplementations;
	}
}
