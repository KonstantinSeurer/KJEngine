/**
 * 
 */
package de.kjEngine.scene.physics.collission;

import de.kjEngine.math.Vec3;

/**
 * @author konst
 *
 */
public class SphereCollider extends Collider {
	
	public static final int TYPE = 1;
	
	public float radius;

	public SphereCollider(float radius) {
		super(TYPE);
		this.radius = radius;
	}

	@Override
	public void getBounds(Vec3 min, Vec3 max) {
		min.set(parent.transform.getGlobalPosition());
		max.set(min);
		min.sub(radius, radius, radius);
		max.add(radius, radius, radius);
	}
}
