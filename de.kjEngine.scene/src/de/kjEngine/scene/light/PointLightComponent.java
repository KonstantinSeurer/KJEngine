package de.kjEngine.scene.light;

import de.kjEngine.math.Vec3;
import de.kjEngine.renderer.Renderable;
import de.kjEngine.renderer.Renderable.RenderImplementation.ID;
import de.kjEngine.renderer.Renderable.RenderImplementation.Provider;
import de.kjEngine.scene.Entity;

public class PointLightComponent extends LightComponent<PointLightComponent> {
	
	public static final ID LIGHT_BUFFER = new ID(PointLightComponent.class, "light_buffer");
	static {
		Renderable.RenderImplementation.Registry.registerProvider(LIGHT_BUFFER, new Provider() {
			
			@Override
			public Renderable.RenderImplementation create() {
				return new RenderImplementation<PointLightComponent>() {
					
					@Override
					public void dispose() {
					}
					
					@Override
					public void updateDescriptors(PointLightComponent component) {
					}
					
					@Override
					public void render(PointLightComponent component) {
						component.getParent().getContainer().getRenderImplementation(SceneLightBuffer.class).getPositionalLights().add(component);
					}
					
					@Override
					public void init(PointLightComponent component) {
						Entity parent = component.getParent();
						if (!parent.isDynamic()) {
							parent.getContainer().getRenderImplementation(SceneLightBuffer.class).getStaticPositionalLights().add(component);
						}
					}
				};
			}
		});
	}
	
	public PointLightComponent() {
	}
	
	public PointLightComponent(Vec3 color) {
		super(color);
	}

	@Override
	protected void update(float delta) {
	}
}
