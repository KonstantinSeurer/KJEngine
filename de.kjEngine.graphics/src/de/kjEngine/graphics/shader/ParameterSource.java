/**
 * 
 */
package de.kjEngine.graphics.shader;

/**
 * @author konst
 *
 */
public class ParameterSource extends VariableSource {

	public static ParameterSource parse(String source) throws ShaderCompilationException {
		String[] pts = source.split(" ");
		if (pts.length < 2 || pts.length > 4) {
			throw new IllegalArgumentException(source);
		}
		boolean input = false, output = false;
		for (int i = 0; i < pts.length - 2; i++) {
			switch (pts[i].trim()) {
			case "input":
				input = true;
				break;
			case "output":
				output = true;
				break;
			default:
				throw new UnexpectedValueException(0, pts[i].trim());
			}
		}
		return new ParameterSource(pts[pts.length - 2].trim(), pts[pts.length - 1].trim(), input, output);
	}

	private boolean input, output;

	/**
	 * @param type
	 * @param input
	 * @param output
	 * @param name
	 */
	public ParameterSource(String type, String name, boolean input, boolean output) {
		super(type, name);
		this.input = input;
		this.output = output;
	}

	/**
	 * @return the input
	 */
	public boolean isInput() {
		return input;
	}

	/**
	 * @return the output
	 */
	public boolean isOutput() {
		return output;
	}

	/**
	 * @param input the input to set
	 */
	public void setInput(boolean input) {
		this.input = input;
	}

	/**
	 * @param output the output to set
	 */
	public void setOutput(boolean output) {
		this.output = output;
	}
}
