/**
 * 
 */
package de.kjEngine.io;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author konst
 *
 */
public class RL {

	public static RL create(String src) {
		StringBuilder protocol = new StringBuilder();
		StringBuilder location = new StringBuilder();
		StringBuilder path = new StringBuilder();
		int src_length = src.length();
		int i = 0;
		for (; i < src_length - 2; i++) {
			if (!(src.charAt(i) == ':' && src.charAt(i + 1) == '/' && src.charAt(i + 2) == '/')) {
				protocol.append(src.charAt(i));
			} else {
				i += 3;
				break;
			}
		}
		for (; i < src_length; i++) {
			if (src.charAt(i) != '/') {
				location.append(src.charAt(i));
			} else {
				i++;
				break;
			}
		}
		for (; i < src_length; i++) {
			path.append(src.charAt(i));
		}
		return new RL(protocol.toString(), location.toString(), path.toString());
	}
	
	public static interface ProtocolImplementation {
		
		public InputStream openInputStream(RL rl) throws ResourceNotFoundException;
		
		public OutputStream openOutputStream(RL rl) throws ResourceNotFoundException;
	}
	
	private static Map<String, ProtocolImplementation> implementations = new HashMap<>();
	
	public static void setProtocolImplementation(String name, ProtocolImplementation implementation) {
		implementations.put(name, implementation);
	}
	
	static {
		setProtocolImplementation("jar", new JarProtocolImplementation());
		setProtocolImplementation("file", new FileProtocolImplementation());
		setProtocolImplementation("http", new HttpProtocolImplementation());
		setProtocolImplementation("https", new HttpProtocolImplementation());
	}

	private String protocol;
	private String location;
	private String path;
	private String type = "";

	public RL(String protocol, String location, String path) {
		this.protocol = protocol;
		this.location = location;
		this.path = path;
		String[] s = path.split("\\.");
		if (s.length > 1) {
			type = s[s.length - 1];
		}
	}

	public String getProtocol() {
		return protocol;
	}

	public String getLocation() {
		return location;
	}

	public String getPath() {
		return path;
	}
	
	public String getType() {
		return type;
	}
	
	public RL getChild(String relativePath) {
		return new RL(protocol, location, path + "/" + relativePath);
	}
	
	public RL getParent() {
		int end = path.lastIndexOf("/");
		if (end == -1) {
			return new RL(protocol, location, "");
		}
		return new RL(protocol, location, path.substring(0, end));
	}

	public BufferedInputStream openInputStream() throws UnknownProtocolException, ResourceNotFoundException {
		return new BufferedInputStream(getProtocolImplementation().openInputStream(this));
	}
	
	public OutputStream openOutputStream() throws UnknownProtocolException, ResourceNotFoundException {
		return getProtocolImplementation().openOutputStream(this);
	}
	
	private ProtocolImplementation getProtocolImplementation() throws UnknownProtocolException {
		ProtocolImplementation implementation = implementations.get(protocol);
		if (implementation == null) {
			throw new UnknownProtocolException(protocol);
		}
		return implementation;
	}

	@Override
	public String toString() {
		return protocol + "://" + location + "/" + path;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result + ((protocol == null) ? 0 : protocol.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof RL))
			return false;
		RL other = (RL) obj;
		if (location == null) {
			if (other.location != null)
				return false;
		} else if (!location.equals(other.location))
			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		if (protocol == null) {
			if (other.protocol != null)
				return false;
		} else if (!protocol.equals(other.protocol))
			return false;
		return true;
	}
}
