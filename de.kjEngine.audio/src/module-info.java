/**
 * 
 */
/**
 * @author konst
 *
 */
module de.kjEngine.audio {
	exports de.kjEngine.audio;
	exports de.kjEngine.audio.openal;
	exports de.kjEngine.audio.sampling;
	
	requires transitive org.lwjgl.openal;
	requires transitive de.kjEngine.util;
	requires transitive java.desktop;
	requires transitive de.kjEngine.io;
	requires transitive de.kjEngine.math;
}