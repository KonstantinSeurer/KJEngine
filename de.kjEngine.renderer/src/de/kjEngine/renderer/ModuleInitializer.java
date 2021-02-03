/**
 * 
 */
package de.kjEngine.renderer;

import de.kjEngine.io.JarProtocolImplementation;

/**
 * @author konst
 *
 */
public class ModuleInitializer {

	public static void init() {
		JarProtocolImplementation.registerClassLoader("renderer", ModuleInitializer.class);
	}
}
