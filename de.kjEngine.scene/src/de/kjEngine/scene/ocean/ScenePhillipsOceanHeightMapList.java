/**
 * 
 */
package de.kjEngine.scene.ocean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.kjEngine.component.Container.RenderImplementation;
import de.kjEngine.renderer.Renderable;
import de.kjEngine.scene.Scene;

/**
 * @author konst
 *
 */
public class ScenePhillipsOceanHeightMapList implements RenderImplementation<Scene> {
	
	public static final ID ID = new ID(Scene.class, "phillips_ocean_height_map_list");
	static {
		Renderable.RenderImplementation.Registry.registerProvider(ID, new Renderable.RenderImplementation.Provider() {

			@Override
			public Renderable.RenderImplementation create() {
				return new ScenePhillipsOceanHeightMapList();
			}
		});
		
		Set<ID> dependencies = new HashSet<>();
		dependencies.add(PhillipsOceanHeightMap.LIST);
		Registry.registerDependency(ID, dependencies);
	}
	
	public final List<PhillipsOceanHeightMap> heightMaps = new ArrayList<>();

	public ScenePhillipsOceanHeightMapList() {
	}

	@Override
	public void dispose() {
	}

	@Override
	public void updateDescriptors(Scene scene) {
		heightMaps.clear();
	}

	@Override
	public void render(Scene scene) {
	}
}
