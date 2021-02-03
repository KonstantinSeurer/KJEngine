/**
 * 
 */
package de.kjEngine.editor;

import java.util.Set;

import de.kjEngine.graphics.CommandBuffer;
import de.kjEngine.renderer.Pipeline;
import de.kjEngine.renderer.PrepassStage;
import de.kjEngine.renderer.RenderList;
import de.kjEngine.renderer.Renderable.RenderImplementation.ID;

/**
 * @author konst
 *
 */
public class SceneRenderer implements PrepassStage {
	
	public final Pipeline pipeline;
	
	public SceneRenderer(Pipeline pipeline) {
		this.pipeline = pipeline;
	}

	@Override
	public void dispose() {
	}

	@Override
	public void updateDescriptors() {
		pipeline.updateDescriptors();
	}

	@Override
	public void render(RenderList renderList, CommandBuffer cb) {
		pipeline.reset();
		pipeline.render(renderList, cb);
	}

	@Override
	public void getRequiredRenderImplementations(Set<ID> target) {
		pipeline.getRequiredRenderImplementations(target);
	}
}
