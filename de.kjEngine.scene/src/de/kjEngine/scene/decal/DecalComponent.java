/**
 * 
 */
package de.kjEngine.scene.decal;

import de.kjEngine.scene.SceneComponent;
import de.kjEngine.scene.TransformComponent;
import de.kjEngine.scene.material.Material;

/**
 * @author konst
 *
 */
public class DecalComponent extends SceneComponent<TransformComponent<?, TransformComponent<?, ?>>, DecalComponent> {

	private Material material;

	public DecalComponent() {
		super(LATE);
	}

	public DecalComponent(Material material) {
		super(LATE);
		setMaterial(material);
	}

	/**
	 * @return the material
	 */
	public Material getMaterial() {
		return material;
	}

	/**
	 * @param material the material to set
	 */
	public void setMaterial(Material material) {
		this.material = material;
	}

	@Override
	protected void update(float delta) {
	}
}
