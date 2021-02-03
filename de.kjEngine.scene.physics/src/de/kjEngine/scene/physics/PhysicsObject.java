/**
 * 
 */
package de.kjEngine.scene.physics;

import de.kjEngine.math.Transform;
import de.kjEngine.math.Vec3;
import de.kjEngine.scene.physics.collission.Collider;

/**
 * @author konst
 *
 */
public abstract class PhysicsObject {
	
	public Transform transform;
	public final Collider collider;
	public final boolean dynamic;
	
	public PhysicsObject(boolean dynamic, Collider collider) {
		this.dynamic = dynamic;
		this.collider = collider;
		collider.parent = this;
	}

	public PhysicsObject(boolean dynamic, Transform transform, Collider collider) {
		this.dynamic = dynamic;
		this.transform = transform;
		this.collider = collider;
		collider.parent = this;
	}

	public abstract void applyForce(Vec3 position, Vec3 force);

	public abstract void applyForce(Vec3 force);

	public abstract void accelerate(Vec3 position, Vec3 s);

	public abstract void accelerate(Vec3 s);

	public abstract void update(float delta);
}
