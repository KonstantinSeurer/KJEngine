package de.kjEngine.scene;

import de.kjEngine.math.Vec3;

public class Entity extends TransformComponent<Entity, Entity> {

	public Entity(boolean dynamic) {
		super(dynamic);
	}

	public Entity(boolean dynamic, Vec3 position) {
		super(dynamic, position);
	}

	public Entity(boolean dynamic, Vec3 position, Vec3 scale) {
		super(dynamic, position, scale);
	}
}
