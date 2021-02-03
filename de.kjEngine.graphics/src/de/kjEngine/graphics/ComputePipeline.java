/**
 * 
 */
package de.kjEngine.graphics;

import de.kjEngine.graphics.shader.PipelineSource;

/**
 * @author konst
 *
 */
public abstract class ComputePipeline extends Pipeline {

	public ComputePipeline(PipelineSource source) {
		super(source);
	}
	
	@Override
	public Type getType() {
		return Type.COMPUTE;
	}
}
