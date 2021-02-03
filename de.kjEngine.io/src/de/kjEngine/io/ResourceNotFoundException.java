/**
 * 
 */
package de.kjEngine.io;

import java.io.IOException;

/**
 * @author konst
 *
 */
public class ResourceNotFoundException extends IOException {
	private static final long serialVersionUID = 6664535259842564941L;

	public ResourceNotFoundException() {
	}

	public ResourceNotFoundException(String message) {
		super(message);
	}

	public ResourceNotFoundException(Throwable cause) {
		super(cause);
	}

	public ResourceNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
