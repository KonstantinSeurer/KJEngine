package de.kjEngine.scene.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import de.kjEngine.graphics.Color;
import de.kjEngine.io.RL;
import de.kjEngine.math.Vec2;
import de.kjEngine.math.Vec3;
import de.kjEngine.scene.material.PbrMaterial;
import de.kjEngine.scene.model.Mesh;
import de.kjEngine.scene.model.Model;

public class OBJFileLoader {

	private static final String SPACE = "\\s+";

	public static List<Model> loadOBJ(RL rl) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(rl.openInputStream()));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		List<String> lines = new ArrayList<>();

		try {
			String line = null;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (!line.isEmpty() && !line.startsWith("#")) {
					lines.add(line);
				}
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		List<Vec3> vertices = new ArrayList<>();
		List<Vec2> textures = new ArrayList<>();
		List<Vec3> normals = new ArrayList<>();
		List<String> mtlLibs = new ArrayList<>();

		for (String line : lines) {
			String tag_name;
			String value;
			{
				String[] pts = line.split(" ");
				if (pts.length < 2) {
					System.out.println("Invalid line: " + line);
					continue;
				}
				tag_name = pts[0];
				StringBuilder sb = new StringBuilder(pts.length << 2);
				for (int i = 1; i < pts.length; i++) {
					sb.append(pts[i]);
					if (i < pts.length - 1) {
						sb.append(" ");
					}
				}
				value = sb.toString();
			}

			if ("v".equals(tag_name)) {
				String[] currentLine = line.split(SPACE);
				Vec3 vertex = Vec3.create((float) Float.valueOf(currentLine[1]), (float) Float.valueOf(currentLine[2]), (float) Float.valueOf(currentLine[3]));
				vertices.add(vertex);

			} else if ("vt".equals(tag_name)) {
				String[] currentLine = line.split(SPACE);
				Vec2 texture = Vec2.create((float) Float.valueOf(currentLine[1]), 1f - (float) Float.valueOf(currentLine[2]));
				textures.add(texture);
			} else if ("vn".equals(tag_name)) {
				String[] currentLine = line.split(SPACE);
				Vec3 normal = Vec3.create((float) Float.valueOf(currentLine[1]), (float) Float.valueOf(currentLine[2]), (float) Float.valueOf(currentLine[3]));
				normals.add(normal);
			} else if ("mtllib".equals(tag_name)) {
				mtlLibs.add(value);
			}
		}

		String mtl = "None";

		List<Model> objects = new ArrayList<>();
		List<MTLFile> mtls = new ArrayList<>();

		for (String mat : mtlLibs) {
			MTLFile mtlLib = MTLFile.load(rl.getParent().getChild(mat));
			if (mtlLib != null) {
				mtls.add(mtlLib);
			}
		}

		for (int i = 0; i < lines.size(); i++) {
			if (lines.get(i).startsWith("f")) {
				List<Integer> indices = new ArrayList<Integer>();
				List<Vertex> localVertices = new ArrayList<>();
				for (; i < lines.size(); i++) {
					if (lines.get(i).startsWith("f")) {
						String[] currentLine = lines.get(i).split(SPACE);
						String[] vertex1 = currentLine[1].split("/");
						String[] vertex2 = currentLine[2].split("/");
						String[] vertex3 = currentLine[3].split("/");
						processVertex(vertex1, vertices, localVertices, indices);
						processVertex(vertex2, vertices, localVertices, indices);
						processVertex(vertex3, vertices, localVertices, indices);
					} else {
						i--;
						break;
					}
				}
				float[] verticesArray = new float[localVertices.size() * 3];
				float[] texturesArray = new float[localVertices.size() * 2];
				float[] normalsArray = new float[localVertices.size() * 3];
				convertDataToArrays(localVertices, textures, normals, verticesArray, texturesArray, normalsArray);
				int[] indicesArray = convertIndicesListToArray(indices);
				Mesh mesh = new Mesh(verticesArray, indicesArray, texturesArray, normalsArray);

				PbrMaterial material = new PbrMaterial(Color.GRAY_07);

				for (MTLFile file : mtls) {
					if (file.getMaterials().containsKey(mtl)) {
						material = file.getMaterials().get(mtl).material;
						break;
					}
				}

				Model o = new Model(mesh, material);
				objects.add(o);
			} else if (lines.get(i).startsWith("usemtl")) {
				mtl = lines.get(i).split(" ")[1];
			}
		}

		vertices.clear();
		textures.clear();
		normals.clear();

		return objects;
	}

	private static void processVertex(String[] vertex, List<Vec3> vertices, List<Vertex> localVertices, List<Integer> indices) {
		int index = Integer.parseInt(vertex[0]) - 1;
		Vertex currentVertex = new Vertex(index + 1, vertices.get(index));
		int textureIndex;
		if (vertex[1].isEmpty()) {
			textureIndex = 0;
		} else {
			textureIndex = Integer.parseInt(vertex[1]) - 1;
		}
		int normalIndex = Integer.parseInt(vertex[2]) - 1;
		currentVertex.setTextureIndex(textureIndex);
		currentVertex.setNormalIndex(normalIndex);
		indices.add(localVertices.size());
		localVertices.add(currentVertex);
	}

	private static int[] convertIndicesListToArray(List<Integer> indices) {
		int[] indicesArray = new int[indices.size()];
		for (int i = 0; i < indicesArray.length; i++) {
			indicesArray[i] = indices.get(i);
		}
		return indicesArray;
	}

	private static void convertDataToArrays(List<Vertex> vertices, List<Vec2> textures, List<Vec3> normals, float[] verticesArray, float[] texturesArray, float[] normalsArray) {
		for (int i = 0; i < vertices.size(); i++) {
			Vertex currentVertex = vertices.get(i);
			Vec3 position = currentVertex.getPosition();
			Vec2 textureCoord;
			if (textures.size() > currentVertex.getTextureIndex() && currentVertex.getTextureIndex() != Vertex.NO_INDEX) {
				textureCoord = textures.get(currentVertex.getTextureIndex());
			} else {
				textureCoord = Vec2.create();
			}
			Vec3 normalVector;
			if (normals.size() > currentVertex.getNormalIndex() && currentVertex.getNormalIndex() != Vertex.NO_INDEX) {
				normalVector = normals.get(currentVertex.getNormalIndex());
			} else {
				normalVector = Vec3.create(0.0f, 1.0f, 0.0f);
			}
			verticesArray[i * 3] = position.x;
			verticesArray[i * 3 + 1] = position.y;
			verticesArray[i * 3 + 2] = position.z;
			texturesArray[i * 2] = textureCoord.x;
			texturesArray[i * 2 + 1] = 1 - textureCoord.y;
			normalsArray[i * 3] = normalVector.x;
			normalsArray[i * 3 + 1] = normalVector.y;
			normalsArray[i * 3 + 2] = normalVector.z;

		}
	}
}