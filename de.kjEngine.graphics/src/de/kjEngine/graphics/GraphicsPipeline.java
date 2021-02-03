/**
 * 
 */
package de.kjEngine.graphics;

import de.kjEngine.graphics.shader.PipelineSource;

/**
 * @author konst
 *
 */
public abstract class GraphicsPipeline extends Pipeline {

	public GraphicsPipeline(PipelineSource source) {
		super(source);
	}

	@Override
	public Type getType() {
		return Type.GRAPHICS;
	}
}
