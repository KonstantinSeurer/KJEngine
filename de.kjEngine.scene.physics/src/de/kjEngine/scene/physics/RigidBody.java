/**
 * 
 */
package de.kjEngine.scene.physics;

import de.kjEngine.math.Vec3;
import de.kjEngine.scene.physics.collission.Collider;

/**
 * @author konst
 *
 */
public class RigidBody extends PhysicsObject {

	public final Vec3 velocity = Vec3.create();
	public final Vec3 angularVelocity = Vec3.create();
	public final float mass;
	public final float friction;
	public final Collider collider;

	public RigidBody(boolean dynamic, float mass, float friction, Collider collider) {
		super(dynamic, collider);
		if (dynamic) {
			this.mass = mass;
		} else {
			this.mass = Float.POSITIVE_INFINITY;
		}
		this.friction = friction;
		this.collider = collider;
	}

	@Override
	public void applyForce(Vec3 position, Vec3 force) {
	}

	@Override
	public void applyForce(Vec3 force) {
		if (dynamic) {
			velocity.add(force, 1f / mass);
		}
	}

	@Override
	public void accelerate(Vec3 position, Vec3 a) {
	}

	@Override
	public void accelerate(Vec3 a) {
		if (dynamic) {
			velocity.add(a);
		}
	}

	public Vec3 getVelocity(Vec3 globalPosition) {
		if (!dynamic) {
			return Vec3.create();
		}
		Vec3 r = Vec3.create(globalPosition).sub(transform.getGlobalPosition());
		return Vec3.create(velocity).add(Vec3.cross(angularVelocity, r, null));
	}

	@Override
	public void update(float delta) {
		if (dynamic) {
			transform.position.add(velocity, delta);
			float angularVelocityMag = angularVelocity.length();
			if (angularVelocityMag > 0.0001f) {
				Vec3 axis = Vec3.create(angularVelocity).normalise();
				transform.rotation.rotate(axis, angularVelocityMag * delta);
			}
		}
	}
}
