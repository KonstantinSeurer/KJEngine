/**
 * 
 */
package de.kjEngine.graphics.shader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.kjEngine.io.RL;
import de.kjEngine.io.ResourceManager;

/**
 * @author konst
 *
 */
public class PipelineSource {

	private static Map<String, PipelineSource> libraries = new HashMap<>();

	public static void addLibrary(String name, PipelineSource library) {
		libraries.put(name, library);
	}
	
	public static void addLibrary(String name, RL library) {
		try {
			libraries.put(name, PipelineSource.parse(library));
		} catch (ShaderCompilationException e) {
			e.printStackTrace();
		}
	}
	
	public static PipelineSource getLibrary(String name) {
		return libraries.get(name);
	}

	static {
		addLibrary("math", RL.create("jar://graphics/de/kjEngine/graphics/shader/math.shader"));
	}

	public static PipelineSource parse(RL res) throws ShaderCompilationException {
		return parse(ResourceManager.loadTextResource(res, true));
	}

	public static PipelineSource parse(String source) throws ShaderCompilationException {
		source = ShaderCompilationUtil.removeComments(source);

		PipelineSource pipeline = new PipelineSource();

		int index = 0;
		while (index < source.length()) {
			ShaderCompilationUtil.ParseResult<ObjectSource> result = ShaderCompilationUtil.parseObject(source, index);
			index = result.endIndex + 1;
			if (result.successful) {
				if (result.data.getName().startsWith("set")) {
					pipeline.descriptorSets.add(DescriptorSetSource.parse(result.data));
				} else if (result.data.getName().startsWith("uniforms")) {
					String content = result.data.getContent();
					int startIndex = 0;
					for (int i = 0; i < content.length(); i++) {
						if (content.charAt(i) == ';') {
							String declaration = content.substring(startIndex, i).trim();
							startIndex = i + 1;
							pipeline.uniforms.add(VariableSource.parse(declaration));
						}
					}
				} else if (ShaderType.isShaderType(result.data.getName().trim())) {
					pipeline.shaders.add(ShaderSource.parse(result.data));
				} else if (result.data.getName().startsWith("struct")) {
					pipeline.structs.add(StructSource.parse(result.data));
				} else {
					pipeline.functions.add(FunctionSource.parse(result.data));
				}
			} else if (result.data != null) {
				String declaration = result.data.getName().trim();
				if (!declaration.isEmpty()) {
					if (declaration.startsWith("const")) {
						pipeline.constants.add(ConstantSource.parse(declaration));
					} else if (declaration.startsWith("include")) {
						pipeline.add(libraries.get(declaration.split(" ")[1]));
					}
				}
			}
		}

		return pipeline;
	}

	private List<DescriptorSetSource> descriptorSets;
	private List<VariableSource> uniforms;
	private List<ShaderSource> shaders;
	private List<FunctionSource> functions;
	private List<ConstantSource> constants;
	private List<StructSource> structs;

	public PipelineSource() {
		this(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
	}

	/**
	 * @param descriptorSets
	 * @param uniforms
	 * @param interfaceBlocks
	 * @param functions
	 * @param constants
	 */
	public PipelineSource(List<DescriptorSetSource> descriptorSets, List<VariableSource> uniforms, List<ShaderSource> shaders, List<FunctionSource> functions, List<ConstantSource> constants,
			List<StructSource> structs) {
		this.descriptorSets = descriptorSets;
		this.uniforms = uniforms;
		this.shaders = shaders;
		this.functions = functions;
		this.constants = constants;
		this.structs = structs;
	}

	/**
	 * @return the descriptorSets
	 */
	public List<DescriptorSetSource> getDescriptorSets() {
		return descriptorSets;
	}

	public DescriptorSetSource getDescriptorSet(String name) {
		for (int i = 0; i < descriptorSets.size(); i++) {
			if (descriptorSets.get(i).getName().equals(name)) {
				return descriptorSets.get(i);
			}
		}
		return null;
	}

	public PipelineSource addDescriptorSet(DescriptorSetSource set) {
		descriptorSets.add(set);
		return this;
	}

	/**
	 * @return the uniforms
	 */
	public List<VariableSource> getUniforms() {
		return uniforms;
	}

	/**
	 * @return the interfaceBlocks
	 */
	public List<ShaderSource> getShaders() {
		return shaders;
	}

	public boolean containsShader(ShaderType type) {
		for (int i = 0; i < shaders.size(); i++) {
			if (shaders.get(i).getType().equals(type)) {
				return true;
			}
		}
		return false;
	}

	public ShaderSource getShader(ShaderType type) {
		for (int i = 0; i < shaders.size(); i++) {
			if (shaders.get(i).getType().equals(type)) {
				return shaders.get(i);
			}
		}
		return null;
	}

	/**
	 * @return the functions
	 */
	public List<FunctionSource> getFunctions() {
		return functions;
	}

	public boolean containsFunction(String name) {
		for (int i = 0; i < functions.size(); i++) {
			if (functions.get(i).getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return the constants
	 */
	public List<ConstantSource> getConstants() {
		return constants;
	}

	/**
	 * @return the structs
	 */
	public List<StructSource> getStructs() {
		return structs;
	}

	public void add(PipelineSource source) {
		descriptorSets.addAll(source.descriptorSets);
		uniforms.addAll(source.uniforms);
		shaders.addAll(source.shaders);
		functions.addAll(source.functions);
		constants.addAll(source.constants);
		structs.addAll(source.structs);
	}

	public FunctionSource getFunction(String name, String... argumentTypes) {
		for (int i = 0; i < functions.size(); i++) {
			FunctionSource f = functions.get(i);
			if (f.getName().equals(name) && f.getArguments().size() == argumentTypes.length) {
				boolean matches = true;
				for (int j = 0; j < argumentTypes.length; i++) {
					if (!f.getArguments().get(j).getType().equals(argumentTypes[i])) {
						matches = false;
					}
				}
				if (matches) {
					return f;
				}
			}
		}
		return null;
	}
}
