/**
 * 
 */
package de.kjEngine.scene.light;

import de.kjEngine.math.Vec3;
import de.kjEngine.renderer.Renderable;
import de.kjEngine.renderer.Renderable.RenderImplementation.ID;
import de.kjEngine.renderer.Renderable.RenderImplementation.Provider;
import de.kjEngine.scene.Entity;

/**
 * @author konst
 *
 */
public class SphereLightComponent extends LightComponent<SphereLightComponent> {

	public static final ID LIGHT_BUFFER = new ID(SphereLightComponent.class, "light_buffer");
	static {
		Renderable.RenderImplementation.Registry.registerProvider(LIGHT_BUFFER, new Provider() {

			@Override
			public Renderable.RenderImplementation create() {
				return new RenderImplementation<SphereLightComponent>() {

					@Override
					public void dispose() {
					}

					@Override
					public void updateDescriptors(SphereLightComponent component) {
					}

					@Override
					public void render(SphereLightComponent component) {
						component.getParent().getContainer().getRenderImplementation(SceneLightBuffer.class).getSphereLights().add(component);
					}

					@Override
					public void init(SphereLightComponent component) {
						Entity parent = component.getParent();
						if (!parent.isDynamic()) {
							parent.getContainer().getRenderImplementation(SceneLightBuffer.class).getStaticSphereLights().add(component);
						}
					}
				};
			}
		});
	}

	public float radius = 1f;

	public SphereLightComponent(Vec3 color, float radius) {
		super(color);
		this.radius = radius;
	}

	@Override
	protected void update(float delta) {
	}
}
