package de.kjEngine.scene.light;

import org.json.JSONObject;

import de.kjEngine.io.serilization.Serializable;
import de.kjEngine.math.Vec3;
import de.kjEngine.renderer.Renderable;
import de.kjEngine.renderer.Renderable.RenderImplementation.ID;
import de.kjEngine.renderer.Renderable.RenderImplementation.Provider;
import de.kjEngine.scene.Entity;

public class DirectionalLightComponent extends LightComponent<DirectionalLightComponent> implements Serializable {

	public static final ID LIGHT_BUFFER = new ID(DirectionalLightComponent.class, "light_buffer");
	static {
		Renderable.RenderImplementation.Registry.registerProvider(LIGHT_BUFFER, new Provider() {

			@Override
			public Renderable.RenderImplementation create() {
				return new RenderImplementation<DirectionalLightComponent>() {

					@Override
					public void dispose() {
					}

					@Override
					public void updateDescriptors(DirectionalLightComponent component) {
					}

					@Override
					public void render(DirectionalLightComponent component) {
						component.getParent().getContainer().getRenderImplementation(SceneLightBuffer.class).getDirectionalLights().add(component);
					}

					@Override
					public void init(DirectionalLightComponent component) {
						Entity parent = component.getParent();
						if (!parent.isDynamic()) {
							parent.getContainer().getRenderImplementation(SceneLightBuffer.class).getStaticDirectionalLights().add(component);
						}
					}
				};
			}
		});
	}

	public final Vec3 direction = Vec3.create();

	public DirectionalLightComponent() {
		this(Vec3.scale(1f), Vec3.create(0f, -1f, 0f));
	}

	public DirectionalLightComponent(Vec3 color, Vec3 direction) {
		super(color);
		this.direction.set(direction);
	}

	@Override
	public void deserialize(JSONObject obj) {
		if (obj.has("color")) {
			color.deserialize(obj.getJSONObject("color"));
		}
		if (obj.has("direction")) {
			direction.deserialize(obj.getJSONObject("direction"));
		}
	}

	@Override
	public JSONObject serialize() {
		return null;
	}

	@Override
	protected void update(float delta) {
	}
}
