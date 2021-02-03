/**
 * 
 */
package de.kjEngine.scene;

import de.kjEngine.component.Component;
import de.kjEngine.component.Component.RenderImplementation;
import de.kjEngine.renderer.Renderable;

/**
 * @author konst
 *
 */
public class EntityTransformBuffer implements Component.RenderImplementation<TransformComponent<?, ?>> {
	
	public static final ID ID = new ID(Entity.class, "transform_buffer");
	static {
		Renderable.RenderImplementation.Registry.registerProvider(ID, new Renderable.RenderImplementation.Provider() {

			@SuppressWarnings("rawtypes")
			@Override
			public RenderImplementation create() {
				return new EntityTransformBuffer();
			}
		});
	}

	public EntityTransformBuffer() {
	}

	@Override
	public void dispose() {
	}

	@Override
	public void updateDescriptors(TransformComponent<?, ?> e) {
	}

	@Override
	public void render(TransformComponent<?, ?> e) {
		Scene scene = e.getContainer();
		if (scene != null) {
			scene.transformBufferGPUImplementation.dynamicEntityQueue.add(e);
		}
	}

	@Override
	public void init(TransformComponent<?, ?> e) {
		if (!e.isDynamic()) {
			Scene scene = e.getContainer();
			if (scene != null) {
				scene.transformBufferGPUImplementation.staticEntityQueue.add(e);
			}
		}
	}
}
