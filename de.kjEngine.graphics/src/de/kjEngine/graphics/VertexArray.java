/**
 * 
 */
package de.kjEngine.graphics;

import de.kjEngine.util.Disposable;

/**
 * @author konst
 *
 */
public abstract class VertexArray implements Disposable {

	public abstract void setIndexBuffer(IndexBuffer ib);

	public abstract void setVertexBuffers(VertexBuffer[] vbs);

	public abstract int getVertexBufferCount();
}
