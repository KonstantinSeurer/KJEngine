/**
 * 
 */
package de.kjEngine.graphics.shader;

/**
 * @author konst
 *
 */
public class VariableSource {
	
	public static VariableSource parse(String source) throws ShaderCompilationException {
		String[] pts = source.split(" ");
		if (pts.length != 2) {
			throw new IllegalArgumentException();
		}
		return new VariableSource(pts[0].trim(), pts[1].trim());
	}

	private String type;
	private String name;
	
	/**
	 * @param type
	 * @param name
	 */
	public VariableSource(String type, String name) {
		this.type = type;
		this.name = name;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
}
