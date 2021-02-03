/**
 * 
 */
package de.kjEngine.graphics;

import de.kjEngine.graphics.shader.Primitive;

/**
 * @author konst
 *
 */
public class VertexArrayElement {
	
	public Primitive type;
	public boolean normalized;

	public VertexArrayElement() {
		this(Primitive.VEC4, false);
	}

	public VertexArrayElement(Primitive type, boolean normalized) {
		this.type = type;
		this.normalized = normalized;
	}
}
