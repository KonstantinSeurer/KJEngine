/**
 * 
 */
package de.kjEngine.scene.physics.sph;

import de.kjEngine.math.Vec3;

/**
 * @author konst
 *
 */
public class SPHParticle {

	public Vec3 position;
	public Vec3 velocity;

	public SPHParticle() {
		position = Vec3.create();
		velocity = Vec3.create();
	}

	public SPHParticle(Vec3 position, Vec3 velocity) {
		this.position = position;
		this.velocity = velocity;
	}
}
