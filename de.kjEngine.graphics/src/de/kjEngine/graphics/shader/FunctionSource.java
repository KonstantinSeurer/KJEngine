/**
 * 
 */
package de.kjEngine.graphics.shader;

import java.util.ArrayList;
import java.util.List;

/**
 * @author konst
 *
 */
public class FunctionSource {
	
	public static FunctionSource parse(ObjectSource source) throws ShaderCompilationException {
		int argListStart = source.getName().indexOf('(');
		String[] pts = source.getName().substring(0, argListStart).split(" ");
		if (pts.length < 2) {
			throw new IllegalArgumentException();
		}
		String returnType = pts[0].trim();
		String name = pts[1].trim();
		
		List<ParameterSource> arguments = new ArrayList<>();
		int startIndex = argListStart + 1;
		for (int i = startIndex; i < source.getName().length(); i++) {
			char c = source.getName().charAt(i);
			if (c == ',' || c == ')') {
				String parameterSource = source.getName().substring(startIndex, i).trim();
				if (parameterSource.isEmpty()) {
					if (c == ')') {
						break;
					} else {
						throw new UnexpectedValueException(i, ",");
					}
				}
				arguments.add(ParameterSource.parse(parameterSource));
				startIndex = i + 1;
			}
		}
		
		return new FunctionSource(returnType, name, arguments, source.getContent());
	}
	
	private String returnType;
	private String name;
	private List<ParameterSource> arguments;
	private String source;
	
	/**
	 * @param returnType
	 * @param name
	 * @param arguments
	 * @param source
	 */
	public FunctionSource(String returnType, String name, List<ParameterSource> arguments, String source) {
		this.returnType = returnType;
		this.name = name;
		this.arguments = arguments;
		this.source = source;
	}
	
	public FunctionSource(FunctionSource s) {
		returnType = s.returnType;
		name = s.name;
		arguments = new ArrayList<>(s.arguments);
		source = s.source;
	}

	/**
	 * @return the returnType
	 */
	public String getReturnType() {
		return returnType;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the arguments
	 */
	public List<ParameterSource> getArguments() {
		return arguments;
	}

	/**
	 * @return the source
	 */
	public String getSource() {
		return source;
	}

	/**
	 * @param returnType the returnType to set
	 */
	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param source the source to set
	 */
	public void setSource(String source) {
		this.source = source;
	}
}
