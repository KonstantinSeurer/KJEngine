/**
 * 
 */
package de.kjEngine.graphics.shader;

import java.util.ArrayList;

/**
 * @author konst
 *
 */
public class ShaderSource {
	
	public static ShaderSource parse(ObjectSource source) throws ShaderCompilationException {
		InterfaceBlockSource input = null, output = null;
		FunctionSource mainFunction = null;
		
		int index = 0;
		while (index < source.getContent().length()) {
			ShaderCompilationUtil.ParseResult<ObjectSource> result = ShaderCompilationUtil.parseObject(source.getContent(), index);
			index = result.endIndex + 1;
			if (result.successful) {
				if (result.data.getName().equals("input")) {
					input = InterfaceBlockSource.parse(result.data);
				} else if (result.data.getName().equals("output")) {
					output = InterfaceBlockSource.parse(result.data);
				} else {
					FunctionSource function = FunctionSource.parse(result.data);
					if (function.getName().equals("main") && function.getArguments().isEmpty() && function.getReturnType().equals("void")) {
						mainFunction = function;
					}
				}
			}
		}
		
		if (input == null) {
			input = new InterfaceBlockSource();
		}
		if (output == null) {
			output = new InterfaceBlockSource();
		}
		if (mainFunction == null) {
			mainFunction = new FunctionSource("void", "main", new ArrayList<>(), "");
		}
		
		return new ShaderSource(ShaderType.getShaderType(source.getName().trim()), input, output, mainFunction);
	}
	
	private ShaderType type;
	private InterfaceBlockSource input, output;
	private FunctionSource mainFunction;
	
	/**
	 * @param input
	 * @param output
	 * @param mainFunction
	 */
	public ShaderSource(ShaderType type, InterfaceBlockSource input, InterfaceBlockSource output, FunctionSource mainFunction) {
		this.type = type;
		this.input = input;
		this.output = output;
		this.mainFunction = mainFunction;
	}

	/**
	 * @return the type
	 */
	public ShaderType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(ShaderType type) {
		this.type = type;
	}

	/**
	 * @return the input
	 */
	public InterfaceBlockSource getInput() {
		return input;
	}

	/**
	 * @param input the input to set
	 */
	public void setInput(InterfaceBlockSource input) {
		this.input = input;
	}

	/**
	 * @return the output
	 */
	public InterfaceBlockSource getOutput() {
		return output;
	}

	/**
	 * @param output the output to set
	 */
	public void setOutput(InterfaceBlockSource output) {
		this.output = output;
	}

	/**
	 * @return the mainFunction
	 */
	public FunctionSource getMainFunction() {
		return mainFunction;
	}

	/**
	 * @param mainFunction the mainFunction to set
	 */
	public void setMainFunction(FunctionSource mainFunction) {
		this.mainFunction = mainFunction;
	}
}
