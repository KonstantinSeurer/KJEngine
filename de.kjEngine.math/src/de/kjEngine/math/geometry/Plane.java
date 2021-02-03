/**
 * 
 */
package de.kjEngine.math.geometry;

import de.kjEngine.math.Vec3;

/**
 * @author konst
 *
 */
public class Plane {
	
	public Vec3 normal;
	public float d;

	/**
	 * 
	 */
	public Plane() {
		this(Vec3.create(), 0f);
	}
	
	/**
	 * 
	 */
	public Plane(Vec3 normal, float d) {
		this.normal = normal;
		this.d = d;
	}
}
