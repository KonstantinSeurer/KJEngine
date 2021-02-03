/**
 * 
 */
package de.kjEngine.scene.light;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.kjEngine.component.Container.RenderImplementation;
import de.kjEngine.scene.Scene;

/**
 * @author konst
 *
 */
public class SceneShadowMapList implements RenderImplementation<Scene> {
	
	public static final ID ID  = new ID(Scene.class, "shadow_map_list");
	static  {
		Registry.registerProvider(ID, new Provider() {
			
			@Override
			public de.kjEngine.renderer.Renderable.RenderImplementation create() {
				return new SceneShadowMapList();
			}
		});
		
		Set<ID> dependencies = new HashSet<>();
		dependencies.add(ShadowMapComponentShadowMapList.ID);
		Registry.registerDependency(ID, dependencies);
	}
	
	public final List<ShadowMapComponent<?>> shadowMaps = new ArrayList<>();

	public SceneShadowMapList() {
	}

	@Override
	public void dispose() {
	}

	@Override
	public void updateDescriptors(Scene scene) {
		shadowMaps.clear();
	}

	@Override
	public void render(Scene scene) {
	}
}
