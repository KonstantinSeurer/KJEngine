/**
 * 
 */
package de.kjEngine.graphics;

import de.kjEngine.graphics.shader.PipelineSource;
import de.kjEngine.util.Disposable;

/**
 * @author konst
 *
 */
public abstract class RayTracingPipeline extends Pipeline implements Disposable {

	public RayTracingPipeline(PipelineSource source) {
		super(source);
	}
}
