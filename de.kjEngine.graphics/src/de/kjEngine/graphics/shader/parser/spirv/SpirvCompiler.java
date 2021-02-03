/**
 * 
 */
package de.kjEngine.graphics.shader.parser.spirv;

import static de.kjEngine.graphics.vulkan.VulkanUtil.*;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

import org.lwjgl.util.shaderc.Shaderc;

import de.kjEngine.graphics.Descriptor;
import de.kjEngine.graphics.shader.BufferSource;
import de.kjEngine.graphics.shader.ConstantSource;
import de.kjEngine.graphics.shader.DescriptorSetSource;
import de.kjEngine.graphics.shader.DescriptorSource;
import de.kjEngine.graphics.shader.FunctionSource;
import de.kjEngine.graphics.shader.ImageSource;
import de.kjEngine.graphics.shader.InterfaceBlockSource;
import de.kjEngine.graphics.shader.ParameterSource;
import de.kjEngine.graphics.shader.PipelineSource;
import de.kjEngine.graphics.shader.PrimitiveType;
import de.kjEngine.graphics.shader.ShaderSource;
import de.kjEngine.graphics.shader.ShaderType;
import de.kjEngine.graphics.shader.StructSource;
import de.kjEngine.graphics.shader.TesselationSpacing;
import de.kjEngine.graphics.shader.TextureSource;
import de.kjEngine.graphics.shader.VariableSource;
import de.kjEngine.graphics.shader.VaryingVariableSource;
import de.kjEngine.graphics.shader.WindingOrder;
import de.kjEngine.util.container.Array;

/**
 * @author konst
 *
 */
public class SpirvCompiler {

	public static void appendInterfaceBlockSource(StringBuilder sb, InterfaceBlockSource interfaceSource, boolean input, boolean array) {
		for (int i = 0; i < interfaceSource.getVariables().size(); i++) {
			sb.append("layout (location = ");
			sb.append(i);
			if (input) {
				sb.append(") in ");
			} else {
				sb.append(") out ");
			}
			sb.append(interfaceSource.getVariables().get(i).getType());
			if (input) {
				sb.append(" input_");
			} else {
				sb.append(" output_");
			}
			sb.append(interfaceSource.getVariables().get(i).getName());
			if (array) {
				sb.append("[]");
			}
			sb.append(";\n");
		}
	}

	public static void appendFunctionSource(StringBuilder sb, FunctionSource function, InterfaceBlockSource input, InterfaceBlockSource output, List<DescriptorSetSource> descriptorSets) {
		sb.append(function.getReturnType());
		sb.append(" ");
		sb.append(function.getName());
		sb.append("(");
		for (int i = 0; i < function.getArguments().size(); i++) {
			ParameterSource arg = function.getArguments().get(i);
			if (arg.isInput()) {
				if (arg.isOutput()) {
					sb.append("inout ");
				} else {
					sb.append("in ");
				}
			} else if (arg.isOutput()) {
				sb.append("out ");
			}
			sb.append(arg.getType());
			sb.append(" ");
			sb.append(arg.getName());
			if (i < function.getArguments().size() - 1) {
				sb.append(", ");
			}
		}
		sb.append(") {\n");
		String functionSource = function.getSource();
		for (VaryingVariableSource variable : input.getVariables()) {
			functionSource = functionSource.replaceAll("input\\." + variable.getName(), "input_" + variable.getName());
		}
		for (VaryingVariableSource variable : output.getVariables()) {
			functionSource = functionSource.replaceAll("output\\." + variable.getName(), "output_" + variable.getName());
		}
		for (DescriptorSetSource descriptorSet : descriptorSets) {
			for (DescriptorSource descriptor : descriptorSet.getDescriptors()) {
				functionSource = functionSource.replaceAll(descriptorSet.getName() + "\\." + descriptor.getName(), descriptorSet.getName() + "_" + descriptor.getName());
			}
		}
		sb.append(functionSource);
		sb.append("}\n");
	}

	public static void appendStructSource(StringBuilder sb, StructSource struct) {
		sb.append("struct ");
		sb.append(struct.getName());
		sb.append(" {\n");
		for (VariableSource member : struct.getMembers()) {
			appendVariableSource(sb, member);
		}
		sb.append("};\n");
	}

	public static void appendBufferSource(StringBuilder sb, BufferSource descriptor, String setName, int set, int binding) {
		sb.append("layout (set = ");
		sb.append(set);
		sb.append(", binding = ");
		sb.append(binding);
		sb.append(", ");
		switch (descriptor.getLayout()) {
		case PACKED:
			sb.append("std430) ");
			break;
		case STANDARD:
			sb.append("std140) ");
			break;
		}
		if (descriptor.getType().equals(Descriptor.Type.UNIFORM_BUFFER)) {
			sb.append("uniform UBO_");
		} else {
			sb.append("buffer SSBO_");
		}
		sb.append(set);
		sb.append("_");
		sb.append(binding);
		sb.append(" {\n");
		for (VariableSource member : descriptor.getMembers()) {
			appendVariableSource(sb, member);
		}
		sb.append("} ");
		sb.append(setName);
		sb.append("_");
		sb.append(descriptor.getName());
		if (descriptor.isArray()) {
			sb.append("[");
			sb.append(descriptor.getArrayLength());
			sb.append("]");
		}
		sb.append(";\n");
	}

	public static void appendTextureSource(StringBuilder sb, TextureSource descriptor, String setName, int set, int binding) {
		sb.append("layout (set = ");
		sb.append(set);
		sb.append(", binding = ");
		sb.append(binding);
		sb.append(") uniform sampler");
		sb.append(descriptor.getDimensions());
		sb.append("D");
		if (descriptor.isArray()) {
			sb.append("[");
			sb.append(descriptor.getArrayLength());
			sb.append("]");
		}
		sb.append(" ");
		sb.append(setName);
		sb.append("_");
		sb.append(descriptor.getName());
		sb.append(";\n");
	}

	public static void appendImageSource(StringBuilder sb, ImageSource descriptor, String setName, int set, int binding) {
		sb.append("layout (set = ");
		sb.append(set);
		sb.append(", binding = ");
		sb.append(binding);
		sb.append(", ");
		switch (getSupportedFormat(descriptor.getFormat())) {
		case R16F:
			sb.append("r16f");
			break;
		case R8:
			sb.append("r8");
			break;
		case RG16F:
			sb.append("rg16f");
			break;
		case RGB16F:
			sb.append("rgb16f");
			break;
		case RGB32F:
			sb.append("rgb32f");
			break;
		case RGB8:
			sb.append("rgb8");
			break;
		case RGBA16F:
			sb.append("rgba16f");
			break;
		case RGBA32F:
			sb.append("rgba32f");
			break;
		case RGBA8:
			sb.append("rgba8");
			break;
		case R32F:
			sb.append("r32f");
			break;
		case RG32F:
			sb.append("rg32f");
			break;
		case RG8:
			sb.append("rg8");
			break;
		}
		sb.append(") uniform image");
		sb.append(descriptor.getDimensions());
		sb.append("D");
		if (descriptor.isArray()) {
			sb.append("[");
			sb.append(descriptor.getArrayLength());
			sb.append("]");
		}
		sb.append(" ");
		sb.append(setName);
		sb.append("_");
		sb.append(descriptor.getName());
		sb.append(";\n");
	}

	public static void appendDefine(StringBuilder sb, String left, String right) {
		sb.append("#define ");
		sb.append(left);
		sb.append(" ");
		sb.append(right);
		sb.append("\n");
	}

	public static void appendVariableSource(StringBuilder sb, VariableSource variable) {
		sb.append(variable.getType());
		sb.append(" ");
		sb.append(variable.getName());
		sb.append(";\n");
	}

	public static void appendConstantSource(StringBuilder sb, ConstantSource constant) {
		sb.append("const ");
		sb.append(constant.getType());
		sb.append(" ");
		sb.append(constant.getName());
		sb.append(" = ");
		sb.append(constant.getValue());
		sb.append(";\n");
	}

	public static String generateShader(PipelineSource pipeline, ShaderSource shader) {
		StringBuilder sb = new StringBuilder();

		for (StructSource struct : pipeline.getStructs()) {
			appendStructSource(sb, struct);
		}

		if (!pipeline.getUniforms().isEmpty()) {
			sb.append("layout (push_constant) uniform UNIFORMS {\n");
			for (VariableSource uniform : pipeline.getUniforms()) {
				appendVariableSource(sb, uniform);
			}
			sb.append("} uniforms;\n");
		}

		for (int set = 0; set < pipeline.getDescriptorSets().size(); set++) {
			DescriptorSetSource descriptorSet = pipeline.getDescriptorSets().get(set);
			for (int binding = 0; binding < descriptorSet.getDescriptors().size(); binding++) {
				DescriptorSource descriptor = descriptorSet.getDescriptors().get(binding);
				switch (descriptor.getType()) {
				case ACCELERATION_STRUCTURE:
					break;
				case IMAGE_2D:
				case IMAGE_3D:
					appendImageSource(sb, (ImageSource) descriptor, descriptorSet.getName(), set, binding);
					break;
				case TEXTURE_1D:
				case TEXTURE_2D:
				case TEXTURE_3D:
					appendTextureSource(sb, (TextureSource) descriptor, descriptorSet.getName(), set, binding);
					break;
				case TEXTURE_CUBE:
					break;
				case UNIFORM_BUFFER:
				case STORAGE_BUFFER:
					appendBufferSource(sb, (BufferSource) descriptor, descriptorSet.getName(), set, binding);
					break;
				}
			}
		}

		InterfaceBlockSource input = shader.getInput();
		InterfaceBlockSource output = shader.getOutput();

		for (ConstantSource constant : pipeline.getConstants()) {
			appendConstantSource(sb, constant);
		}

		appendDefine(sb, "texture2D", "sampler2D");

		boolean inputArray = false;
		boolean outputArray = false;

		switch (shader.getType()) {
		case COMPUTE:
			appendDefine(sb, "globalInvocationIndex", "gl_GlobalInvocationID");
			appendDefine(sb, "globalInvocationCount", "(gl_NumWorkGroups * gl_WorkGroupSize)");
			appendDefine(sb, "workGroupCount", "gl_NumWorkGroups");
			appendDefine(sb, "workGroupSize", "gl_WorkGroupSize");
			break;
		case FRAGMENT:
			appendDefine(sb, "fragmentCoord", "gl_FragCoord");
			break;
		case GEOMETRY:
			inputArray = true;
			break;
		case HIT:
			break;
		case MISS:
			break;
		case NEAREST_HIT:
			break;
		case RAY_GENERATION:
			break;
		case TESSELATION_CONTROL: {
			appendDefine(sb, "invocationIndex", "gl_InvocationID");
			appendDefine(sb, "outerTesselationLevel", "gl_TessLevelOuter");
			appendDefine(sb, "innerTesselationLevel", "gl_TessLevelInner");
			appendDefine(sb, "vertexPosition", "gl_Position");

			PrimitiveType topology = pipeline.getShader(ShaderType.TESSELATION_EVALUATION).getInput().getPrimitiveType("topology");
			sb.append("layout (vertices = ");
			switch (topology) {
			case LINE_LIST:
				sb.append(2);
				break;
			case LINE_STRIP:
				sb.append(2);
				break;
			case PATCH_LIST:
				break;
			case POINT_LIST:
				sb.append(1);
				break;
			case QUAD_LIST:
				sb.append(4);
				break;
			case TRIANGLE_LIST:
				sb.append(3);
				break;
			case TRIANGLE_STRIP:
				sb.append(3);
				break;
			}
			sb.append(") out;\n");

			inputArray = true;
			outputArray = true;
			break;
		}
		case TESSELATION_EVALUATION:
			appendDefine(sb, "tesselationCoord", "gl_TessCoord");
			appendDefine(sb, "vertexPosition", "gl_Position");

			PrimitiveType topology = input.getPrimitiveType("topology");
			TesselationSpacing spacing = input.getTesselationSpacing("spacing");
			WindingOrder windingOrder = input.getWindingOrder("windingOrder");
			sb.append("layout (");
			switch (topology) {
			case LINE_LIST:
				sb.append("lines");
				break;
			case LINE_STRIP:
				sb.append("line_strip");
				break;
			case PATCH_LIST:
				break;
			case POINT_LIST:
				sb.append("points");
				break;
			case TRIANGLE_LIST:
				sb.append("triangles");
				break;
			case TRIANGLE_STRIP:
				sb.append("triangle_strip");
				break;
			case QUAD_LIST:
				sb.append("quads");
				break;
			}
			sb.append(", ");
			switch (spacing) {
			case EQUAL:
				sb.append("equal_spacing");
				break;
			case FRACTIONAL_EVEN:
				sb.append("fractional_even_spacing");
				break;
			case FRACTIONAL_ODD:
				sb.append("fractional_odd_spacing");
				break;
			}
			sb.append(", ");
			switch (windingOrder) {
			case CLOCKWISE:
				sb.append("cw");
				break;
			case COUNTER_CLOCKWISE:
				sb.append("ccw");
				break;
			}
			sb.append(") in;\n");

			inputArray = true;
			break;
		case VERTEX:
			appendDefine(sb, "vertexPosition", "gl_Position");
			appendDefine(sb, "vertexIndex", "gl_VertexIndex");
			appendDefine(sb, "instanceIndex", "gl_InstanceIndex");
			break;
		}

		appendInterfaceBlockSource(sb, input, true, inputArray);
		appendInterfaceBlockSource(sb, output, false, outputArray);

		for (FunctionSource function : pipeline.getFunctions()) {
			appendFunctionSource(sb, function, input, output, pipeline.getDescriptorSets());
		}

		appendFunctionSource(sb, shader.getMainFunction(), input, output, pipeline.getDescriptorSets());

		return sb.toString();
	}

	private static final long VERTEX_SHADER_COMPILE_OPTIONS;
	private static final long TESSETATION_CONTROL_SHADER_COMPILE_OPTIONS;
	private static final long TESSETATION_EVALUATION_SHADER_COMPILE_OPTIONS;
	private static final long GEOMETRY_SHADER_COMPILE_OPTIONS;
	private static final long FRAGMENT_SHADER_COMPILE_OPTIONS;
	private static final long COMPUTE_SHADER_COMPILE_OPTIONS;
	private static final long COMPILER;
	static {
		VERTEX_SHADER_COMPILE_OPTIONS = createCompileOptions();
		Shaderc.shaderc_compile_options_add_macro_definition(VERTEX_SHADER_COMPILE_OPTIONS, "instanceIndex", "gl_InstanceIndex");
		Shaderc.shaderc_compile_options_add_macro_definition(VERTEX_SHADER_COMPILE_OPTIONS, "vertexIndex", "gl_VertexIndex");
		Shaderc.shaderc_compile_options_add_macro_definition(VERTEX_SHADER_COMPILE_OPTIONS, "vertexPosition", "gl_Position");
		Shaderc.shaderc_compile_options_add_macro_definition(VERTEX_SHADER_COMPILE_OPTIONS, "vertexPosition", "gl_Position");

		TESSETATION_CONTROL_SHADER_COMPILE_OPTIONS = createCompileOptions();

		TESSETATION_EVALUATION_SHADER_COMPILE_OPTIONS = createCompileOptions();

		GEOMETRY_SHADER_COMPILE_OPTIONS = createCompileOptions();

		FRAGMENT_SHADER_COMPILE_OPTIONS = createCompileOptions();

		COMPUTE_SHADER_COMPILE_OPTIONS = createCompileOptions();

		COMPILER = Shaderc.shaderc_compiler_initialize();
	}

	private static long createCompileOptions() {
		long options = Shaderc.shaderc_compile_options_initialize();
		Shaderc.shaderc_compile_options_set_optimization_level(options, Shaderc.shaderc_optimization_level_performance);
		return options;
	}

	public static ByteBuffer compileVertexShader(String src) {
		return compileShader(src, Shaderc.shaderc_vertex_shader, VERTEX_SHADER_COMPILE_OPTIONS);
	}

	public static ByteBuffer compileTesselationControlShader(String src) {
		return compileShader(src, Shaderc.shaderc_tess_control_shader, TESSETATION_CONTROL_SHADER_COMPILE_OPTIONS);
	}

	public static ByteBuffer compileTesselationEvaluationShader(String src) {
		return compileShader(src, Shaderc.shaderc_tess_evaluation_shader, TESSETATION_EVALUATION_SHADER_COMPILE_OPTIONS);
	}

	public static ByteBuffer compileGeometryShader(String src) {
		return compileShader(src, Shaderc.shaderc_geometry_shader, GEOMETRY_SHADER_COMPILE_OPTIONS);
	}

	public static ByteBuffer compileFragmentShader(String src) {
		return compileShader(src, Shaderc.shaderc_fragment_shader, FRAGMENT_SHADER_COMPILE_OPTIONS);
	}

	public static ByteBuffer compileComputeShader(String src) {
		return compileShader(src, Shaderc.shaderc_compute_shader, COMPUTE_SHADER_COMPILE_OPTIONS);
	}

	private static ByteBuffer compileShader(String src, int type, long options) {
		String actualSrc = "#version 450\n#extension GL_ARB_separate_shader_objects : enable\n" + src;

		long result = Shaderc.shaderc_compile_into_spv(COMPILER, actualSrc, type, "shader.glsl", "main", options);

		int status = Shaderc.shaderc_result_get_compilation_status(result);
		if (status != Shaderc.shaderc_compilation_status_success) {
			System.err.println("Error:");
			System.err.println(Shaderc.shaderc_result_get_error_message(result));
			System.err.println("Source:");
			String[] lines = src.split("\n");
			for (int i = 0; i < lines.length; i++) {
				System.err.println((i + 1) + ": " + lines[i]);
			}
		}
		
		ByteBuffer buffer = Shaderc.shaderc_result_get_bytes(result);

		boolean debugOutput = false;

		Spirv spirv = new Spirv(buffer);
		if (debugOutput) {
			System.out.println("###############################################################################");
			System.out.println("source:");
			System.out.println(actualSrc);
		}

		String[] assemblyLines = spirv.toString().split("\n");
		int maxLineLength = 0;
		for (int i = 0; i < assemblyLines.length; i++) {
			maxLineLength = Math.max(maxLineLength, assemblyLines[i].length());
		}

		Array<Optimization> optimizations = new Array<>();
		optimizations.add(FmaOptimization.INSTANCE);
		optimizations.add(DeadCodeOptimization.INSTANCE);
		// optimizations.add(BranchThreadingOptimization.INSTANCE);
		Map<Optimization, Integer> statistics = spirv.optimize(optimizations);
		if (debugOutput) {
			for (Optimization o : statistics.keySet()) {
				System.out.println(o.getClass() + ": " + statistics.get(o));
			}
		}

		String[] optimizedAssemblyLines = spirv.toString().split("\n");
		int maxLineCount = Math.max(assemblyLines.length, optimizedAssemblyLines.length);

		if (debugOutput) {
			System.out.print("assembly:");
			for (int i = 9; i < maxLineLength; i++) {
				System.out.print(" ");
			}
			System.out.println("optimized assembly:");
			for (int i = 0; i < maxLineCount; i++) {
				int currentLength = 0;
				if (i < assemblyLines.length) {
					currentLength = assemblyLines[i].length();
					System.out.print(assemblyLines[i]);
				}
				for (int j = currentLength; j < maxLineLength; j++) {
					System.out.print(" ");
				}
				System.out.print("#");
				if (i < optimizedAssemblyLines.length) {
					System.out.print(optimizedAssemblyLines[i]);
				}
				System.out.println();
			}
		}

		return spirv.get();
	}
}
