package de.kjEngine.ui.font;

import de.kjEngine.graphics.Graphics;
import de.kjEngine.graphics.SamplingMode;
import de.kjEngine.graphics.Texture2D;
import de.kjEngine.graphics.WrappingMode;
import de.kjEngine.io.RL;
import de.kjEngine.ui.model.Material;
import de.kjEngine.ui.model.Mesh;
import de.kjEngine.ui.model.Model;
import de.kjEngine.ui.model.StandartMaterial;

/**
 * Represents a font. It holds the font's texture atlas as well as having the
 * ability to create the quad vertices for any text using this font.
 * 
 * @author Karl
 *
 */
public class FontType {

	private static FontType arial;

	public static FontType getArial() {
		if (arial == null) {
			arial = new FontType(Graphics.loadTexture(new RL("jar", "ui", "de/kjEngine/ui/font/arial.png"), SamplingMode.LINEAR, WrappingMode.CLAMP, false),
					new RL("jar", "ui", "de/kjEngine/ui/font/arial.fnt"));
		}
		return arial;
	}

	public final Material textureAtlas;
	public final MetaFile data;
	private final TextMeshCreator loader;

	public FontType(Texture2D textureAtlas, RL fontFile) {
		this.textureAtlas = new StandartMaterial(textureAtlas);
		data = new MetaFile(fontFile);
		this.loader = new TextMeshCreator(data);
	}

	public Model text(String text, float size, boolean wrap, boolean centerX, boolean centerY, float width, float height) {
		Mesh mesh = loader.createTextMesh(text, size, wrap, centerX, centerY, width, height);
		if (mesh.indices.length > 0) {
			return new Model(mesh, textureAtlas);
		}
		return null;
	}
}
