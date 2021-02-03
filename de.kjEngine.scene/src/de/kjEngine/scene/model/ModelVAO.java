/**
 * 
 */
package de.kjEngine.scene.model;

import de.kjEngine.graphics.Graphics;
import de.kjEngine.graphics.IndexBuffer;
import de.kjEngine.graphics.VertexArray;
import de.kjEngine.graphics.VertexArrayElement;
import de.kjEngine.graphics.VertexBuffer;
import de.kjEngine.graphics.shader.Primitive;
import de.kjEngine.scene.model.Model.RenderImplementation;

/**
 * @author konst
 *
 */
public class ModelVAO implements RenderImplementation {

	public static final VertexArrayElement[] LAYOUT = new VertexArrayElement[] { new VertexArrayElement(Primitive.VEC3, false), new VertexArrayElement(Primitive.VEC2, false),
			new VertexArrayElement(Primitive.VEC3, false), new VertexArrayElement(Primitive.VEC4, false), new VertexArrayElement(Primitive.VEC4, false) };

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
	public void init(Mesh mesh, float[] jointIds, float[] jointWeights) {
		vao = Graphics.createVertexArray(LAYOUT);

		ibo = Graphics.createIndexBuffer(mesh.indices);

		VertexBuffer pos_vbo = Graphics.createVertexBuffer(mesh.vertices);
		VertexBuffer tex_vbo = Graphics.createVertexBuffer(mesh.texCoords);
		VertexBuffer norm_vbo = Graphics.createVertexBuffer(mesh.normals);

		int vertexCount = mesh.vertices.length / 3;

		int transformCount = 1;

		VertexBuffer joint_id_vbo;
		if (jointIds != null) {
			joint_id_vbo = Graphics.createVertexBuffer(jointIds);
			for (float i : jointIds) {
				transformCount = Math.max(transformCount, (int) i);
			}
			transformCount++;
		} else {
			joint_id_vbo = Graphics.createVertexBuffer(new float[vertexCount * 4]);
		}

		VertexBuffer joint_weight_vbo;
		if (jointWeights != null) {
			joint_weight_vbo = Graphics.createVertexBuffer(jointWeights);
		} else {

			jointWeights = new float[vertexCount * 4];
			for (int i = 0; i < vertexCount; i++) {
				int base = i * 4;
				jointWeights[base] = 1f;
			}
			joint_weight_vbo = Graphics.createVertexBuffer(jointWeights);
		}

		vbos = new VertexBuffer[] { pos_vbo, tex_vbo, norm_vbo, joint_id_vbo, joint_weight_vbo };

		vao.setIndexBuffer(ibo);
		vao.setVertexBuffers(vbos);
	}

	@Override
	public RenderImplementation deepCopy() {
		return null;
	}

	@Override
	public RenderImplementation shallowCopy() {
		ModelVAO m = new ModelVAO();
		m.vao = vao;
		m.vbos = vbos;
		m.ibo = ibo;
		return m;
	}
}
