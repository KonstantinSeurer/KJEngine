/**
 * 
 */
/**
 * @author konst
 *
 */
module de.kjEngine.scene.renderer {
	exports de.kjEngine.scene.renderer;
	exports de.kjEngine.scene.renderer.deferred;
	exports de.kjEngine.scene.renderer.forward;
	exports de.kjEngine.scene.renderer.ocean;
	exports de.kjEngine.scene.renderer.raytracing;
	exports de.kjEngine.scene.renderer.shadows;
	exports de.kjEngine.scene.renderer.sph;
	
	opens de.kjEngine.scene.renderer;
	opens de.kjEngine.scene.renderer.deferred;
	opens de.kjEngine.scene.renderer.forward;
	opens de.kjEngine.scene.renderer.ocean;
	opens de.kjEngine.scene.renderer.raytracing;
	opens de.kjEngine.scene.renderer.shadows;
	opens de.kjEngine.scene.renderer.sph;
	
	requires transitive de.kjEngine.renderer;
	requires transitive de.kjEngine.graphics;
	requires transitive de.kjEngine.io;
	requires transitive de.kjEngine.scene;
	requires transitive de.kjEngine.scene.physics;
	requires transitive de.kjEngine.component;
}