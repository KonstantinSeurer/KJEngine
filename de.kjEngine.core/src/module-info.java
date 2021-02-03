
module de.kjEngine.core {
	exports de.kjEngine.core;

	requires transitive de.kjEngine.audio;
	requires transitive de.kjEngine.renderer;
	requires transitive de.kjEngine.ui;
	requires transitive de.kjEngine.scene;
	requires transitive de.kjEngine.graphics;
	requires transitive de.kjEngine.util;
	requires transitive org.json;
}