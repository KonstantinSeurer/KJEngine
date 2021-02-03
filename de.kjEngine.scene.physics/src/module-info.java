/**
 * 
 */
/**
 * @author konst
 *
 */
module de.kjEngine.scene.physics {
	exports de.kjEngine.scene.physics;
	exports de.kjEngine.scene.physics.sph;
	exports de.kjEngine.scene.physics.solver;
	exports de.kjEngine.scene.physics.collission;
	
	opens de.kjEngine.scene.physics.sph;
	
	requires transitive de.kjEngine.scene;
	requires transitive de.kjEngine.math;
	requires transitive de.kjEngine.util;
}