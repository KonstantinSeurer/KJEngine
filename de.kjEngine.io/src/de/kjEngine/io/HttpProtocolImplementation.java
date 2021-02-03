/**
 * 
 */
package de.kjEngine.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import de.kjEngine.io.RL.ProtocolImplementation;

/**
 * @author konst
 *
 */
public class HttpProtocolImplementation implements ProtocolImplementation {

	public HttpProtocolImplementation() {
	}

	@Override
	public InputStream openInputStream(RL rl) throws ResourceNotFoundException {
		String[] pts = rl.getLocation().split(":");
		try {
			int default_port = 0;
			switch (rl.getProtocol()) {
			case "http":
				default_port = 80;
				break;
			case "https":
				default_port = 443;
				break;
			}
			URL url = new URL(rl.getProtocol(), pts[0].trim(), pts.length > 1 ? Integer.parseInt(pts[1].trim()) : default_port, "/" + rl.getPath());
			try {
				return url.openStream();
			} catch (IOException e) {
				throw new ResourceNotFoundException(e);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public OutputStream openOutputStream(RL rl) throws ResourceNotFoundException {
		throw new UnsupportedOperationException();
	}
}
