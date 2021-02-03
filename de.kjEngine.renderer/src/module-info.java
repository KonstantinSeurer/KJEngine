/**
 * 
 */
/**
 * @author konst
 *
 */
module de.kjEngine.renderer {
	exports de.kjEngine.renderer;
	opens de.kjEngine.renderer;
	
	requires transitive de.kjEngine.graphics;
	requires transitive de.kjEngine.util;
	requires transitive de.kjEngine.math;
	requires transitive de.kjEngine.io;
	requires transitive org.json;
}