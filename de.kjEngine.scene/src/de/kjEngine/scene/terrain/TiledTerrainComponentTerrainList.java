/**
 * 
 */
package de.kjEngine.scene.terrain;

import de.kjEngine.component.Component.RenderImplementation;

/**
 * @author konst
 *
 */
public class TiledTerrainComponentTerrainList implements RenderImplementation<TiledTerrainComponent> {

	public TiledTerrainComponentTerrainList() {
	}

	@Override
	public void dispose() {
	}

	@Override
	public void init(TiledTerrainComponent c) {
	}

	@Override
	public void updateDescriptors(TiledTerrainComponent c) {
	}

	@Override
	public void render(TiledTerrainComponent c) {
		c.getParent().getContainer().getRenderImplementation(SceneTiledTerrainList.class).terrains.add(c);
	}
}
