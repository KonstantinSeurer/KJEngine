/**
 * 
 */
package de.kjEngine.scene.physics.collission;

import de.kjEngine.math.Vec3;

/**
 * @author konst
 *
 */
public class Collission {

	public final Vec3 deepA = Vec3.create();
	public final Vec3 deepB = Vec3.create();
	public Collider a;
	public Collider b;
	
	public Collission() {
	}

	public void negate() {
		float x = deepA.x;
		float y = deepA.y;
		float z = deepA.z;
		deepA.set(deepB);
		deepB.x = x;
		deepB.y = y;
		deepB.z = z;
		
		Collider c = a;
		a = b;
		b = c;
	}
}
