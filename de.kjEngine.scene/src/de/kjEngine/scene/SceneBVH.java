/**
 * 
 */
package de.kjEngine.scene;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import de.kjEngine.component.Container.RenderImplementation;
import de.kjEngine.graphics.ArrayAccessor;
import de.kjEngine.graphics.Descriptor.Type;
import de.kjEngine.graphics.DescriptorSet;
import de.kjEngine.graphics.Graphics;
import de.kjEngine.graphics.ShaderBuffer;
import de.kjEngine.graphics.StructAccessor;
import de.kjEngine.graphics.shader.BufferSource;
import de.kjEngine.graphics.shader.BufferSource.Layout;
import de.kjEngine.graphics.shader.DescriptorSetSource;
import de.kjEngine.graphics.shader.PipelineSource;
import de.kjEngine.graphics.shader.StructSource;
import de.kjEngine.graphics.shader.VariableSource;
import de.kjEngine.math.Mat4;
import de.kjEngine.math.Real;
import de.kjEngine.math.Transform;
import de.kjEngine.math.Vec3;
import de.kjEngine.math.Vec4;
import de.kjEngine.scene.model.Model;
import de.kjEngine.scene.model.ModelBVH;
import de.kjEngine.scene.model.ModelComponent;
import de.kjEngine.util.container.Array;

/**
 * @author konst
 *
 */
public class SceneBVH implements RenderImplementation<Scene> {

	public static final ID ID = new ID(Scene.class, "bvh");
	static {
		Registry.registerProvider(ID, new Provider() {

			@Override
			public de.kjEngine.renderer.Renderable.RenderImplementation create() {
				return new SceneBVH();
			}
		});

		Set<ID> dependencies = new HashSet<>();
		dependencies.add(SceneEntityList.ID);
		dependencies.add(ModelBVH.ID);
		Registry.registerDependency(ID, dependencies);
	}

	public static final StructSource NODE_SOURCE = new StructSource("Node");
	static {
		NODE_SOURCE.addMembder(new VariableSource("vec4", "min"));
		NODE_SOURCE.addMembder(new VariableSource("vec4", "max"));
		NODE_SOURCE.addMembder(new VariableSource("int", "parent"));
		NODE_SOURCE.addMembder(new VariableSource("int", "model"));
		NODE_SOURCE.addMembder(new VariableSource("int", "left"));
		NODE_SOURCE.addMembder(new VariableSource("int", "right"));
		NODE_SOURCE.addMembder(new VariableSource("vec4", "padding"));
		NODE_SOURCE.addMembder(new VariableSource("mat4", "invTransform"));
	}

	public static final int MAX_NODE_COUNT = 100000;
	public static final int MAX_MODEL_COUNT = 1000;

	public static final BufferSource BVH_SOURCE = new BufferSource("bvh", Type.STORAGE_BUFFER, Layout.STANDARD);
	static {
		BVH_SOURCE.addMember("Node[" + MAX_NODE_COUNT + "]", "nodes");
	}

	public static final DescriptorSetSource DESCRIPTOR_SET_SOURCE = new DescriptorSetSource("bvh");
	static {
		DESCRIPTOR_SET_SOURCE.addDescriptor(BVH_SOURCE);
		DESCRIPTOR_SET_SOURCE.addDescriptor(ModelBVH.SSBO_SOURCE.shallowCopy().setArrayLength(MAX_MODEL_COUNT).setName("models"));
	}

	public static final PipelineSource LIBRARY_SOURCE = new PipelineSource();
	static {
		LIBRARY_SOURCE.getStructs().add(NODE_SOURCE);
		LIBRARY_SOURCE.getStructs().add(ModelBVH.NODE_SOURCE);
		LIBRARY_SOURCE.addDescriptorSet(DESCRIPTOR_SET_SOURCE);
	}

	private static final float SQRT_3 = Real.sqrt(3f);

	private static class Node {
		Node parent, left, right;
		final Vec3 min = Vec3.scale(Float.POSITIVE_INFINITY), max = Vec3.scale(Float.NEGATIVE_INFINITY);
		int bufferIndex = -1;
		Mat4 transform;
		int index;
	}

	private Node tree;
	private ArrayList<Model> models = new ArrayList<>();
	private ArrayList<Node> bottomNodes = new ArrayList<>();
	private Node[] nodeArray = new Node[MAX_NODE_COUNT];
	private int nodeCount;

	public final ShaderBuffer ssbo;
	public final DescriptorSet descriptorSet;

	public SceneBVH() {
		ssbo = Graphics.createStorageBuffer(BVH_SOURCE, Arrays.asList(NODE_SOURCE), ShaderBuffer.FLAG_NONE);

		descriptorSet = Graphics.createDescriptorSet(DESCRIPTOR_SET_SOURCE);
		descriptorSet.set("bvh", ssbo);
		descriptorSet.update();
	}

	@Override
	public void dispose() {
	}

	@Override
	public void updateDescriptors(Scene container) {
		ArrayAccessor accessor = ssbo.getAccessor().getArray("nodes");
		for (int i = 0; i < nodeCount && i < MAX_NODE_COUNT; i++) {
			StructAccessor a = accessor.getStruct(i);
			Node n = nodeArray[i];

			a.set("min", Vec4.create(n.min, 0f));
			a.set("max", Vec4.create(n.max, 0f));

			int left = -1;
			if (n.left != null) {
				left = n.left.index;
			}
			a.seti("left", left);

			int right = -1;
			if (n.right != null) {
				right = n.right.index;
			}
			a.seti("right", right);

			a.seti("model", n.bufferIndex);

			int parent = -1;
			if (n.parent != null) {
				parent = n.parent.index;
			}
			a.seti("parent", parent);
			
			if (n.transform != null) {
				a.set("invTransform", Mat4.invert(n.transform, null));
			}
		}
		ssbo.update();

		for (int i = 0; i < models.size(); i++) {
			descriptorSet.set("models", i, models.get(i).getRenderImplementation(ModelBVH.class).ssbo);
		}
		descriptorSet.update();
	}

	private Node buildBVH(ArrayList<Node> nodes) {
		if (nodes.size() == 0) {
			return null;
		}
		if (nodes.size() == 1) {
			return nodes.get(0);
		}

		Node node = new Node();
		for (int i = 0; i < nodes.size(); i++) {
			Vec3.max(node.max, nodes.get(i).max, node.max);
			Vec3.min(node.min, nodes.get(i).min, node.min);
		}

		if (nodes.size() == 2) {
			node.left = nodes.get(0);
			nodes.get(0).parent = node;

			node.right = nodes.get(1);
			nodes.get(1).parent = node;
		} else {
			Vec3 nodeSize = Vec3.create(node.max).sub(node.min);

			ArrayList<Node> left;
			ArrayList<Node> right;

			if (nodeSize.y > nodeSize.x && nodeSize.y > nodeSize.z) {
				float center = 0f;
				for (int i = 0; i < nodes.size(); i++) {
					center += nodes.get(i).max.y - nodes.get(i).min.y;
				}
				center /= nodes.size();

				int leftCount = nodes.size() / 2;
				left = new ArrayList<>(leftCount);
				right = new ArrayList<>(nodes.size() - leftCount);

				for (int i = 0; i < nodes.size(); i++) {
					if (nodes.get(i).max.y - nodes.get(i).min.y < center) {
						left.add(nodes.get(i));
					} else {
						right.add(nodes.get(i));
					}
				}
			} else if (nodeSize.z > nodeSize.x && nodeSize.z > nodeSize.y) {
				float center = 0f;
				for (int i = 0; i < nodes.size(); i++) {
					center += nodes.get(i).max.z - nodes.get(i).min.z;
				}
				center /= nodes.size();

				int leftCount = nodes.size() / 2;
				left = new ArrayList<>(leftCount);
				right = new ArrayList<>(nodes.size() - leftCount);

				for (int i = 0; i < nodes.size(); i++) {
					if (nodes.get(i).max.z - nodes.get(i).min.z < center) {
						left.add(nodes.get(i));
					} else {
						right.add(nodes.get(i));
					}
				}
			} else {
				float center = 0f;
				for (int i = 0; i < nodes.size(); i++) {
					center += nodes.get(i).max.x - nodes.get(i).min.x;
				}
				center /= nodes.size();

				int leftCount = nodes.size() / 2;
				left = new ArrayList<>(leftCount);
				right = new ArrayList<>(nodes.size() - leftCount);

				for (int i = 0; i < nodes.size(); i++) {
					if (nodes.get(i).max.x - nodes.get(i).min.x < center) {
						left.add(nodes.get(i));
					} else {
						right.add(nodes.get(i));
					}
				}
			}

			node.left = buildBVH(left);
			node.left.parent = node;

			node.right = buildBVH(right);
			node.right.parent = node;
		}

		return node;
	}

	@Override
	public void render(Scene container) {
		Array<ModelComponent> models = container.getRenderImplementation(SceneEntityList.class).models;
		bottomNodes.clear();
		for (int i = 0; i < models.length(); i++) {
			Model model = models.get(i).getModel();
			if (!this.models.contains(model)) {
				this.models.add(model);
			}

			Node node = new Node();
			Transform transform = models.get(i).getParent().transform;
			node.bufferIndex = this.models.indexOf(model);
			node.transform = transform.globalTransform;
			Vec3 position = transform.getGlobalPosition();
			float scale = transform.scale.max() * SQRT_3;
			node.min.set(model.mesh.min).mul(scale).add(position);
			node.max.set(model.mesh.max).mul(scale).add(position);
			bottomNodes.add(node);
		}
		tree = buildBVH(bottomNodes);

		nodeCount = storeNode(tree, 0);

//		System.out.println("#####################################################################################");
//		print(tree, 0);
	}

	private int storeNode(Node node, int offset) {
		node.index = offset;
		nodeArray[offset++] = node;
		if (node.left != null) {
			offset = storeNode(node.left, offset);
		}
		if (node.right != null) {
			offset = storeNode(node.right, offset);
		}
		return offset;
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
}
