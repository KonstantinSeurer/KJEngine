/**
 * 
 */
package de.kjEngine.ui.model;

import de.kjEngine.graphics.Graphics;
import de.kjEngine.graphics.IndexBuffer;
import de.kjEngine.graphics.Texture2D;
import de.kjEngine.graphics.VertexArray;
import de.kjEngine.graphics.VertexArrayElement;
import de.kjEngine.graphics.VertexBuffer;
import de.kjEngine.graphics.shader.Primitive;
import de.kjEngine.ui.model.Model.RenderImplementation;

/**
 * @author konst
 *
 */
public class ModelVAO implements RenderImplementation {
	
	public static final ID ID = new ID(Model.class, "vao");
	static {
		Registry.registerProvider(ID, new Provider() {
			
			@Override
			public de.kjEngine.renderer.Renderable.RenderImplementation create() {
				return new ModelVAO();
			}
		});
	}

	public static final VertexArrayElement[] LAYOUT = new VertexArrayElement[] { new VertexArrayElement(Primitive.VEC2, false), new VertexArrayElement(Primitive.VEC2, false) };

	private VertexBuffer[] vbos;
	private IndexBuffer ibo;
	private VertexArray vao;

	public ModelVAO() {
	}

	@Override
	public void dispose() {
		vao.dispose();
		ibo.dispose();
		for (VertexBuffer b : vbos) {
			b.dispose();
		}
	}

	public VertexArray getVao() {
		return vao;
	}

	@Override
	public void init(Mesh mesh) {
		vao = Graphics.createVertexArray(LAYOUT);

		ibo = Graphics.createIndexBuffer(mesh.indices);

		vbos = new VertexBuffer[] { Graphics.createVertexBuffer(mesh.vertices), Graphics.createVertexBuffer(mesh.texCoords) };

		vao.setIndexBuffer(ibo);
		vao.setVertexBuffers(vbos);
	}
}
