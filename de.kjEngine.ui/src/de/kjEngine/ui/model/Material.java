/**
 * 
 */
package de.kjEngine.ui.model;

import de.kjEngine.renderer.Renderable;

/**
 * @author konst
 *
 */
public class Material extends Renderable<Material.RenderImplementation> {
	
	public static interface RenderImplementation extends Renderable.RenderImplementation {
		
		public void updateDescriptors(Material material);
	}

	public Material() {
	}
	
	public void updateDescriptors() {
		if (renderImplementations != null) {
			for (int i = 0; i < renderImplementations.length; i++) {
				renderImplementations[i].updateDescriptors(this);
			}
		}
	}

	@Override
	protected RenderImplementation[] createImplementationArray(int length) {
		return new RenderImplementation[length];
	}
}
