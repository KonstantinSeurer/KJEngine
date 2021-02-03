package de.kjEngine.scene.model.procedual;

import java.util.ArrayList;
import java.util.List;

import de.kjEngine.math.Vec2;
import de.kjEngine.math.Vec3;
import de.kjEngine.scene.model.Mesh;

public class MeshBuilder {

	private List<Vec3> positions = new ArrayList<>();
	private List<Vec2> texCoords = new ArrayList<>();
	private List<Vec3> normals = new ArrayList<>();
	private List<Integer> indices = new ArrayList<>();

	public MeshBuilder() {
	}

	public void clear() {
		positions.clear();
		texCoords.clear();
		normals.clear();
	}

	public void appendVertex(float px, float py, float pz, float u, float v, float nx, float ny, float nz) {
		appendVertex(Vec3.create(px, py, pz), Vec2.create(u, v), Vec3.create(nx, ny, nz));
	}

	public void appendVertex(Vec3 pos, Vec2 texCoord, Vec3 normal) {
		positions.add(pos);
		texCoords.add(texCoord);
		normals.add(normal);
	}

	public void appendPoint(int i) {
		indices.add(i);
	}

	public void appendLine(int i0, int i1) {
		indices.add(i0);
		indices.add(i1);
	}

	public void appendTriangle(int i0, int i1, int i2) {
		indices.add(i0);
		indices.add(i1);
		indices.add(i2);
	}

	public void appendQuad(int i0, int i1, int i2, int i3) {
		indices.add(i0);
		indices.add(i1);
		indices.add(i2);
		indices.add(i3);
	}

	public Mesh toMesh() {
		int[] indices = new int[this.indices.size()];
		for (int i = 0; i < indices.length; i++) {
			indices[i] = this.indices.get(i);
		}
		return new Mesh(positions.toArray(new Vec3[positions.size()]), texCoords.toArray(new Vec2[texCoords.size()]), normals.toArray(new Vec3[normals.size()]), indices);
	}
}
