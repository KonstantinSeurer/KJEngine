package de.kjEngine.scene.model;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.kjEngine.io.RL;
import de.kjEngine.renderer.Renderable;
import de.kjEngine.renderer.Renderable.RenderImplementation.ID;
import de.kjEngine.scene.io.OBJFileLoader;
import de.kjEngine.scene.material.Material;
import de.kjEngine.scene.material.PbrMaterial;
import de.kjEngine.util.Copy;
import de.kjEngine.util.Timer;

public class Model extends Renderable<Model.RenderImplementation> {

	public static interface RenderImplementation extends Renderable.RenderImplementation, Copy<RenderImplementation> {

		public void init(Mesh mesh, float[] jointIds, float[] jointWeights);
	}

	public static final ID VAO = new ID(Model.class, "vao");
	public static final ID ENTITY_MAP = new ID(Model.class, "entity_map");

	static {
		Renderable.RenderImplementation.Registry.registerProvider(VAO, new Renderable.RenderImplementation.Provider() {

			@Override
			public RenderImplementation create() {
				return new ModelVAO();
			}
		});
		Renderable.RenderImplementation.Registry.registerProvider(ENTITY_MAP, new Renderable.RenderImplementation.Provider() {

			@Override
			public RenderImplementation create() {
				return new ModelEntityMap();
			}
		});
	}
	
	private static Map<RL, List<Model>> models = new HashMap<>();
	
	public static List<Model> loadModel(RL rl, boolean cached) {
		if (cached) {
			List<Model> model = models.get(rl);
			if (model == null) {
				model = loadModel(rl, false);
				models.put(rl, model);
			}
			return model;
		} else {
			switch (rl.getType()) {
			case "obj":
				return OBJFileLoader.loadOBJ(rl);
			}
			return null;
		}
	}

	private static Model defaultSphere;

	public static Model getDefaultSphere() {
		if (defaultSphere == null) {
			defaultSphere = Model.loadModel(new RL("jar", "scene", "de/kjEngine/scene/model/sphere.obj"), true).get(0);
		}
		return defaultSphere;
	}

	private static Model defaultCube;

	public static Model getDefaultCube() {
		if (defaultCube == null) {
			defaultCube = Model.loadModel(new RL("jar", "scene", "de/kjEngine/scene/model/cube.obj"), true).get(0);
		}
		return defaultCube;
	}

	private static Model defaultPlane;

	public static Model getDefaultPlane() {
		if (defaultPlane == null) {
			defaultPlane = new Model(new float[] { -1f, 0f, 1f, -1f, 0f, -1f, 1f, 0f, -1f, 1f, 0f, 1f }, new float[] { 0f, 1f, 0f, 0f, 1f, 0f, 1f, 1f },
					new float[] { 0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f, 0f }, new int[] { 0, 1, 2, 2, 3, 0 }, PbrMaterial.getNullMaterial());
		}
		return defaultPlane;
	}

	private static Model defaultCircle;

	public static Model getDefaultCircle() {
		if (defaultCircle == null) {
			defaultCircle = loadModel(new RL("jar", "scene", "de/kjEngine/scene/model/circle.obj"), true).get(0);
		}
		return defaultCircle;
	}

	private static Model defaultCylinder;

	public static Model getDefaultCylinder() {
		if (defaultCylinder == null) {
			defaultCylinder = loadModel(new RL("jar", "scene", "de/kjEngine/scene/model/cylinder.obj"), true).get(0);
		}
		return defaultCylinder;
	}

	private static Model defaultCone;

	public static Model getDefaultCone() {
		if (defaultCone == null) {
			defaultCone = loadModel(new RL("jar", "scene", "de/kjEngine/scene/model/cone.obj"), true).get(0);
		}
		return defaultCone;
	}

	private static Model defaultBillboard;

	public static Model getDefaultBillboard() {
		if (defaultBillboard == null) {
			defaultBillboard = loadModel(new RL("jar", "scene", "de/kjEngine/scene/model/billboard.obj"), true).get(0);
		}
		return defaultBillboard;
	}

	private static Model defaultQuat2D;

	public static Model getDefaultQuat2D() {
		if (defaultQuat2D == null) {
			defaultQuat2D = new Model(new float[] { 0f, 0f, 0f, 1f, 0f, 0f, 1f, 1f, 0f, 0f, 1f, 0f }, new float[] { 0f, 1f, 1f, 1f, 1f, 0f, 0f, 0f },
					new float[] { 0f, 0f, -1f, 0f, 0f, -1f, 0f, 0f, -1f, 0f, 0f, -1f }, new int[] { 0, 3, 2, 2, 1, 0 }, PbrMaterial.getNullMaterial());
		}
		return defaultQuat2D;
	}

	private static Model defaultParticle;

	public static Model getDefaultParticle() {
		if (defaultParticle == null) {
			defaultParticle = new Model(new float[] { -1f, -1f, 0f, 1f, -1f, 0f, 1f, 1f, 0f, -1f, 1f, 0f }, new float[] { 0f, 1f, 1f, 1f, 1f, 0f, 0f, 0f },
					new float[] { 0f, 0f, -1f, 0f, 0f, -1f, 0f, 0f, -1f, 0f, 0f, -1f }, new int[] { 0, 1, 2, 2, 3, 0 }, PbrMaterial.getNullMaterial());
		}
		return defaultParticle;
	}

	private Material material;
	public final Mesh mesh;
	private float[] jointIds;
	private float[] jointWeights;

	private int transformCount;

	private int hash;

	// for fast access
	public ModelEntityMap entityMapImplementation;

	public Model(float[] positions, float[] texCoords, float[] normals, int[] indices, Material material) {
		this(new Mesh(positions, indices, texCoords, normals), null, null, material);
	}

	public Model(Mesh mesh, Material material) {
		this(mesh, null, null, material);
	}

	public Model(float[] positions, float[] texCoords, float[] normals, float[] jointIds, float[] jointWeights, int[] indices, Material material) {
		this(new Mesh(positions, indices, texCoords, normals), jointIds, jointWeights, material);
	}

	public Model(Mesh mesh, float[] jointIds, float[] jointWeights, Material material) {
		this.material = material;
		this.mesh = mesh;
		this.jointIds = jointIds;
		this.jointWeights = jointWeights;

		transformCount = 1;

		if (jointIds != null) {
			for (float i : jointIds) {
				transformCount = Math.max(transformCount, (int) i);
			}
			transformCount++;
		}

		if (renderImplementations != null) {
			for (RenderImplementation impl : renderImplementations) {
				impl.init(mesh, jointIds, jointWeights);
			}
		}

		entityMapImplementation = getRenderImplementation(ModelEntityMap.class);

		updateHashCode();
	}

	/**
	 * @param model
	 */
	public Model(Model m) {
		super(new HashMap<>(m.renderImplementationMap.size()), new RenderImplementation[m.renderImplementations.length]);
		
		material = m.material;
		mesh = m.mesh;
		transformCount = m.transformCount;
		jointIds = m.jointIds;
		jointWeights = m.jointWeights;
		
		int i = 0;
		for (ID id : m.renderImplementationMap.keySet()) {
			RenderImplementation impl = m.renderImplementationMap.get(id).shallowCopy();
			renderImplementationMap.put(id, impl);
			renderImplementations[i++] = impl;
		}

		entityMapImplementation = getRenderImplementation(ModelEntityMap.class);

		updateHashCode();
	}

	public int getIndexCount() {
		return mesh.indices.length;
	}

	public Material getMaterial() {
		return material;
	}

	public Model setMaterial(Material material) {
		this.material = material;
		updateHashCode();
		return this;
	}

	/**
	 * @return the transformCount
	 */
	public int getTransformCount() {
		return transformCount;
	}

	public static Model[] combine(List<Model> models) {
		return combine(models.toArray(new Model[models.size()]));
	}

	public static Model[] combine(Model... models) {
		Timer.start();

		Map<Material, List<Mesh>> map = new HashMap<>();
		for (Model m : models) {
			Material mat = m.getMaterial();
			List<Mesh> batch = map.get(mat);
			if (batch == null) {
				batch = new ArrayList<>();
				map.put(mat, batch);
			}
			batch.add(m.mesh);
		}

		Model[] result = new Model[map.size()];
		int i = 0;
		for (Material mat : map.keySet()) {
			List<Mesh> meshes = map.get(mat);

			int vertexCount = 0;
			int indexCount = 0;
			for (Mesh mesh : meshes) {
				vertexCount += mesh.vertices.length / 3;
				indexCount += mesh.indices.length;
			}

			FloatBuffer vertexBuffer = FloatBuffer.allocate(vertexCount * 3);
			FloatBuffer texCoordBuffer = FloatBuffer.allocate(vertexCount * 2);
			FloatBuffer normalBuffer = FloatBuffer.allocate(vertexCount * 3);
			IntBuffer indexBuffer = IntBuffer.allocate(indexCount);

			int off = 0;

			for (Mesh mesh : meshes) {
				vertexBuffer.put(mesh.vertices);
				texCoordBuffer.put(mesh.texCoords);
				normalBuffer.put(mesh.normals);

				for (int index : mesh.indices) {
					indexBuffer.put(index + off);
				}

				off += mesh.vertices.length / 3;
			}

			Mesh mesh = new Mesh(vertexBuffer.array(), indexBuffer.array(), texCoordBuffer.array(), normalBuffer.array());

			result[i++] = new Model(mesh, mat);
		}

		return result;
	}

	private void updateHashCode() {
		final int prime = 31;
		hash = 1;
		hash = prime * hash + ((material == null) ? 0 : material.hashCode());
		hash = prime * hash + ((mesh == null) ? 0 : mesh.hashCode());
		hash = prime * hash + transformCount;
	}

	@Override
	public int hashCode() {
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Model other = (Model) obj;
		if (material == null) {
			if (other.material != null)
				return false;
		} else if (!material.equals(other.material))
			return false;
		if (mesh == null) {
			if (other.mesh != null)
				return false;
		} else if (!mesh.equals(other.mesh))
			return false;
		if (transformCount != other.transformCount)
			return false;
		return true;
	}

	@Override
	protected RenderImplementation[] createImplementationArray(int length) {
		return new RenderImplementation[length];
	}
}
