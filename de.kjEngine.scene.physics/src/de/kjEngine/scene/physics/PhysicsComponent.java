package de.kjEngine.scene.physics;

import de.kjEngine.scene.SceneComponent;
import de.kjEngine.scene.TransformComponent;

/**
 * @author konst
 *
 */
public class PhysicsComponent extends SceneComponent<TransformComponent<?, ?>, PhysicsComponent> {
	
	public final PhysicsObject object;

	public PhysicsComponent(PhysicsObject object) {
		super(EARLY);
		this.object = object;
	}

	@Override
	public void init() {
		super.init();
		object.transform = parent.transform;
	}

	@Override
	protected void update(float delta) {
		object.update(delta);
	}
}
