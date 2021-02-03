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
public class ResourceLookupProtocolImplementation implements ProtocolImplementation {
	
	private static Map<String, ResourceLookup> lookups = new HashMap<>();

	public ResourceLookupProtocolImplementation() {
	}

	@Override
	public InputStream openInputStream(RL rl) throws ResourceNotFoundException {
		try {
			return getRL(rl).openInputStream();
		} catch (UnknownProtocolException e) {
			throw new ResourceNotFoundException(e);
		}
	}

	@Override
	public OutputStream openOutputStream(RL rl) throws ResourceNotFoundException {
		try {
			return getRL(rl).openOutputStream();
		} catch (UnknownProtocolException e) {
			throw new ResourceNotFoundException(e);
		}
	}
	
	private RL getRL(RL rl) throws ResourceNotFoundException {
		ResourceLookup lookup = lookups.get(rl.getLocation());
		if (lookup == null) {
			throw new ResourceNotFoundException();
		}
		return lookup.getResource(rl.getPath());
	}
}
