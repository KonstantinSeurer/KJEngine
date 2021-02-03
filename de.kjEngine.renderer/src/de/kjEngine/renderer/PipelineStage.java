/**
 * 
 */
package de.kjEngine.renderer;

import java.util.Set;

import de.kjEngine.graphics.CommandBuffer;
import de.kjEngine.graphics.Texture2D;
import de.kjEngine.renderer.Renderable.RenderImplementation.ID;

/**
 * @author konst
 *
 */
public class PipelineStage extends Stage {
	
	public static final String OUTPUT_TEXTURE_RESULT_NAME = "result";
	
	private Pipeline pipeline;
	
	public PipelineStage(Pipeline pipeline) {
		super(null);
		this.pipeline = pipeline;
		input = new InputProvider() {
			
			@Override
			public void reset() {
				PipelineStage.this.pipeline.reset();
			}
			
			@Override
			public void render(RenderList renderList, CommandBuffer cb) {
				PipelineStage.this.pipeline.render(renderList, cb);
			}
			
			@Override
			public void link() {
				PipelineStage.this.pipeline.getFinalStage().link();
			}
			
			@Override
			public Texture2D get(String name) {
				return null;
			}

			@Override
			public void updateDescriptors() {
				PipelineStage.this.pipeline.updateDescriptors();
			}

			@Override
			public void prepareResize() {
				PipelineStage.this.pipeline.getFinalStage().prepareResize();
			}

			@Override
			public void resize(int width, int height) {
				PipelineStage.this.pipeline.getFinalStage().resize(width, height);
			}
		};
	}

	@Override
	public void dispose() {
		pipeline.getFinalStage().dispose();
	}

	@Override
	protected void renderImplementation(RenderList renderList, CommandBuffer cb) {
	}

	@Override
	protected void linkImplementation() {
		output.put(OUTPUT_TEXTURE_RESULT_NAME, pipeline.getFinalStage().getOutput().get("result"));
	}

	/**
	 * @return the pipeline
	 */
	public Pipeline getPipeline() {
		return pipeline;
	}

	@Override
	protected void updateDescriptorsImplementation() {
	}

	@Override
	public void getRequiredRenderImplementations(Set<ID> target) {
		pipeline.getRequiredRenderImplementations(target);
	}

	@Override
	protected void resizeImplementation(int width, int height) {
	}
}
