package de.kjEngine.scene.model;

import de.kjEngine.math.Vec2;
import de.kjEngine.math.Vec3;

public class Mesh {

	public final float[] vertices;
	public final int[] indices;
	public final float[] texCoords;
	public final float[] normals;
	public final float maxD;
	public final Vec3 min = Vec3.scale(Float.POSITIVE_INFINITY), max = Vec3.scale(Float.NEGATIVE_INFINITY);
	
	public Mesh(Vec3[] vertices, Vec2[] texCoords, Vec3[] normals, int[] indices) {
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
		
		float[] n = new float[normals.length * 3];
		for (int i = 0; i < normals.length; i++) {
			n[i * 3] = normals[i].x;
			n[i * 3 + 1] = normals[i].y;
			n[i * 3 + 2] = normals[i].z;
		}
		
		this.vertices = v;
		this.indices = indices;
		this.texCoords = t;
		this.normals = n;

		float max = 0f;
		for (int i = 0; i < vertices.length; i++) {
			Vec3.max(this.max, vertices[i], this.max);
			Vec3.min(min, vertices[i], min);
			float lsq = vertices[i].lengthSquared();
			if (lsq > max) {
				max = lsq;
			}
		}
		maxD = (float) Math.sqrt(max);
	}

	public Mesh(float[] vertices, int[] indices, float[] texCoords, float[] normals) {
		this.vertices = vertices;
		this.indices = indices;
		this.texCoords = texCoords;
		this.normals = normals;

		Vec3 v = Vec3.create();
		float max = 0f;
		for (int i = 0; i < vertices.length; i += 3) {
			v.x = vertices[i];
			v.y = vertices[i + 1];
			v.z = vertices[i + 2];
			Vec3.max(this.max, v, this.max);
			Vec3.min(min, v, min);
			float lsq = v.lengthSquared();
			if (lsq > max) {
				max = lsq;
			}
		}
		maxD = (float) Math.sqrt(max);
	}
}
