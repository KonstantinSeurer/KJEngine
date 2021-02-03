/**
 * 
 */
package de.kjEngine.scene.material;

import de.kjEngine.io.serilization.Serializable;
import de.kjEngine.renderer.Renderable;
import de.kjEngine.util.Copy;

/**
 * @author konst
 *
 */
public abstract class Material extends Renderable<Material.RenderImplementation> implements Serializable, Copy<Material> {
	
	public static interface RenderImplementation extends Renderable.RenderImplementation {
		
		public void update(Material material);
	}
	
	public Material() {
	}

	@Override
	protected RenderImplementation[] createImplementationArray(int length) {
		return new RenderImplementation[length];
	}
}
