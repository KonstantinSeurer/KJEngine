package de.kjEngine.ui.font;

import java.util.ArrayList;
import java.util.List;

import de.kjEngine.io.RL;
import de.kjEngine.ui.model.Mesh;

public class TextMeshCreator {

	public static final float LINE_HEIGHT = 1f;

	private MetaFile metaData;

	protected TextMeshCreator(RL metaFile) {
		metaData = new MetaFile(metaFile);
	}

	public TextMeshCreator(MetaFile file) {
		metaData = file;
	}

	protected Mesh createTextMesh(String text, float fontSize, boolean wrap, boolean centerX, boolean centerY, float width, float height) {
		fontSize /= metaData.getMaxCharacterHeight();
		return createQuadVertices(text, fontSize, wrap, centerX, centerY, width, height);
	}

	private Mesh createQuadVertices(String text, float fontSize, boolean wrap, boolean centerX, boolean centerY, float width, float height) {
		double curserX = 0f;
		double curserY = height - LINE_HEIGHT * fontSize * metaData.getMaxCharacterHeight();
		List<Float> vertices = new ArrayList<Float>();
		List<Float> textureCoords = new ArrayList<Float>();
		float spaceSize = metaData.getSpaceWidth() * fontSize;
		char[] characters = text.toCharArray();
		for (int i = 0; i < characters.length; i++) {
			char c = characters[i];
			if (c == ' ') {
				curserX += spaceSize;
			} else if (c == '\n') {
				curserX = 0;
				curserY -= LINE_HEIGHT * fontSize * metaData.getMaxCharacterHeight();
			} else {
				CharacterData characterData = metaData.getCharacter(c);
				addVerticesForCharacter(curserX, curserY, characterData, fontSize, vertices, width, height);
				addTexCoords(textureCoords, characterData.getxTextureCoord(), 1f - characterData.getyTextureCoord(), characterData.getXMaxTextureCoord(), 1f - characterData.getYMaxTextureCoord());
				curserX += characterData.getxAdvance() * fontSize;
			}
		}

		float[] positions = listToArray(vertices);
		
		if (centerX) {
			float minX = Float.POSITIVE_INFINITY, maxX = Float.NEGATIVE_INFINITY;
			for (int i = 0; i < positions.length / 2; i++) {
				float x = positions[i * 2];
				minX = Math.min(minX, x);
				maxX = Math.max(maxX, x);
			}
			float center = minX * 0.5f + maxX * 0.5f;
			float offset = 0.5f - center;
			for (int i = 0; i < positions.length / 2; i++) {
				positions[i * 2] += offset;
			}
		}

		if (centerY) {
			float minY = Float.POSITIVE_INFINITY, maxY = Float.NEGATIVE_INFINITY;
			for (int i = 0; i < positions.length / 2; i++) {
				float y = positions[i * 2 + 1];
				minY = Math.min(minY, y);
				maxY = Math.max(maxY, y);
			}
			float center = minY * 0.5f + maxY * 0.5f;
			float offset = 0.5f - center;
			for (int i = 0; i < positions.length / 2; i++) {
				positions[i * 2 + 1] += offset;
			}
		}
		
		return new Mesh(positions, indexArray(vertices.size() / 2), listToArray(textureCoords));
	}

	private int[] indexArray(int count) {
		int[] array = new int[count];
		for (int i = 0; i < count; i++) {
			array[i] = i;
		}
		return array;
	}

	private void addVerticesForCharacter(double curserX, double curserY, CharacterData character, double fontSize, List<Float> vertices, float width, float height) {
		double x = curserX + (character.getxOffset() * fontSize);
		double y = curserY - (character.getyOffset() * fontSize);
		double maxX = x + (character.getSizeX() * fontSize);
		double maxY = y - (character.getSizeY() * fontSize);
		addVertices(vertices, x / width, (y + fontSize * metaData.getMaxCharacterHeight()) / height, maxX / width, (maxY + fontSize * metaData.getMaxCharacterHeight()) / height);
	}

	private static void addVertices(List<Float> vertices, double x, double y, double maxX, double maxY) {
		vertices.add((float) x);
		vertices.add((float) y);
		vertices.add((float) x);
		vertices.add((float) maxY);
		vertices.add((float) maxX);
		vertices.add((float) maxY);
		vertices.add((float) maxX);
		vertices.add((float) maxY);
		vertices.add((float) maxX);
		vertices.add((float) y);
		vertices.add((float) x);
		vertices.add((float) y);
	}

	private static void addTexCoords(List<Float> texCoords, double x, double y, double maxX, double maxY) {
		texCoords.add((float) x);
		texCoords.add((float) y);
		texCoords.add((float) x);
		texCoords.add((float) maxY);
		texCoords.add((float) maxX);
		texCoords.add((float) maxY);
		texCoords.add((float) maxX);
		texCoords.add((float) maxY);
		texCoords.add((float) maxX);
		texCoords.add((float) y);
		texCoords.add((float) x);
		texCoords.add((float) y);
	}

	private static float[] listToArray(List<Float> listOfFloats) {
		float[] array = new float[listOfFloats.size()];
		for (int i = 0; i < array.length; i++) {
			array[i] = listOfFloats.get(i);
		}
		return array;
	}
}
