package de.kjEngine.scene.io;

import java.util.ArrayList;
import java.util.List;

import de.kjEngine.math.Vec3;

public class Vertex {

	public static final int NO_INDEX = -1;

	private Vec3 position;
	private int textureIndex = NO_INDEX;
	private int normalIndex = NO_INDEX;
	private Vertex duplicateVertex = null;
	private int index;
	private float length;
	private List<Vec3> tangents = new ArrayList<Vec3>();
	private Vec3 averagedTangent = Vec3.create();

	public Vertex(int index, Vec3 position) {
		this.index = index;
		this.position = position;
		this.length = position.length();
	}

	public void addTangent(Vec3 tangent) {
		tangents.add(tangent);
	}

	public void averageTangents() {
		if (tangents.isEmpty()) {
			return;
		}
		for (Vec3 tangent : tangents) {
			Vec3.add(averagedTangent, tangent, averagedTangent);
		}
		averagedTangent.normalise();
	}

	public Vec3 getAverageTangent() {
		return averagedTangent;
	}

	public int getIndex() {
		return index;
	}

	public float getLength() {
		return length;
	}

	public boolean isSet() {
		return textureIndex != NO_INDEX && normalIndex != NO_INDEX;
	}

	public boolean hasSameTextureAndNormal(int textureIndexOther, int normalIndexOther) {
		return textureIndexOther == textureIndex && normalIndexOther == normalIndex;
	}

	public void setTextureIndex(int textureIndex) {
		this.textureIndex = textureIndex;
	}

	public void setNormalIndex(int normalIndex) {
		this.normalIndex = normalIndex;
	}

	public Vec3 getPosition() {
		return position;
	}

	public int getTextureIndex() {
		return textureIndex;
	}

	public int getNormalIndex() {
		return normalIndex;
	}

	public Vertex getDuplicateVertex() {
		return duplicateVertex;
	}

	public void setDuplicateVertex(Vertex duplicateVertex) {
		this.duplicateVertex = duplicateVertex;
	}

}
