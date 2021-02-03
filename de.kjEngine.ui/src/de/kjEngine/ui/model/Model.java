package de.kjEngine.ui.model;

import de.kjEngine.graphics.Color;
import de.kjEngine.renderer.Renderable;
import de.kjEngine.renderer.Renderable.RenderImplementation.ID;

public class Model extends Renderable<Model.RenderImplementation> {

	public static interface RenderImplementation extends Renderable.RenderImplementation {

		public void init(Mesh mesh);
	}

	public static final ID VAO = new ID(Model.class, "vao");
	public static final ID ENTITY_MAP = new ID(Model.class, "entity_map");

	private static Model rectangle;

	public static Model getRectangle() {
		if (rectangle == null) {
			rectangle = new Model(new float[] { 0f, 0f, 1f, 0f, 1f, 1f, 0f, 1f }, new float[] { 0f, 0f, 1f, 0f, 1f, 1f, 0f, 1f }, new int[] { 0, 1, 2, 2, 3, 0 },
					new StandartMaterial(Color.BLACK.getTexture()));
		}
		return rectangle;
	}

	public Material material;
	public final Mesh mesh;

	public Model(float[] positions, float[] texCoords, int[] indices, Material material) {
		this(new Mesh(positions, indices, texCoords), material);
	}

	public Model(Mesh mesh, Material material) {
		this.material = material;
		this.mesh = mesh;

		if (renderImplementations != null) {
			for (RenderImplementation impl : renderImplementations) {
				impl.init(mesh);
			}
		}
	}

	public Model(Model m) {
		super(m.renderImplementationMap, m.renderImplementations);
		material = m.material;
		mesh = m.mesh;
	}
	
	public Model(Model m, Material material) {
		super(m.renderImplementationMap, m.renderImplementations);
		this.material = material;
		mesh = m.mesh;
	}

	public int getIndexCount() {
		return mesh.indices.length;
	}

	@Override
	protected RenderImplementation[] createImplementationArray(int length) {
		return new RenderImplementation[length];
	}
}
