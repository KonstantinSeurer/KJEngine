/**
 * 
 */
package de.kjEngine.scene;

import de.kjEngine.component.Component.RenderImplementation;
import de.kjEngine.renderer.Renderable;
import de.kjEngine.scene.model.ModelComponent;

/**
 * @author konst
 *
 */
public class EntityEntityList implements RenderImplementation<Entity> {

	public static final ID ID = new ID(Entity.class, "entity_list");
	static {
		Renderable.RenderImplementation.Registry.registerProvider(ID, new Renderable.RenderImplementation.Provider() {

			@Override
			public RenderImplementation<Entity> create() {
				return new EntityEntityList();
			}
		});
	}

	public EntityEntityList() {
	}

	@Override
	public void dispose() {
	}

	@Override
	public void init(Entity e) {
		if (!e.isDynamic()) {
			SceneEntityList impl = e.getContainer().getRenderImplementation(SceneEntityList.class);
			e.getAll(ModelComponent.class, impl.staticModelQueue);
		}
	}

	@Override
	public void updateDescriptors(Entity e) {
	}

	@Override
	public void render(Entity e) {
		SceneEntityList impl = e.getContainer().getRenderImplementation(SceneEntityList.class);
		e.getAll(ModelComponent.class, impl.dynamicModelQueue);
	}
}
