/**
 * 
 */
package de.kjEngine.graphics;

import de.kjEngine.graphics.shader.PrimitiveType;
import de.kjEngine.util.Disposable;

/**
 * @author konst
 *
 */
public abstract class BottomLevelAccelerationStructure implements Disposable {
	
	protected final VertexBuffer vertexBuffer;
	protected final IndexBuffer indexBuffer;
	protected final PrimitiveType topology;
	
	/**
	 * @param vertexBuffer
	 * @param indexBuffer
	 */
	public BottomLevelAccelerationStructure(VertexBuffer vertexBuffer, IndexBuffer indexBuffer, PrimitiveType topology) {
		this.vertexBuffer = vertexBuffer;
		this.indexBuffer = indexBuffer;
		this.topology = topology;
	}

	/**
	 * @return the vertexBuffer
	 */
	public VertexBuffer getVertexBuffer() {
		return vertexBuffer;
	}

	/**
	 * @return the indexBuffer
	 */
	public IndexBuffer getIndexBuffer() {
		return indexBuffer;
	}

	/**
	 * @return the topology
	 */
	public PrimitiveType getTopology() {
		return topology;
	}
	
	public abstract void update();
}
