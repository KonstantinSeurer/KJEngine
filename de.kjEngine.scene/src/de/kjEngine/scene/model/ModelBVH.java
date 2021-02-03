/**
 * 
 */
package de.kjEngine.scene.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.kjEngine.graphics.ArrayAccessor;
import de.kjEngine.graphics.Descriptor.Type;
import de.kjEngine.graphics.Graphics;
import de.kjEngine.graphics.ShaderBuffer;
import de.kjEngine.graphics.StructAccessor;
import de.kjEngine.graphics.shader.BufferSource;
import de.kjEngine.graphics.shader.BufferSource.Layout;
import de.kjEngine.graphics.shader.StructSource;
import de.kjEngine.graphics.shader.VariableSource;
import de.kjEngine.math.Vec3;
import de.kjEngine.math.Vec4;
import de.kjEngine.scene.model.Model.RenderImplementation;

/**
 * @author konst
 *
 */
public class ModelBVH implements RenderImplementation {

	public static final ID ID = new ID(Model.class, "bvh");
	static {
		Registry.registerProvider(ID, () -> {
			return new ModelBVH();
		});
	}

	public static final int MAX_NODE_COUNT = 10000;
	public static final int MAX_VERTEX_COUNT = 10000;
	public static final int MAX_INDEX_COUNT = 10000;

	public static final StructSource NODE_SOURCE = new StructSource("BottomLevelNode");
	static {
		NODE_SOURCE.addMembder(new VariableSource("vec3", "min"));
		NODE_SOURCE.addMembder(new VariableSource("int", "left"));
		NODE_SOURCE.addMembder(new VariableSource("vec3", "max"));
		NODE_SOURCE.addMembder(new VariableSource("int", "right"));
		NODE_SOURCE.addMembder(new VariableSource("int", "parent"));
		NODE_SOURCE.addMembder(new VariableSource("int", "firstIndex"));
		NODE_SOURCE.addMembder(new VariableSource("int", "triangleCount"));
	}

	public static final BufferSource SSBO_SOURCE = new BufferSource("model", Type.STORAGE_BUFFER, Layout.STANDARD);
	static {
		SSBO_SOURCE.addMember("BottomLevelNode[" + MAX_NODE_COUNT + "]", "nodes");
		SSBO_SOURCE.addMember("int[" + MAX_INDEX_COUNT + "]", "indices");
		SSBO_SOURCE.addMember("vec4[" + MAX_VERTEX_COUNT + "]", "position_tc_x");
		SSBO_SOURCE.addMember("vec4[" + MAX_VERTEX_COUNT + "]", "normal_tc_y");
	}

	private static class Node {
		Node parent, left, right;
		final Vec3 min = Vec3.scale(Float.POSITIVE_INFINITY), max = Vec3.scale(Float.NEGATIVE_INFINITY);
		List<Triangle> triangles;
		int index;
	}

	private static class Triangle {
		private static final float ONE_THIRD = 1.0f / 3.0f;

		int i0, i1, i2;

		Triangle(int i0, int i1, int i2) {
			this.i0 = i0;
			this.i1 = i1;
			this.i2 = i2;
		}

		private float centroid(Mesh m, int axis) {
			return (m.vertices[i0 * 3 + axis] + m.vertices[i1 * 3 + axis] + m.vertices[i2 * 3 + axis]) * ONE_THIRD;
		}

		private float max(Mesh m, int axis) {
			return Math.max(Math.max(m.vertices[i0 * 3 + axis], m.vertices[i1 * 3 + axis]), m.vertices[i2 * 3 + axis]);
		}

		private float min(Mesh m, int axis) {
			return Math.min(Math.min(m.vertices[i0 * 3 + axis], m.vertices[i1 * 3 + axis]), m.vertices[i2 * 3 + axis]);
		}
	}

	public final ShaderBuffer ssbo;

	public ModelBVH() {
		ssbo = Graphics.createStorageBuffer(SSBO_SOURCE, Arrays.asList(NODE_SOURCE), ShaderBuffer.FLAG_NONE);
	}

	@Override
	public void dispose() {
		ssbo.dispose();
	}

	@Override
	public void init(Mesh mesh, float[] jointIds, float[] jointWeights) {
		ArrayList<Triangle> triangles = new ArrayList<>(mesh.indices.length / 3);
		for (int i = 0; i < mesh.indices.length / 3; i++) {
			triangles.add(new Triangle(mesh.indices[i * 3], mesh.indices[i * 3 + 1], mesh.indices[i * 3 + 2]));
		}

		Node tree = buildBVH(mesh, triangles);
//		System.out.println("###################################################################################################");
//		print(tree, 0);
		Node[] nodeArray = new Node[MAX_NODE_COUNT];
		int nodeCount = storeNode(nodeArray, tree, 0);

		ArrayAccessor indexAccessor = ssbo.getAccessor().getArray("indices");
		ArrayAccessor nodesAccessor = ssbo.getAccessor().getArray("nodes");
		int indexOffset = 0;
		for (int i = 0; i < nodeCount; i++) {
			Node n = nodeArray[i];
			StructAccessor nodeAccessor = nodesAccessor.getStruct(i);

			nodeAccessor.set("min", n.min);
			nodeAccessor.set("max", n.max);
			if (n.left != null) {
				nodeAccessor.seti("left", n.left.index);
			}
			if (n.right != null) {
				nodeAccessor.seti("right", n.right.index);
			}
			if (n.parent != null) {
				nodeAccessor.seti("parent", n.parent.index);
			}
			nodeAccessor.seti("firstIndex", indexOffset);
			if (n.triangles != null) {
				nodeAccessor.seti("triangleCount", n.triangles.size());

				for (int j = 0; j < n.triangles.size(); j++) {
					Triangle t = n.triangles.get(j);

					indexAccessor.seti(indexOffset++, t.i0);
					indexAccessor.seti(indexOffset++, t.i1);
					indexAccessor.seti(indexOffset++, t.i2);
				}
			} else {
				nodeAccessor.seti("triangleCount", 0);
			}
		}

		ArrayAccessor posTcXAccessor = ssbo.getAccessor().getArray("position_tc_x");
		ArrayAccessor normTcY = ssbo.getAccessor().getArray("normal_tc_y");
		for (int i = 0; i < mesh.texCoords.length / 2; i++) {
			posTcXAccessor.set(i, Vec4.create(mesh.vertices[i * 3], mesh.vertices[i * 3 + 1], mesh.vertices[i * 3 + 2], mesh.texCoords[i * 2]));
			normTcY.set(i, Vec4.create(mesh.normals[i * 3], mesh.normals[i * 3 + 1], mesh.normals[i * 3 + 2], mesh.texCoords[i * 2 + 1]));
		}

		ssbo.update();
	}
	
	private void print(Node node, int indentation) {
		if (node == null) {
			System.out.println("null");
			return;
		}
		String indent = "";
		for (int i = 0; i < indentation; i++) {
			indent += "    ";
		}
		System.out.println("min:" + node.min + " max: " + node.max);
		System.out.print(indent + "left ");
		print(node.left, indentation + 1);
		System.out.print(indent + "right ");
		print(node.right, indentation + 1);
	}

	private int storeNode(Node[] target, Node node, int offset) {
		node.index = offset;
		target[offset++] = node;
		if (node.left != null) {
			offset = storeNode(target, node.left, offset);
		}
		if (node.right != null) {
			offset = storeNode(target, node.right, offset);
		}
		return offset;
	}

	private Node buildBVH(Mesh m, ArrayList<Triangle> triangles) {
		Node node = new Node();

		for (int i = 0; i < triangles.size(); i++) {
			Triangle t = triangles.get(i);

			node.min.x = Math.min(node.min.x, t.min(m, 0));
			node.min.y = Math.min(node.min.y, t.min(m, 1));
			node.min.z = Math.min(node.min.z, t.min(m, 2));

			node.max.x = Math.max(node.max.x, t.max(m, 0));
			node.max.y = Math.max(node.max.y, t.max(m, 1));
			node.max.z = Math.max(node.max.z, t.max(m, 2));
		}

		if (triangles.size() > 10) {
			float lenX = node.max.x - node.min.x;
			float lenY = node.max.y - node.min.y;
			float lenZ = node.max.z - node.min.z;

			int axis = 0;
			if (lenY > lenX)
				axis = 1;
			if (lenZ > lenY && lenZ > lenX)
				axis = 2;

			float c = 0;
			for (Triangle t : triangles)
				c += t.centroid(m, axis);
			c /= triangles.size();

			ArrayList<Triangle> left = new ArrayList<>(triangles.size() / 2);
			ArrayList<Triangle> right = new ArrayList<>(triangles.size() / 2);

			for (Triangle t : triangles) {
				if (t.centroid(m, axis) < c) {
					left.add(t);
				} else {
					right.add(t);
				}
			}

			node.left = buildBVH(m, left);
			node.left.parent = node;
			node.right = buildBVH(m, right);
			node.right.parent = node;
		} else {
			node.triangles = triangles;
		}

		return node;
	}

	@Override
	public RenderImplementation deepCopy() {
		return null;
	}

	@Override
	public RenderImplementation shallowCopy() {
		return null;
	}
}
