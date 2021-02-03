/**
 * 
 */
package de.kjEngine.scene.ocean;

import de.kjEngine.component.Component.RenderImplementation;

/**
 * @author konst
 *
 */
public class TiledOceanComponentOceanList implements RenderImplementation<TiledOceanComponent> {

	public TiledOceanComponentOceanList() {
	}

	@Override
	public void dispose() {
	}

	@Override
	public void init(TiledOceanComponent c) {
	}

	@Override
	public void updateDescriptors(TiledOceanComponent c) {
	}

	@Override
	public void render(TiledOceanComponent c) {
		c.getParent().getContainer().getRenderImplementation(SceneTiledOceanList.class).oceans.add(c);
	}
}
