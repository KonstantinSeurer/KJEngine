/**
 * 
 */
package de.kjEngine.graphics.vulkan;

import de.kjEngine.graphics.RayTracingPipeline;
import de.kjEngine.graphics.StructAccessor;
import de.kjEngine.graphics.shader.FunctionSource;
import de.kjEngine.graphics.shader.PipelineSource;
import de.kjEngine.graphics.shader.ShaderType;

/**
 * @author konst
 *
 */
public class EmulatedVulkanRayTracingPipeline extends RayTracingPipeline implements VulkanPipeline {

	private VulkanComputePipeline implementation;

	public EmulatedVulkanRayTracingPipeline(PipelineSource source) {
		super(source);
		PipelineSource s = new PipelineSource();
		s.add(source);
		
		FunctionSource main = new FunctionSource(source.getShader(ShaderType.COMPUTE).getMainFunction());
		main.setSource(main.getSource().replaceAll("invocationIndex", "gl_GlobalInvocationID"));
		s.getShader(ShaderType.COMPUTE).setMainFunction(main);
		
		implementation = new VulkanComputePipeline(source);
	}

	@Override
	public void dispose() {
		implementation.dispose();
	}

	@Override
	public Type getType() {
		return Type.COMPUTE;
	}

	@Override
	public long getPipeline() {
		return implementation.getPipeline();
	}

	@Override
	public long getLayout() {
		return implementation.getLayout();
	}

	@Override
	public StructAccessor getUniformAccessor() {
		return implementation.getUniformAccessor();
	}

	@Override
	public int getBindPoint() {
		return implementation.getBindPoint();
	}

	@Override
	public VulkanUniforms getUniforms() {
		return implementation.getUniforms();
	}
}
