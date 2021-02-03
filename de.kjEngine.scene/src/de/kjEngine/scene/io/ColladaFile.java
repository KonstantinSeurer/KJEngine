/**
 * 
 */
package de.kjEngine.scene.io;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.kjEngine.io.RL;
import de.kjEngine.io.xml.XmlNode;
import de.kjEngine.io.xml.XmlParser;
import de.kjEngine.math.Mat4;
import de.kjEngine.math.Real;
import de.kjEngine.math.Transform;
import de.kjEngine.math.Vec3;
import de.kjEngine.scene.animation.Skeleton;
import de.kjEngine.scene.animation.Skeleton.Joint;
import de.kjEngine.scene.animation.TransformAnimation;
import de.kjEngine.scene.material.PbrMaterial;
import de.kjEngine.scene.model.Model;

/**
 * @author konst
 *
 */
public class ColladaFile {

	private static class Vertex {
		int pos, tc, normal;

		Vertex(int pos, int tc, int normal) {
			this.pos = pos;
			this.tc = tc;
			this.normal = normal;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + normal;
			result = prime * result + pos;
			result = prime * result + tc;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Vertex other = (Vertex) obj;
			if (normal != other.normal)
				return false;
			if (pos != other.pos)
				return false;
			if (tc != other.tc)
				return false;
			return true;
		}
	}

	private static final Mat4 CORRECTION = new Mat4().rotate(-Real.HALF_PI, Vec3.X);

	public static class ModelEntry {

		private Model model;
		private Skeleton skeleton;

		/**
		 * @param model
		 * @param skeleton
		 */
		public ModelEntry(Model model, Skeleton skeleton) {
			this.model = model;
			this.skeleton = skeleton;
		}

		/**
		 * @return the model
		 */
		public Model getModel() {
			return model;
		}

		/**
		 * @return the skeleton
		 */
		public Skeleton getSkeleton() {
			return skeleton;
		}
	}

	private List<ModelEntry> models = new ArrayList<>();
	private Map<String, TransformAnimation> animations = new HashMap<>();

	public ColladaFile(RL rl) {
		XmlNode node = XmlParser.loadXmlFile(rl);
		XmlNode geometries = node.getChild("library_geometries");
		XmlNode controllers = node.getChild("library_controllers");
		XmlNode visualScene = node.getChild("library_visual_scenes").getChild("visual_scene");
		XmlNode animations = node.getChild("library_animations");

		boolean zUp = node.getChild("asset").getChild("up_axis").getData().equals("Z_UP");

		for (XmlNode geometry : geometries.getChildren("geometry")) {
			String id = geometry.getAttribute("id");

			XmlNode mesh = geometry.getChild("mesh");

			XmlNode polylist = mesh.getChild("polylist");

			float[] positions = null;
			float[] texCoords = null;
			float[] normals = null;

			int vertexOffset = 0, tcOffset = 0, normalOffset = 0;

			List<XmlNode> inputs = polylist.getChildren("input");

			for (XmlNode input : inputs) {
				String semantic = input.getAttribute("semantic");

				String source = input.getAttribute("source").substring(1);
				if ("VERTEX".equals(semantic)) {
					XmlNode vertices_node = mesh.getChildWithAttribute("vertices", "id", source);
					XmlNode vertices_input = vertices_node.getChildWithAttribute("input", "semantic", "POSITION");
					source = vertices_input.getAttribute("source").substring(1);
				}

				XmlNode source_node = mesh.getChildWithAttribute("source", "id", source);
				XmlNode array = source_node.getChild("float_array");

				float[] data = new float[Integer.parseInt(array.getAttribute("count"))];
				String[] array_data = array.getData().split(" ");

				if (data.length != array_data.length) {
					System.err.println("Count does not match content!");
				}

				for (int i = 0; i < data.length && i < array_data.length; i++) {
					data[i] = Float.parseFloat(array_data[i]);
				}

				int offset = Integer.parseInt(input.getAttribute("offset"));

				switch (semantic) {
				case "VERTEX":
					positions = data;
					vertexOffset = offset;
					break;
				case "TEXCOORD":
					texCoords = data;
					tcOffset = offset;
					break;
				case "NORMAL":
					normals = data;
					normalOffset = offset;
					break;
				}
			}

			int[] joints = null;
			float[] weights = null;

			String[] jointNames = null;

			String controllerName = null;

			for (XmlNode controller : controllers.getChildren("controller")) {
				XmlNode skin = controller.getChild("skin");
				if (skin == null) {
					continue;
				}
				if (skin.getAttribute("source").equals("#" + id)) {
					controllerName = controller.getAttribute("name");

					XmlNode vertexWeights = skin.getChild("vertex_weights");

					XmlNode weightInput = vertexWeights.getChildWithAttribute("input", "semantic", "WEIGHT");
					String weightSource = weightInput.getAttribute("source").substring(1);
					String[] weightSourceArray = skin.getChildWithAttribute("source", "id", weightSource).getChild("float_array").getData().split(" ");

					float[] weightBuffer = new float[weightSourceArray.length];
					for (int i = 0; i < weightBuffer.length; i++) {
						weightBuffer[i] = Float.parseFloat(weightSourceArray[i]);
					}

					int weightOffset = Integer.parseInt(weightInput.getAttribute("offset"));
					int jointOffset = Integer.parseInt(vertexWeights.getChildWithAttribute("input", "semantic", "JOINT").getAttribute("offset"));

					int inputCount = vertexWeights.getChildren("input").size();

					String[] weightCountSource = vertexWeights.getChild("vcount").getData().split(" ");
					int[] weightCounts = new int[weightCountSource.length];
					for (int i = 0; i < weightCounts.length; i++) {
						weightCounts[i] = Integer.parseInt(weightCountSource[i]);
					}

					joints = new int[weightCountSource.length * 4];
					weights = new float[weightCountSource.length * 4];

					String[] weightIndexSource = vertexWeights.getChild("v").getData().split(" ");
					int[] weightIndices = new int[weightIndexSource.length];
					for (int i = 0; i < weightIndices.length; i++) {
						weightIndices[i] = Integer.parseInt(weightIndexSource[i]);
					}

					XmlNode jointInput = skin.getChild("joints").getChildWithAttribute("input", "semantic", "JOINT");
					String jointSource = jointInput.getAttribute("source").substring(1);
					jointNames = skin.getChildWithAttribute("source", "id", jointSource).getChild("Name_array").getData().split(" ");

					int i = 0;
					int offset = 0;

					List<Float> allWeights = new ArrayList<>();
					List<Integer> allJoints = new ArrayList<>();

					for (int count : weightCounts) {
						if (count <= 4) {
							for (int j = 0; j < count; j++) {
								int base = offset * inputCount;
								int index = i * 4 + j;

								weights[index] = weightBuffer[weightIndices[base + weightOffset]];
								joints[index] = weightIndices[base + jointOffset];

								offset++;
							}
							i++;
						} else {
							allWeights.clear();
							allJoints.clear();

							for (int j = 0; j < count; j++) {
								int base = offset * inputCount;

								allWeights.add(weightBuffer[weightIndices[base + weightOffset]]);
								allJoints.add(weightIndices[base + jointOffset]);

								offset++;
							}

							int base_index = i * 4;

							for (int j = 0; j < 4; j++) {
								int index = base_index + j;

								float maxWeight = 0f;
								int maxJoint = 0;
								int maxK = 0;

								for (int k = 0; k < allWeights.size(); k++) {
									float weight = allWeights.get(k);
									if (weight > maxWeight) {
										maxWeight = weight;
										maxJoint = allJoints.get(k);
										maxK = k;
									}
								}

								weights[index] = maxWeight;
								joints[index] = maxJoint;

								allWeights.remove(maxK);
								allJoints.remove(maxK);
							}

							float sum = 0f;
							for (int j = 0; j < 4; j++) {
								sum += weights[base_index + j];
							}

							float inverseSum = 1f / sum;

							for (int j = 0; j < 4; j++) {
								weights[base_index + j] *= inverseSum;
							}

							i++;
						}
					}

					break;
				}
			}

			String[] index_strings = polylist.getChild("p").getData().split(" ");
			int[] indices = new int[index_strings.length];
			for (int i = 0; i < indices.length; i++) {
				indices[i] = Integer.parseInt(index_strings[i]);
			}

			int indexCount = indices.length / inputs.size();

			List<Vertex> vertices = new ArrayList<>();

			int[] actualIndices = new int[indexCount];

			List<Float> actualPositions = new ArrayList<>();
			List<Float> actualTexCoords = new ArrayList<>();
			List<Float> actualNormals = new ArrayList<>();
			List<Float> actualWeights = new ArrayList<>();
			List<Integer> actualJoints = new ArrayList<>();

			for (int i = 0; i < indexCount; i++) {
				int base = i * inputs.size();

				int vertex = base + vertexOffset;
				int texCoord = base + tcOffset;
				int normal = base + normalOffset;

				Vertex v = new Vertex(indices[vertex], indices[texCoord], indices[normal]);

				if (!vertices.contains(v)) {
					actualIndices[i] = vertices.size();

					vertices.add(v);

					if (zUp) {
						actualPositions.add(positions[v.pos * 3]);
						actualPositions.add(positions[v.pos * 3 + 2]);
						actualPositions.add(-positions[v.pos * 3 + 1]);

						actualTexCoords.add(texCoords[v.tc * 2]);
						actualTexCoords.add(1f - texCoords[v.tc * 2 + 1]);

						actualNormals.add(normals[v.normal * 3]);
						actualNormals.add(normals[v.normal * 3 + 2]);
						actualNormals.add(-normals[v.normal * 3 + 1]);

						actualWeights.add(weights[v.pos * 4]);
						actualWeights.add(weights[v.pos * 4 + 1]);
						actualWeights.add(weights[v.pos * 4 + 2]);
						actualWeights.add(weights[v.pos * 4 + 3]);

						actualJoints.add(joints[v.pos * 4]);
						actualJoints.add(joints[v.pos * 4 + 1]);
						actualJoints.add(joints[v.pos * 4 + 2]);
						actualJoints.add(joints[v.pos * 4 + 3]);
					} else {
						actualPositions.add(positions[v.pos * 3]);
						actualPositions.add(positions[v.pos * 3 + 1]);
						actualPositions.add(positions[v.pos * 3 + 2]);

						actualTexCoords.add(texCoords[v.tc * 2]);
						actualTexCoords.add(texCoords[v.tc * 2 + 1]);

						actualNormals.add(normals[v.normal * 3]);
						actualNormals.add(normals[v.normal * 3 + 1]);
						actualNormals.add(normals[v.normal * 3 + 2]);
					}
				} else {
					actualIndices[i] = vertices.indexOf(v);
				}
			}

			float[] actualPositionArray = new float[actualPositions.size()];
			for (int i = 0; i < actualPositionArray.length; i++) {
				actualPositionArray[i] = actualPositions.get(i);
			}

			float[] actualTexCoordArray = new float[actualTexCoords.size()];
			for (int i = 0; i < actualTexCoordArray.length; i++) {
				actualTexCoordArray[i] = actualTexCoords.get(i);
			}

			float[] actualNormalArray = new float[actualNormals.size()];
			for (int i = 0; i < actualNormalArray.length; i++) {
				actualNormalArray[i] = actualNormals.get(i);
			}

			float[] actualWeightArray = new float[actualWeights.size()];
			for (int i = 0; i < actualWeightArray.length; i++) {
				actualWeightArray[i] = actualWeights.get(i);
			}

			float[] actualJointArray = new float[actualJoints.size()];
			for (int i = 0; i < actualJointArray.length; i++) {
				actualJointArray[i] = actualJoints.get(i);
			}

			Model model = new Model(actualPositionArray, actualTexCoordArray, actualNormalArray, actualJointArray, actualWeightArray, actualIndices, PbrMaterial.getNullMaterial());

			XmlNode amature = visualScene.getChildWithAttribute("node", "name", controllerName);
			Joint root = loadJoints(amature, zUp, true).get(0);

			Skeleton skeleton = new Skeleton(root, jointNames);

			ModelEntry entry = new ModelEntry(model, skeleton);

			models.add(entry);
		}

		for (XmlNode animation : animations.getChildren("animation")) {
			XmlNode channel = animation.getChild("channel");
			XmlNode sampler = animation.getChildWithAttribute("sampler", "id", channel.getAttribute("source").substring(1));
			String joint = channel.getAttribute("target").split("/")[0];

			String inputSource = sampler.getChildWithAttribute("input", "semantic", "INPUT").getAttribute("source").substring(1);
			String outputSource = sampler.getChildWithAttribute("input", "semantic", "OUTPUT").getAttribute("source").substring(1);

			String[] inputSourceArray = animation.getChildWithAttribute("source", "id", inputSource).getChild("float_array").getData().split(" ");
			float[] inputs = new float[inputSourceArray.length];
			for (int i = 0; i < inputs.length; i++) {
				inputs[i] = Float.parseFloat(inputSourceArray[i]);
			}
			float[] frameLengths = new float[inputs.length];
			for (int i = 0; i < frameLengths.length - 1; i++) {
				frameLengths[i] = inputs[i + 1] - inputs[i];
			}

			String[] outputSourceArray = animation.getChildWithAttribute("source", "id", outputSource).getChild("float_array").getData().split(" ");
			Transform[] transforms = new Transform[inputs.length];
			float[] matrixBuffer = new float[16];
			for (int i = 0; i < transforms.length; i++) {
				int baseIndex = i * 16;
				for (int j = 0; j < 16; j++) {
					matrixBuffer[j] = Float.parseFloat(outputSourceArray[baseIndex + j]);
				}
				Mat4 matrix = new Mat4();
				setMatrix(matrixBuffer, matrix);
				matrix.transpose();
				if (zUp) {
					for (ModelEntry e : models) {
						if (e.skeleton.getRoot().getName().equals(joint)) {
							Mat4.mul(CORRECTION, matrix, matrix);
							break;
						}
					}
				}
				transforms[i] = new Transform(matrix);
			}

			this.animations.put(joint, new TransformAnimation(transforms, frameLengths));
		}
	}

	private List<Joint> loadJoints(XmlNode node, boolean zUp, boolean root) {
		List<Joint> joints = new ArrayList<>();

		for (XmlNode jointNode : node.getChildrenWithAttribute("node", "type", "JOINT")) {
			XmlNode initialTransformNode = jointNode.getChildWithAttribute("matrix", "sid", "transform");
			String[] initialTransformSource = initialTransformNode.getData().split(" ");
			float[] initialTransformData = new float[initialTransformSource.length];
			for (int i = 0; i < initialTransformData.length; i++) {
				initialTransformData[i] = Float.parseFloat(initialTransformSource[i]);
			}

			Mat4 initialTransform = new Mat4();
			setMatrix(initialTransformData, initialTransform);
			initialTransform.transpose();
			
			if (root && zUp) {
				Mat4.mul(CORRECTION, initialTransform, initialTransform);
			}

			Joint joint = new Joint(jointNode.getAttribute("id"), initialTransform);

			List<Joint> children = loadJoints(jointNode, zUp, false);
			for (int i = 0; i < children.size(); i++) {
				joint.addJoint(children.get(i));
			}

			joints.add(joint);
		}

		return joints;
	}
	
	private static void setMatrix(float[] f, Mat4 target) {
		target.xx = f[0];
		target.xy = f[1];
		target.xz = f[2];
		target.xw = f[3];
		
		target.xx = f[4];
		target.xy = f[5];
		target.xz = f[6];
		target.xw = f[7];
		
		target.xx = f[8];
		target.xy = f[9];
		target.xz = f[10];
		target.xw = f[11];
		
		target.xx = f[12];
		target.xy = f[13];
		target.xz = f[14];
		target.xw = f[15];
	}

	/**
	 * @return the models
	 */
	public List<ModelEntry> getModels() {
		return models;
	}

	/**
	 * @return the animations
	 */
	public Map<String, TransformAnimation> getAnimations() {
		return animations;
	}
}
