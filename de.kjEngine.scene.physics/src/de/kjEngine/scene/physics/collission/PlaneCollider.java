/**
 * 
 */
package de.kjEngine.scene.physics.collission;

import de.kjEngine.math.Vec3;

/**
 * @author konst
 *
 */
public class PlaneCollider extends Collider {
	
	public static final int TYPE = 0;
	
	public PlaneCollider() {
		super(TYPE);
	}

	@Override
	public void getBounds(Vec3 min, Vec3 max) {
		min.set(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);
		max.set(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
	}
}
