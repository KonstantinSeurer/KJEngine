/**
 * 
 */
package de.kjEngine.graphics;

import de.kjEngine.io.JarProtocolImplementation;

/**
 * @author konst
 *
 */
public class ModuleInitializer {

	public static void init() {
		JarProtocolImplementation.registerClassLoader("graphics", ModuleInitializer.class);
	}
}
