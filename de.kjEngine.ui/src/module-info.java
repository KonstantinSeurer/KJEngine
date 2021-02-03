/**
 * 
 */
/**
 * @author konst
 *
 */
module de.kjEngine.ui {
	exports de.kjEngine.ui;
	exports de.kjEngine.ui.event;
	exports de.kjEngine.ui.font;
	exports de.kjEngine.ui.model;
	exports de.kjEngine.ui.transform;
	exports de.kjEngine.ui.renderer;
	
	opens de.kjEngine.ui.font;
	opens de.kjEngine.ui.renderer;
	
	requires transitive de.kjEngine.math;
	requires transitive de.kjEngine.graphics;
	requires transitive de.kjEngine.renderer;
	requires transitive de.kjEngine.util;
	requires transitive de.kjEngine.io;
	requires transitive de.kjEngine.component;
	
	requires org.lwjgl.glfw;
	requires org.lwjgl;
}