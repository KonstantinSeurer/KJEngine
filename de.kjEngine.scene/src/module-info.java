/**
 * 
 */
/**
 * @author konst
 *
 */
module de.kjEngine.scene {
	exports de.kjEngine.scene.model;
	exports de.kjEngine.scene.model.procedual;
	exports de.kjEngine.scene.particles;
	exports de.kjEngine.scene.ocean;
	exports de.kjEngine.scene.light;
	exports de.kjEngine.scene;
	exports de.kjEngine.scene.animation;
	exports de.kjEngine.scene.decal;
	exports de.kjEngine.scene.ibl;
	exports de.kjEngine.scene.terrain;
	exports de.kjEngine.scene.camera;
	exports de.kjEngine.scene.io;
	exports de.kjEngine.scene.material;
	exports de.kjEngine.scene.volume;
	exports de.kjEngine.scene.atmosphere;

	opens de.kjEngine.scene.camera;
	opens de.kjEngine.scene.ocean;
	opens de.kjEngine.scene.model;

	requires java.desktop;
	requires transitive de.kjEngine.io;
	requires transitive de.kjEngine.math;
	requires transitive de.kjEngine.util;
	requires transitive de.kjEngine.graphics;
	requires transitive de.kjEngine.renderer;
	requires transitive de.kjEngine.ui;
	requires transitive de.kjEngine.component;
}