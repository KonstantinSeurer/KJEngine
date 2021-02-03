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
public class EntityEntityMap implements RenderImplementation<Entity> {
	
	public static final ID ID = new ID(Entity.class, "entity_map");
	static {
		Renderable.RenderImplementation.Registry.registerProvider(ID, new Renderable.RenderImplementation.Provider() {

			@Override
			public RenderImplementation<Entity> create() {
				return new EntityEntityMap();
			}
		});
	}

	public EntityEntityMap() {
	}

	@Override
	public void dispose() {
	}

	@Override
	public void init(Entity e) {
		if (!e.isDynamic()) {
			if (e.has(ModelComponent.class)) {
				SceneEntityMap impl = e.getContainer().entityMapGPUImplementation;
				impl.staticEntityQueue.add(e);
			}
		}
	}

	@Override
	public void updateDescriptors(Entity e) {
	}

	@Override
	public void render(Entity e) {
		if (e.has(ModelComponent.class)) {
			SceneEntityMap impl = e.getContainer().entityMapGPUImplementation;
			impl.dynamicEntityQueue.add(e);
		}
	}
}
