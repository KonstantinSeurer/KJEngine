/**
 * 
 */
package de.kjEngine.io;

import java.io.IOException;

/**
 * @author konst
 *
 */
public class UnknownProtocolException extends IOException {
	private static final long serialVersionUID = -2428479892188317467L;

	public UnknownProtocolException() {
	}

	public UnknownProtocolException(String message) {
		super(message);
	}

	public UnknownProtocolException(Throwable cause) {
		super(cause);
	}

	public UnknownProtocolException(String message, Throwable cause) {
		super(message, cause);
	}
}
