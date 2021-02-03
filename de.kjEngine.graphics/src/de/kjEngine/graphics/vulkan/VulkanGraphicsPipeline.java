/**
 * 
 */
package de.kjEngine.graphics.vulkan;

import static de.kjEngine.graphics.vulkan.VulkanUtil.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.vulkan.VK10.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkGraphicsPipelineCreateInfo;
import org.lwjgl.vulkan.VkPipelineColorBlendAttachmentState;
import org.lwjgl.vulkan.VkPipelineColorBlendStateCreateInfo;
import org.lwjgl.vulkan.VkPipelineDepthStencilStateCreateInfo;
import org.lwjgl.vulkan.VkPipelineDynamicStateCreateInfo;
import org.lwjgl.vulkan.VkPipelineInputAssemblyStateCreateInfo;
import org.lwjgl.vulkan.VkPipelineLayoutCreateInfo;
import org.lwjgl.vulkan.VkPipelineMultisampleStateCreateInfo;
import org.lwjgl.vulkan.VkPipelineRasterizationStateCreateInfo;
import org.lwjgl.vulkan.VkPipelineShaderStageCreateInfo;
import org.lwjgl.vulkan.VkPipelineTessellationStateCreateInfo;
import org.lwjgl.vulkan.VkPipelineVertexInputStateCreateInfo;
import org.lwjgl.vulkan.VkPipelineViewportStateCreateInfo;
import org.lwjgl.vulkan.VkPushConstantRange;
import org.lwjgl.vulkan.VkRect2D;
import org.lwjgl.vulkan.VkShaderModuleCreateInfo;
import org.lwjgl.vulkan.VkVertexInputAttributeDescription;
import org.lwjgl.vulkan.VkVertexInputBindingDescription;
import org.lwjgl.vulkan.VkViewport;

import de.kjEngine.graphics.Graphics;
import de.kjEngine.graphics.GraphicsPipeline;
import de.kjEngine.graphics.StructAccessor;
import de.kjEngine.graphics.TextureFormat;
import de.kjEngine.graphics.shader.CullMode;
import de.kjEngine.graphics.shader.DrawMode;
import de.kjEngine.graphics.shader.InterfaceBlockSource;
import de.kjEngine.graphics.shader.PipelineSource;
import de.kjEngine.graphics.shader.PrimitiveType;
import de.kjEngine.graphics.shader.ShaderType;
import de.kjEngine.graphics.shader.VaryingVariableSource;
import de.kjEngine.graphics.shader.WindingOrder;
import de.kjEngine.graphics.shader.parser.spirv.SpirvCompiler;

/**
 * @author konst
 *
 */
public class VulkanGraphicsPipeline extends GraphicsPipeline implements VulkanPipeline {

	long vModule, fModule, tcModule, teModule, gModule;
	VkPipelineShaderStageCreateInfo.Buffer shaderStages;
	VkPipelineVertexInputStateCreateInfo vertexInputStateCreateInfo;
	VkPipelineInputAssemblyStateCreateInfo inputAssemblyStateCreateInfo;
	VkViewport viewport;
	VkRect2D scissor;
	VkPipelineViewportStateCreateInfo viewportStateCreateInfo;
	VkPipelineRasterizationStateCreateInfo rasterizationStateCreateInfo;
	VkPipelineMultisampleStateCreateInfo multisampleStateCreateInfo;
	VkPipelineDepthStencilStateCreateInfo depthStencilStateCreateInfo;
	VkPipelineColorBlendStateCreateInfo colorBlendStateCreateInfo;
	VkPipelineDynamicStateCreateInfo dynamicStateCreateInfo;
	VkPipelineTessellationStateCreateInfo tessellationStateCreateInfo;
	long layout;
	long pipeline;
	VulkanUniforms uniforms;

	public VulkanGraphicsPipeline(PipelineSource source) {
		super(source);
		createShaderModules();
		createShaderStages();
		createVertexInputState();
		createInputAssemblyState();
		createTesselationState();
		createViewportAndScissor();
		createRasterizationState();
		createMultisampleState();
		createDepthStencilState();
		createColorBlendState();
		createDynamicState();
		createLayout();
		createPipeline();
	}

	private void createTesselationState() {
		int patchSize = 1;
		if (source.containsShader(ShaderType.TESSELATION_CONTROL)) {
			InterfaceBlockSource block = source.getShader(ShaderType.TESSELATION_CONTROL).getInput();
			if (block.getSettings().containsKey("patchSize")) {
				patchSize = block.getInt("patchSize");
			}
		}
		tessellationStateCreateInfo = VkPipelineTessellationStateCreateInfo.calloc();
		tessellationStateCreateInfo.set(VK_STRUCTURE_TYPE_PIPELINE_TESSELLATION_STATE_CREATE_INFO, NULL, 0, patchSize);
	}

	private void createPipeline() {
		VkGraphicsPipelineCreateInfo pipelineCreateInfo = VkGraphicsPipelineCreateInfo.calloc().sType(VK_STRUCTURE_TYPE_GRAPHICS_PIPELINE_CREATE_INFO).pNext(NULL).flags(0);
		pipelineCreateInfo.pStages(shaderStages);
		pipelineCreateInfo.pVertexInputState(vertexInputStateCreateInfo);
		pipelineCreateInfo.pInputAssemblyState(inputAssemblyStateCreateInfo);
		pipelineCreateInfo.pTessellationState(tessellationStateCreateInfo);
		pipelineCreateInfo.pViewportState(viewportStateCreateInfo);
		pipelineCreateInfo.pRasterizationState(rasterizationStateCreateInfo);
		pipelineCreateInfo.pMultisampleState(multisampleStateCreateInfo);
		pipelineCreateInfo.pDepthStencilState(depthStencilStateCreateInfo);
		pipelineCreateInfo.pColorBlendState(colorBlendStateCreateInfo);
		pipelineCreateInfo.pDynamicState(dynamicStateCreateInfo);
		pipelineCreateInfo.layout(layout);

		InterfaceBlockSource fboSource = source.getShader(ShaderType.FRAGMENT).getOutput();

		if (fboSource.getSettings().containsKey("target") && fboSource.getSettings().get("target").equals("SCREEN")) {
			pipelineCreateInfo.renderPass(Graphics.getVkContext().getRenderpass());
		} else {
			boolean hasDepthAttachment = false;
			if (fboSource.getSettings().containsKey("depth")) {
				hasDepthAttachment = fboSource.getBoolean("depth");
			}

			TextureFormat[] colorAttachmentFormats = new TextureFormat[fboSource.getVariables().size()];
			for (int i = 0; i < colorAttachmentFormats.length; i++) {
				colorAttachmentFormats[i] = fboSource.getVariables().get(i).getFormat();
			}
			pipelineCreateInfo.renderPass(RenderpassCache.getRenderpass(hasDepthAttachment, colorAttachmentFormats));
		}

		pipelineCreateInfo.subpass(0);
		pipelineCreateInfo.basePipelineHandle(VK_NULL_HANDLE);
		pipelineCreateInfo.basePipelineIndex(0);

		VkGraphicsPipelineCreateInfo.Buffer createInfos = VkGraphicsPipelineCreateInfo.calloc(1).put(0, pipelineCreateInfo);
		LongBuffer pPipeline = memAllocLong(1);
		err(vkCreateGraphicsPipelines(Graphics.getVkContext().getDevice(), VK_NULL_HANDLE, createInfos, null, pPipeline));
		pipeline = pPipeline.get(0);

		memFree(pPipeline);
		createInfos.free();
		pipelineCreateInfo.free();
	}

	private void createLayout() {
		LongBuffer pSetLayouts = memAllocLong(source.getDescriptorSets().size());
		for (int i = 0; i < source.getDescriptorSets().size(); i++) {
			pSetLayouts.put(i, DescriptorSetLayoutCache.getLayout(source.getDescriptorSets().get(i)));
		}

		uniforms = new VulkanUniforms(source);

		VkPushConstantRange.Buffer pushConstantRange = VkPushConstantRange.calloc(1);
		uniforms.getPushConstantRange(pushConstantRange.get(0), VK_SHADER_STAGE_ALL_GRAPHICS);

		VkPipelineLayoutCreateInfo layoutCreateInfo = VkPipelineLayoutCreateInfo.calloc().sType(VK_STRUCTURE_TYPE_PIPELINE_LAYOUT_CREATE_INFO).pNext(NULL).flags(0);
		layoutCreateInfo.pSetLayouts(pSetLayouts);
		layoutCreateInfo.pPushConstantRanges(pushConstantRange);

		LongBuffer pLayout = memAllocLong(1);
		err(vkCreatePipelineLayout(Graphics.getVkContext().getDevice(), layoutCreateInfo, null, pLayout));
		layout = pLayout.get(0);

		memFree(pLayout);
		layoutCreateInfo.free();
		memFree(pSetLayouts);
	}

	private void createDynamicState() {
		dynamicStateCreateInfo = VkPipelineDynamicStateCreateInfo.calloc().sType(VK_STRUCTURE_TYPE_PIPELINE_DYNAMIC_STATE_CREATE_INFO).pNext(NULL).flags(0);
		IntBuffer states = memAllocInt(2);
		states.put(0, VK_DYNAMIC_STATE_VIEWPORT);
		states.put(1, VK_DYNAMIC_STATE_SCISSOR);
		dynamicStateCreateInfo.pDynamicStates(states);
	}

	private void createColorBlendState() {
		colorBlendStateCreateInfo = VkPipelineColorBlendStateCreateInfo.calloc().sType(VK_STRUCTURE_TYPE_PIPELINE_COLOR_BLEND_STATE_CREATE_INFO).pNext(NULL).flags(0);
		colorBlendStateCreateInfo.logicOpEnable(false);

		InterfaceBlockSource block = source.getShader(ShaderType.FRAGMENT).getOutput();

		int attachmentCount = block.getVariables().size();

		VkPipelineColorBlendAttachmentState.Buffer attachments = null;

		boolean blend = false;
		if (block.getSettings().containsKey("blend")) {
			blend = block.getBoolean("blend");
		}

		int srcFactor = VK_BLEND_FACTOR_ONE;
		int dstFactor = VK_BLEND_FACTOR_ZERO;

		if (blend) {
			srcFactor = getBlendFactor(block.getBlendFactor("sourceFactor"));
			dstFactor = getBlendFactor(block.getBlendFactor("destinationFactor"));
		}

		if (attachmentCount > 0) {
			attachments = VkPipelineColorBlendAttachmentState.calloc(attachmentCount);
			for (int i = 0; i < attachmentCount; i++) {
				VkPipelineColorBlendAttachmentState attachment = attachments.get(i);
				attachment.colorWriteMask(VK_COLOR_COMPONENT_R_BIT | VK_COLOR_COMPONENT_G_BIT | VK_COLOR_COMPONENT_B_BIT | VK_COLOR_COMPONENT_A_BIT);
				attachment.blendEnable(blend);
				attachment.srcColorBlendFactor(srcFactor);
				attachment.dstColorBlendFactor(dstFactor);
				attachment.colorBlendOp(VK_BLEND_OP_ADD);
				attachment.srcAlphaBlendFactor(VK_BLEND_FACTOR_ONE);
				attachment.dstAlphaBlendFactor(VK_BLEND_FACTOR_ZERO);
				attachment.alphaBlendOp(VK_BLEND_OP_ADD);
			}
		}
		colorBlendStateCreateInfo.pAttachments(attachments);
	}

	private void createDepthStencilState() {
		InterfaceBlockSource block = source.getShader(ShaderType.FRAGMENT).getInput();

		boolean depthTest = false;
		if (block.getSettings().containsKey("depthTest")) {
			depthTest = block.getBoolean("depthTest");
		}

		boolean depthWrite = depthTest;
		if (block.getSettings().containsKey("depthWrite")) {
			depthWrite = block.getBoolean("depthWrite");
		}

		depthStencilStateCreateInfo = VkPipelineDepthStencilStateCreateInfo.calloc().sType(VK_STRUCTURE_TYPE_PIPELINE_DEPTH_STENCIL_STATE_CREATE_INFO).pNext(NULL).flags(0);
		depthStencilStateCreateInfo.depthTestEnable(depthTest);
		depthStencilStateCreateInfo.depthWriteEnable(depthWrite);
		depthStencilStateCreateInfo.depthCompareOp(VK_COMPARE_OP_LESS);
		depthStencilStateCreateInfo.depthBoundsTestEnable(false);
		depthStencilStateCreateInfo.minDepthBounds(0f);
		depthStencilStateCreateInfo.maxDepthBounds(1f);
		depthStencilStateCreateInfo.stencilTestEnable(false);
	}

	private void createMultisampleState() {
		multisampleStateCreateInfo = VkPipelineMultisampleStateCreateInfo.calloc().sType(VK_STRUCTURE_TYPE_PIPELINE_MULTISAMPLE_STATE_CREATE_INFO).pNext(NULL).flags(0);
		multisampleStateCreateInfo.sampleShadingEnable(false);
		multisampleStateCreateInfo.rasterizationSamples(VK_SAMPLE_COUNT_1_BIT);
	}

	private void createRasterizationState() {
		InterfaceBlockSource block = source.getShader(ShaderType.FRAGMENT).getInput();

		boolean depthClamp = false;
		if (block.getSettings().containsKey("depthClamp")) {
			depthClamp = block.getBoolean("depthClamp");
		}

		DrawMode drawMode = DrawMode.FILL;
		if (block.getSettings().containsKey("drawMode")) {
			drawMode = block.getDrawMode("drawMode");
		}

		CullMode cullMode = CullMode.NONE;
		if (block.getSettings().containsKey("cullMode")) {
			cullMode = block.getCullMode("cullMode");
		}

		WindingOrder frontFace = WindingOrder.COUNTER_CLOCKWISE;
		if (block.getSettings().containsKey("frontFace")) {
			frontFace = block.getWindingOrder("frontFace");
		}

		rasterizationStateCreateInfo = VkPipelineRasterizationStateCreateInfo.calloc().sType(VK_STRUCTURE_TYPE_PIPELINE_RASTERIZATION_STATE_CREATE_INFO).pNext(NULL).flags(0);
		rasterizationStateCreateInfo.depthClampEnable(depthClamp);
		rasterizationStateCreateInfo.rasterizerDiscardEnable(false);
		switch (drawMode) {
		case EDGES:
			rasterizationStateCreateInfo.polygonMode(VK_POLYGON_MODE_LINE);
			break;
		case FILL:
			rasterizationStateCreateInfo.polygonMode(VK_POLYGON_MODE_FILL);
			break;
		}
		rasterizationStateCreateInfo.lineWidth(1f);
		switch (cullMode) {
		case BACK:
			rasterizationStateCreateInfo.cullMode(VK_CULL_MODE_BACK_BIT);
			break;
		case FRONT:
			rasterizationStateCreateInfo.cullMode(VK_CULL_MODE_FRONT_BIT);
			break;
		case NONE:
			rasterizationStateCreateInfo.cullMode(VK_CULL_MODE_NONE);
			break;
		}
		switch (frontFace) {
		case CLOCKWISE:
			rasterizationStateCreateInfo.frontFace(VK_FRONT_FACE_CLOCKWISE);
			break;
		case COUNTER_CLOCKWISE:
			rasterizationStateCreateInfo.frontFace(VK_FRONT_FACE_COUNTER_CLOCKWISE);
			break;
		}
		rasterizationStateCreateInfo.depthBiasEnable(false);
	}

	private void createViewportAndScissor() {
		viewport = VkViewport.calloc();
		viewport.set(0f, 0f, 1f, 1f, 0f, 1f);

		scissor = VkRect2D.calloc();
		scissor.offset().set(0, 0);
		scissor.extent().set(1, 1);

		viewportStateCreateInfo = VkPipelineViewportStateCreateInfo.calloc().sType(VK_STRUCTURE_TYPE_PIPELINE_VIEWPORT_STATE_CREATE_INFO).pNext(NULL).flags(0);

		VkRect2D.Buffer scissors = VkRect2D.calloc(1).put(0, scissor);
		viewportStateCreateInfo.scissorCount(1).pScissors(scissors);

		VkViewport.Buffer viewports = VkViewport.calloc(1).put(0, viewport);
		viewportStateCreateInfo.viewportCount(1).pViewports(viewports);
	}

	private void createInputAssemblyState() {
		inputAssemblyStateCreateInfo = VkPipelineInputAssemblyStateCreateInfo.calloc().sType(VK_STRUCTURE_TYPE_PIPELINE_INPUT_ASSEMBLY_STATE_CREATE_INFO).pNext(NULL).flags(0);
		PrimitiveType topology = source.getShader(ShaderType.VERTEX).getInput().getPrimitiveType("topology");
		switch (topology) {
		case LINE_LIST:
			inputAssemblyStateCreateInfo.topology(VK_PRIMITIVE_TOPOLOGY_LINE_LIST);
			break;
		case LINE_STRIP:
			inputAssemblyStateCreateInfo.topology(VK_PRIMITIVE_TOPOLOGY_LINE_STRIP);
			break;
		case POINT_LIST:
			inputAssemblyStateCreateInfo.topology(VK_PRIMITIVE_TOPOLOGY_POINT_LIST);
			break;
		case TRIANGLE_LIST:
			inputAssemblyStateCreateInfo.topology(VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST);
			break;
		case TRIANGLE_STRIP:
			inputAssemblyStateCreateInfo.topology(VK_PRIMITIVE_TOPOLOGY_TRIANGLE_STRIP);
			break;
		case PATCH_LIST:
			inputAssemblyStateCreateInfo.topology(VK_PRIMITIVE_TOPOLOGY_PATCH_LIST);
			break;
		case QUAD_LIST:
			throw new UnsupportedOperationException();
		}
		inputAssemblyStateCreateInfo.primitiveRestartEnable(false);
	}

	private void createVertexInputState() {
		vertexInputStateCreateInfo = VkPipelineVertexInputStateCreateInfo.calloc().sType(VK_STRUCTURE_TYPE_PIPELINE_VERTEX_INPUT_STATE_CREATE_INFO).pNext(NULL).flags(0);

		InterfaceBlockSource vertexInput = source.getShader(ShaderType.VERTEX).getInput();
		int inputCount = vertexInput.getVariables().size();

		VkVertexInputBindingDescription.Buffer bindings = VkVertexInputBindingDescription.calloc(inputCount);
		VkVertexInputAttributeDescription.Buffer attribs = VkVertexInputAttributeDescription.calloc(inputCount);

		for (int i = 0; i < inputCount; i++) {
			int format = 0;
			int size = 0;

			VaryingVariableSource variable = vertexInput.getVariables().get(i);

			switch (variable.getType()) {
			case "float":
				size = 4;
				format = VK_FORMAT_R32_SFLOAT;
				break;
			case "vec2":
				size = 8;
				format = VK_FORMAT_R32G32_SFLOAT;
				break;
			case "vec3":
				size = 12;
				format = VK_FORMAT_R32G32B32_SFLOAT;
				break;
			case "vec4":
				size = 16;
				format = VK_FORMAT_R32G32B32A32_SFLOAT;
				break;
			}

			if (variable.hasFormat()) {
				format = getFormat(variable.getFormat());
			}

			VkVertexInputBindingDescription binding = bindings.get(i);
			binding.binding(i);
			binding.stride(size);
			binding.inputRate(VK_VERTEX_INPUT_RATE_VERTEX);

			VkVertexInputAttributeDescription attrib = attribs.get(i);
			attrib.binding(i);
			attrib.location(i);
			attrib.format(format);
			attrib.offset(0);
		}

		vertexInputStateCreateInfo.pVertexBindingDescriptions(bindings);
		vertexInputStateCreateInfo.pVertexAttributeDescriptions(attribs);
	}

	private void createShaderStages() {
		List<VkPipelineShaderStageCreateInfo> infos = new ArrayList<>();

		if (vModule != 0) {
			infos.add(VkPipelineShaderStageCreateInfo.malloc().sType(VK_STRUCTURE_TYPE_PIPELINE_SHADER_STAGE_CREATE_INFO).pNext(NULL).flags(0).stage(VK_SHADER_STAGE_VERTEX_BIT).module(vModule)
					.pName(memUTF8("main")).pSpecializationInfo(null));
		}
		if (tcModule != 0) {
			infos.add(VkPipelineShaderStageCreateInfo.malloc().sType(VK_STRUCTURE_TYPE_PIPELINE_SHADER_STAGE_CREATE_INFO).pNext(NULL).flags(0).stage(VK_SHADER_STAGE_TESSELLATION_CONTROL_BIT)
					.module(tcModule).pName(memUTF8("main")).pSpecializationInfo(null));
		}
		if (teModule != 0) {
			infos.add(VkPipelineShaderStageCreateInfo.malloc().sType(VK_STRUCTURE_TYPE_PIPELINE_SHADER_STAGE_CREATE_INFO).pNext(NULL).flags(0).stage(VK_SHADER_STAGE_TESSELLATION_EVALUATION_BIT)
					.module(teModule).pName(memUTF8("main")).pSpecializationInfo(null));
		}
		if (gModule != 0) {
			infos.add(VkPipelineShaderStageCreateInfo.malloc().sType(VK_STRUCTURE_TYPE_PIPELINE_SHADER_STAGE_CREATE_INFO).pNext(NULL).flags(0).stage(VK_SHADER_STAGE_GEOMETRY_BIT).module(gModule)
					.pName(memUTF8("main")).pSpecializationInfo(null));
		}
		if (fModule != 0) {
			infos.add(VkPipelineShaderStageCreateInfo.malloc().sType(VK_STRUCTURE_TYPE_PIPELINE_SHADER_STAGE_CREATE_INFO).pNext(NULL).flags(0).stage(VK_SHADER_STAGE_FRAGMENT_BIT).module(fModule)
					.pName(memUTF8("main")).pSpecializationInfo(null));
		}

		shaderStages = VkPipelineShaderStageCreateInfo.calloc(infos.size());
		for (int i = 0; i < infos.size(); i++) {
			shaderStages.put(i, infos.get(i));
		}
	}

	private void createShaderModules() {
		if (source.containsShader(ShaderType.VERTEX)) {
			vModule = createShaderModule(SpirvCompiler.compileVertexShader(SpirvCompiler.generateShader(source, source.getShader(ShaderType.VERTEX))));
		}
		if (source.containsShader(ShaderType.TESSELATION_CONTROL)) {
			tcModule = createShaderModule(SpirvCompiler.compileTesselationControlShader(SpirvCompiler.generateShader(source, source.getShader(ShaderType.TESSELATION_CONTROL))));
		}
		if (source.containsShader(ShaderType.TESSELATION_EVALUATION)) {
			teModule = createShaderModule(SpirvCompiler.compileTesselationEvaluationShader(SpirvCompiler.generateShader(source, source.getShader(ShaderType.TESSELATION_EVALUATION))));
		}
		if (source.containsShader(ShaderType.GEOMETRY)) {
			gModule = createShaderModule(SpirvCompiler.compileGeometryShader(SpirvCompiler.generateShader(source, source.getShader(ShaderType.GEOMETRY))));
		}
		if (source.containsShader(ShaderType.FRAGMENT)) {
			fModule = createShaderModule(SpirvCompiler.compileFragmentShader(SpirvCompiler.generateShader(source, source.getShader(ShaderType.FRAGMENT))));
		}
	}

	private long createShaderModule(ByteBuffer code) {
		VkShaderModuleCreateInfo moduleCreateInfo = VkShaderModuleCreateInfo.calloc().sType(VK_STRUCTURE_TYPE_SHADER_MODULE_CREATE_INFO).pNext(NULL).pCode(code).flags(0);

		LongBuffer pModule = memAllocLong(1);
		err(vkCreateShaderModule(Graphics.getVkContext().getDevice(), moduleCreateInfo, null, pModule));
		long module = pModule.get(0);

		memFree(pModule);
		moduleCreateInfo.free();

		return module;
	}

	@Override
	public void dispose() {
		VkDevice device = Graphics.getVkContext().getDevice();
		vkDestroyShaderModule(device, vModule, null);
		vkDestroyShaderModule(device, fModule, null);
		vkDestroyPipelineLayout(device, layout, null);
		vkDestroyPipeline(device, pipeline, null);
		uniforms.dispose();
	}

	@Override
	public long getLayout() {
		return layout;
	}

	@Override
	public long getPipeline() {
		return pipeline;
	}

	@Override
	public StructAccessor getUniformAccessor() {
		return uniforms.getAccessor();
	}

	@Override
	public int getBindPoint() {
		return VK_PIPELINE_BIND_POINT_GRAPHICS;
	}

	@Override
	public VulkanUniforms getUniforms() {
		return uniforms;
	}
}
