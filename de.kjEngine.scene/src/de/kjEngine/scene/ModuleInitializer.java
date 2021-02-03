/**
 * 
 */
package de.kjEngine.scene;

import de.kjEngine.io.JarProtocolImplementation;

/**
 * @author konst
 *
 */
public class ModuleInitializer {

	public static void init() {
		JarProtocolImplementation.registerClassLoader("scene", ModuleInitializer.class);
	}
}
