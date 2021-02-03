package de.kjEngine.scene.light;

import de.kjEngine.math.Vec3;
import de.kjEngine.scene.Entity;
import de.kjEngine.scene.SceneComponent;

public abstract class LightComponent<ComponentType extends LightComponent<?>> extends SceneComponent<Entity, ComponentType> {

	public final Vec3 color = Vec3.create();

	public LightComponent() {
		super(LATE);
	}

	public LightComponent(Vec3 color) {
		super(LATE);
		this.color.set(color);
	}
}
