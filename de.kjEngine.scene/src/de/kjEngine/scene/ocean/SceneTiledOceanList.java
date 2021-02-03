/**
 * 
 */
package de.kjEngine.scene.ocean;

import java.util.ArrayList;
import java.util.List;

import de.kjEngine.component.Container.RenderImplementation;
import de.kjEngine.scene.Scene;

/**
 * @author konst
 *
 */
public class SceneTiledOceanList implements RenderImplementation<Scene> {
	
	public final List<TiledOceanComponent> oceans = new ArrayList<>();

	public SceneTiledOceanList() {
	}

	@Override
	public void dispose() {
	}

	@Override
	public void updateDescriptors(Scene scene) {
		oceans.clear();
	}

	@Override
	public void render(Scene scene) {
	}
}
