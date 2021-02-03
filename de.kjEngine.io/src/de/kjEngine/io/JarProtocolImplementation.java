/**
 * 
 */
package de.kjEngine.io;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import de.kjEngine.io.RL.ProtocolImplementation;

/**
 * @author konst
 *
 */
public class JarProtocolImplementation implements ProtocolImplementation {
	
	private static Map<String, Class<?>> classLoaders = new HashMap<>();
	
	public JarProtocolImplementation() {
	}

	@Override
	public InputStream openInputStream(RL rl) throws ResourceNotFoundException {
		Class<?> loader = classLoaders.get(rl.getLocation());
		if (loader == null)  {
			throw new ResourceNotFoundException("Could not find class loader \"" + rl.getLocation() + "\"!");
		}
		InputStream input = loader.getResourceAsStream("/" + rl.getPath());
		if (input == null) {
			throw new ResourceNotFoundException(rl.toString());
		}
		return input;
	} 

	@Override
	public OutputStream openOutputStream(RL rl) throws ResourceNotFoundException {
		throw new UnsupportedOperationException("The jar protocol does not support output!");
	}
	
	public static void registerClassLoader(String name, Class<?> loader) {
		classLoaders.put(name, loader);
	}
}
