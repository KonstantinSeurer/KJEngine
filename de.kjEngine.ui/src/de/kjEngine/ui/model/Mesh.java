package de.kjEngine.ui.model;

import de.kjEngine.math.Vec2;
import de.kjEngine.math.Vec3;

public class Mesh {

	public final float[] vertices;
	public final int[] indices;
	public final float[] texCoords;
	
	public Mesh(Vec3[] vertices, Vec2[] texCoords, int[] indices) {
		float[] v = new float[vertices.length * 3];
		for (int i = 0; i < vertices.length; i++) {
			v[i * 3] = vertices[i].x;
			v[i * 3 + 1] = vertices[i].y;
			v[i * 3 + 2] = vertices[i].z;
		}
		
		float[] t = new float[texCoords.length * 2];
		for (int i = 0; i < texCoords.length; i++) {
			t[i * 2] = texCoords[i].x;
			t[i * 2 + 1] = texCoords[i].y;
		}
		
		this.vertices = v;
		this.indices = indices;
		this.texCoords = t;
	}

	public Mesh(float[] vertices, int[] indices, float[] texCoords) {
		this.vertices = vertices;
		this.indices = indices;
		this.texCoords = texCoords;
	}
}
