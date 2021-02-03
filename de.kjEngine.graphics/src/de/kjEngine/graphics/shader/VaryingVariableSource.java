/**
 * 
 */
package de.kjEngine.graphics.shader;

import java.util.HashSet;
import java.util.Set;

import de.kjEngine.graphics.TextureFormat;

/**
 * @author konst
 *
 */
public class VaryingVariableSource extends VariableSource {
	
	public static VaryingVariableSource parse(String source) {
		String[] pts = source.split(" ");
		if (pts.length < 2 || pts.length > 4) {
			throw new IllegalArgumentException();
		}
		String type = pts[pts.length - 2].trim();
		String name = pts[pts.length - 1].trim();
		Set<String> properties = new HashSet<>();
		for (int i = 0; i < pts.length - 2; i++) {
			properties.add(pts[i].trim());
		}
		
		return new VaryingVariableSource(type, name, properties);
	}
	
	private Set<String> properties;

	/**
	 * @param type
	 * @param name
	 */
	public VaryingVariableSource(String type, String name, Set<String> properties) {
		super(type, name);
		this.properties = properties;
	}

	/**
	 * @return the properties
	 */
	public Set<String> getProperties() {
		return properties;
	}
	
	public boolean isInterpolated() {
		return !properties.contains("flat");
	}
	
	public void setInterpolated(boolean interpolated) {
		if (interpolated) {
			properties.remove("flat");
		} else {
			properties.add("flat");
		}
	}
	
	public boolean isPerspective() {
		return !properties.contains("noperspective");
	}
	
	public void setPerspective(boolean perspective) {
		if (perspective) {
			properties.remove("noperspective");
		} else {
			properties.add("noperspective");
		}
	}
	
	public boolean hasFormat() {
		for (TextureFormat format : TextureFormat.values()) {
			if (properties.contains(format.toString())) {
				return true;
			}
		}
		return false;
	}
	
	public TextureFormat getFormat() {
		for (TextureFormat format : TextureFormat.values()) {
			if (properties.contains(format.toString())) {
				return format;
			}
		}
		return TextureFormat.RGBA8;
	}
	
	public void setFormat(TextureFormat format) {
		for (TextureFormat f : TextureFormat.values()) {
			properties.remove(f.toString());
		}
		properties.add(format.toString());
	}
}
