/**
 * 
 */
package de.kjEngine.scene;

import java.util.HashSet;
import java.util.Set;

import de.kjEngine.component.Container.RenderImplementation;
import de.kjEngine.scene.model.ModelComponent;
import de.kjEngine.util.container.Array;

/**
 * @author konst
 *
 */
public class SceneEntityList implements RenderImplementation<Scene> {

	public static final ID ID = new ID(Scene.class, "entityList");
	static {
		Registry.registerProvider(ID, new Provider() {
			
			@Override
			public de.kjEngine.renderer.Renderable.RenderImplementation create() {
				return new SceneEntityList();
			}
		});
		
		Set<ID> dependencies = new HashSet<>();
		dependencies.add(EntityEntityList.ID);
		Registry.registerDependency(ID, dependencies);
	}

	public Array<ModelComponent> models = new Array<>();

	public Array<ModelComponent> staticModelQueue = new Array<>();
	public Array<ModelComponent> dynamicModelQueue = new Array<>();

	private Array<ModelComponent> staticModels = new Array<>();

	public SceneEntityList() {
	}

	@Override
	public void dispose() {
	}

	@Override
	public void updateDescriptors(Scene scene) {
	}

	@Override
	public void render(Scene scene) {
		models.clear(false);

		staticModels.add(staticModelQueue);

		models.add(staticModels);
		models.add(dynamicModelQueue);

		staticModelQueue.clear(false);
		dynamicModelQueue.clear(false);
	}
}
