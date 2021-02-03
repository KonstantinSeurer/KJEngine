/**
 * 
 */
package de.kjEngine.ui.model;

import de.kjEngine.graphics.Texture2D;

/**
 * @author konst
 *
 */
public class StandartMaterial extends Material {
	
	private Texture2D texture;

	public StandartMaterial() {
	}
	
	public StandartMaterial(Texture2D texture) {
		setTexture(texture);
	}

	public Texture2D getTexture() {
		return texture;
	}

	public StandartMaterial setTexture(Texture2D texture) {
		this.texture = texture;
		updateDescriptors();
		return this;
	}
}
