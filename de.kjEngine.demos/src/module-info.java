/**
 * 
 */
/**
 * @author konst
 *
 */
module de.kjEngine.demos {
	opens de.kjEngine.demos.beach;
	opens de.kjEngine.demos.lights;
	opens de.kjEngine.demos.mars;
	opens de.kjEngine.demos.spheres;
	opens de.kjEngine.demos.animation;
	opens de.kjEngine.demos.charactermovement;
	opens de.kjEngine.demos.sponza;
	opens de.kjEngine.demos.sponza.textures_pbr;
	opens de.kjEngine.demos.ssr;
	opens de.kjEngine.demos.gltf;
	opens de.kjEngine.demos.ocean;
	opens de.kjEngine.demos.sss;
	opens de.kjEngine.demos.shadingsandbox;
	opens de.kjEngine.demos.fluidsim;
	opens de.kjEngine.demos.ui;
	opens de.kjEngine.demos.physics;

	requires de.kjEngine.core;
	requires org.json;
	requires de.kjEngine.scene;
	requires de.kjEngine.scene.renderer;
	requires de.kjEngine.ui;
	requires de.kjEngine.scene.physics;
}