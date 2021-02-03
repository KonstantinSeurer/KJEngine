/**
 * 
 */
package de.kjEngine.scene.atmosphere;

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
public class ScenePbrAtmosphereList implements RenderImplementation<Scene> {
	
	public static final ID ID = new ID(Scene.class, "pbr_atmosphere_list");
	
	static {
		Registry.registerProvider(ID, new Provider() {
			
			@Override
			public de.kjEngine.renderer.Renderable.RenderImplementation create() {
				return new ScenePbrAtmosphereList();
			}
		});
		
		Set<ID> dependencies = new HashSet<>();
		dependencies.add(PbrAtmosphereComponentPbrAtmosphereList.ID);
		Registry.registerDependency(ID, dependencies);
	}
	
	public final List<PbrAtmosphereComponent> atmospheres = new ArrayList<>();

	public ScenePbrAtmosphereList() {
	}

	@Override
	public void dispose() {
	}

	@Override
	public void updateDescriptors(Scene scene) {
		atmospheres.clear();
	}

	@Override
	public void render(Scene scene) {
	}
}
