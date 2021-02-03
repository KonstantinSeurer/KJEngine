/**
 * 
 */
package de.kjEngine.graphics.vulkan;

import static de.kjEngine.graphics.vulkan.VulkanUtil.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.vulkan.VK10.*;

import java.nio.LongBuffer;

import org.lwjgl.vulkan.VkComputePipelineCreateInfo;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkPipelineLayoutCreateInfo;
import org.lwjgl.vulkan.VkPipelineShaderStageCreateInfo;
import org.lwjgl.vulkan.VkPushConstantRange;
import org.lwjgl.vulkan.VkShaderModuleCreateInfo;

import de.kjEngine.graphics.ComputePipeline;
import de.kjEngine.graphics.Graphics;
import de.kjEngine.graphics.StructAccessor;
import de.kjEngine.graphics.shader.InterfaceBlockSource;
import de.kjEngine.graphics.shader.PipelineSource;
import de.kjEngine.graphics.shader.ShaderSource;
import de.kjEngine.graphics.shader.ShaderType;
import de.kjEngine.graphics.shader.parser.spirv.SpirvCompiler;

/**
 * @author konst
 *
 */
public class VulkanComputePipeline extends ComputePipeline implements VulkanPipeline {

	private long pipeline, module, layout;
	private VulkanUniforms uniforms;

	public VulkanComputePipeline(PipelineSource source) {
		super(source);

		uniforms = new VulkanUniforms(source);

		VkDevice device = Graphics.getVkContext().getDevice();

		LongBuffer pSetLayouts = memAllocLong(source.getDescriptorSets().size());
		for (int i = 0; i < source.getDescriptorSets().size(); i++) {
			pSetLayouts.put(i, DescriptorSetLayoutCache.getLayout(source.getDescriptorSets().get(i)));
		}

		VkPushConstantRange.Buffer pushConstantRange = VkPushConstantRange.calloc(1);
		uniforms.getPushConstantRange(pushConstantRange.get(0), VK_SHADER_STAGE_COMPUTE_BIT);

		VkPipelineLayoutCreateInfo layoutCreateInfo = VkPipelineLayoutCreateInfo.calloc().sType(VK_STRUCTURE_TYPE_PIPELINE_LAYOUT_CREATE_INFO).pNext(NULL).flags(0);
		layoutCreateInfo.pSetLayouts(pSetLayouts);
		layoutCreateInfo.pPushConstantRanges(pushConstantRange);

		LongBuffer pLayout = memAllocLong(1);
		err(vkCreatePipelineLayout(Graphics.getVkContext().getDevice(), layoutCreateInfo, null, pLayout));
		layout = pLayout.get(0);

		memFree(pLayout);
		layoutCreateInfo.free();
		memFree(pSetLayouts);

		VkShaderModuleCreateInfo moduleCreateInfo = VkShaderModuleCreateInfo.calloc();
		moduleCreateInfo.sType(VK_STRUCTURE_TYPE_SHADER_MODULE_CREATE_INFO);
		moduleCreateInfo.pNext(NULL);
		moduleCreateInfo.flags(0);
		
		ShaderSource shader = source.getShader(ShaderType.COMPUTE);
		InterfaceBlockSource input = shader.getInput();
		
		StringBuilder code = new StringBuilder();
		code.append("layout (local_size_x = ");
		code.append(input.getInt("localSizeX"));
		code.append(", local_size_y = ");
		code.append(input.getInt("localSizeY"));
		code.append(", local_size_z = ");
		code.append(input.getInt("localSizeZ"));
		code.append(") in;\n");
		code.append(SpirvCompiler.generateShader(source, source.getShader(ShaderType.COMPUTE)));

		moduleCreateInfo.pCode(SpirvCompiler.compileComputeShader(code.toString()));

		LongBuffer pModule = memAllocLong(1);
		VulkanUtil.err(vkCreateShaderModule(device, moduleCreateInfo, null, pModule));
		module = pModule.get(0);
		memFree(pModule);

		VkComputePipelineCreateInfo.Buffer pipelineCreateInfos = VkComputePipelineCreateInfo.calloc(1);
		VkComputePipelineCreateInfo pipelineCreateInfo = pipelineCreateInfos.get(0);

		pipelineCreateInfo.sType(VK_STRUCTURE_TYPE_COMPUTE_PIPELINE_CREATE_INFO);
		pipelineCreateInfo.pNext(NULL);
		pipelineCreateInfo.flags(0);

		VkPipelineShaderStageCreateInfo stageCreateInfo = VkPipelineShaderStageCreateInfo.calloc();
		stageCreateInfo.sType(VK_STRUCTURE_TYPE_PIPELINE_SHADER_STAGE_CREATE_INFO);
		stageCreateInfo.pNext(NULL);
		stageCreateInfo.flags(0);
		stageCreateInfo.stage(VK_SHADER_STAGE_COMPUTE_BIT);
		stageCreateInfo.pName(memUTF8("main"));
		stageCreateInfo.pSpecializationInfo(null);
		stageCreateInfo.module(module);

		pipelineCreateInfo.stage(stageCreateInfo);
		pipelineCreateInfo.layout(layout);
		pipelineCreateInfo.basePipelineHandle(VK_NULL_HANDLE);
		pipelineCreateInfo.basePipelineIndex(0);

		LongBuffer pPipeline = memAllocLong(1);
		VulkanUtil.err(vkCreateComputePipelines(device, VK_NULL_HANDLE, pipelineCreateInfos, null, pPipeline));
		pipeline = pPipeline.get(0);
		memFree(pPipeline);

		pipelineCreateInfos.free();
	}

	@Override
	public void dispose() {
		VkDevice device = Graphics.getVkContext().getDevice();
		vkDestroyPipeline(device, pipeline, null);
		vkDestroyShaderModule(device, module, null);
		vkDestroyPipelineLayout(device, layout, null);
		uniforms.dispose();
	}

	@Override
	public long getPipeline() {
		return pipeline;
	}

	@Override
	public long getLayout() {
		return layout;
	}

	@Override
	public StructAccessor getUniformAccessor() {
		return uniforms.getAccessor();
	}

	@Override
	public int getBindPoint() {
		return VK_PIPELINE_BIND_POINT_COMPUTE;
	}

	@Override
	public VulkanUniforms getUniforms() {
		return uniforms;
	}
}
