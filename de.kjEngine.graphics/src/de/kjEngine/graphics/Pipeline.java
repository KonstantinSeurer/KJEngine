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
public abstract class Pipeline implements Disposable {
	
	protected final PipelineSource source;

	public static enum Type {
		GRAPHICS, COMPUTE, RAYTRACING
	}
	
	public Pipeline(PipelineSource source) {
		this.source = source;
	}

	/**
	 * @return the source
	 */
	public PipelineSource getSource() {
		return source;
	}

	public abstract StructAccessor getUniformAccessor();
	
	public abstract Type getType();
}
