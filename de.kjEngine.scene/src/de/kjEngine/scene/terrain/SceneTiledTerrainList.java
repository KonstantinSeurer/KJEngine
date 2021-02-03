/**
 * 
 */
package de.kjEngine.scene.terrain;

import java.util.ArrayList;
import java.util.List;

import de.kjEngine.component.Container.RenderImplementation;
import de.kjEngine.scene.Scene;

/**
 * @author konst
 *
 */
public class SceneTiledTerrainList implements RenderImplementation<Scene> {
	
	public final List<TiledTerrainComponent> terrains = new ArrayList<>();
	
	public SceneTiledTerrainList() {
	}

	@Override
	public void dispose() {
	}

	@Override
	public void updateDescriptors(Scene scene) {
		terrains.clear();
	}

	@Override
	public void render(Scene scene) {
	}
}
