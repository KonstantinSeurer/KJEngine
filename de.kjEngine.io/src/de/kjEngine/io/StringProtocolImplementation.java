/**
 * 
 */
package de.kjEngine.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import de.kjEngine.io.RL.ProtocolImplementation;

/**
 * @author konst
 *
 */
public class StringProtocolImplementation implements ProtocolImplementation {
	
	private static Map<String, String> strings = new HashMap<>();
	
	public static void addString(String name, String string) {
		strings.put(name, string);
	}

	public StringProtocolImplementation() {
	}

	@Override
	public InputStream openInputStream(RL rl) throws ResourceNotFoundException {
		String string = strings.get(rl.getPath());
		if (string == null) {
			throw new ResourceNotFoundException(rl.getPath());
		}
		return new InputStream() {
			
			int i = 0;
			
			@Override
			public int read() throws IOException {
				if (i >= string.length()) {
					return -1;
				}
				return string.charAt(i);
			}
		};
	}

	@Override
	public OutputStream openOutputStream(RL rl) throws ResourceNotFoundException {
		return null;
	}
}
