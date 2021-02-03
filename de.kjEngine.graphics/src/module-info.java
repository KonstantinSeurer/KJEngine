/**
 * 
 */
/**
 * @author konst
 *
 */
module de.kjEngine.graphics {
	exports de.kjEngine.graphics;
	exports de.kjEngine.graphics.khronos;
	exports de.kjEngine.graphics.vulkan;
	exports de.kjEngine.graphics.shader;
	exports de.kjEngine.graphics.shader.parser;
	exports de.kjEngine.graphics.shader.parser.spirv;
	
	opens de.kjEngine.graphics.shader;
	opens de.kjEngine.graphics.shader.parser.spirv;
	
	requires transitive de.kjEngine.math;
	requires transitive de.kjEngine.io;
	requires transitive de.kjEngine.util;
	requires de.kjEngine.thirdparty;
	
	requires transitive org.lwjgl;
	requires transitive org.lwjgl.vulkan;
	requires transitive org.lwjgl.glfw;
	requires transitive org.lwjgl.shaderc;
}