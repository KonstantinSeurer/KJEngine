/**
 * 
 */
package de.kjEngine.graphics.shader;

/**
 * @author konst
 *
 */
public class ConstantSource extends VariableSource {
	
	public static ConstantSource parse(String source) throws ShaderCompilationException {
		String[] pts = source.split("=");
		if (pts.length != 2) {
			throw new IllegalArgumentException();
		}
		String value = pts[1].trim();
		pts = pts[0].trim().split(" ");
		if (pts.length != 3 || !"const".equals(pts[0].trim())) {
			throw new IllegalArgumentException();
		}
		return new ConstantSource(pts[1].trim(), pts[2].trim(), value);
	}
	
	private String value;

	/**
	 * @param type
	 * @param name
	 */
	public ConstantSource(String type, String name, String value) {
		super(type, name);
		this.value = value;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
}
