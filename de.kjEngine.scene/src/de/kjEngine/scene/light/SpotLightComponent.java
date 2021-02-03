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
public class SpotLightComponent extends LightComponent<SpotLightComponent> {
	
	public static final ID LIGHT_BUFFER = new ID(SpotLightComponent.class, "light_buffer");
	static {
		Renderable.RenderImplementation.Registry.registerProvider(LIGHT_BUFFER, new Provider() {
			
			@Override
			public Renderable.RenderImplementation create() {
				return new RenderImplementation<SpotLightComponent>() {
					
					@Override
					public void dispose() {
					}
					
					@Override
					public void updateDescriptors(SpotLightComponent component) {
					}
					
					@Override
					public void render(SpotLightComponent component) {
						component.getContainer().getRenderImplementation(SceneLightBuffer.class).getSpotLights().add(component);
					}
					
					@Override
					public void init(SpotLightComponent component) {
						Entity parent = component.getParent();
						if (!parent.isDynamic() && parent.getContainer() != null) {
							parent.getContainer().getRenderImplementation(SceneLightBuffer.class).getStaticSpotLights().add(component);
						}
					}
				};
			}
		});
	}
	
	public final Vec3 direction = Vec3.create();
	public float angle = 1f;
	public float angularFalloff = 1f;

	public SpotLightComponent(Vec3 color, Vec3 direction, float angle, float angularFalloff) {
		super(color);
		this.direction.set(direction);
		this.angle = angle;
		this.angularFalloff = angularFalloff;
	}

	@Override
	protected void update(float delta) {
	}
}
