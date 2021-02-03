/**
 * 
 */
package de.kjEngine.scene.physics.collission;

/**
 * @author konst
 *
 */
public interface CollissionSolver<A extends Collider, B extends Collider> {

	public boolean getCollission(A a, B b, Collission target);
}
