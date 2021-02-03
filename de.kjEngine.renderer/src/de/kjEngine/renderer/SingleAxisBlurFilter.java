/**
 * 
 */
package de.kjEngine.renderer;

import java.util.Set;

import de.kjEngine.graphics.CommandBuffer;
import de.kjEngine.graphics.DescriptorSet;
import de.kjEngine.graphics.Graphics;
import de.kjEngine.graphics.GraphicsPipeline;
import de.kjEngine.graphics.ShaderBuffer;
import de.kjEngine.graphics.shader.PipelineSource;
import de.kjEngine.graphics.shader.ShaderCompilationException;
import de.kjEngine.io.RL;
import de.kjEngine.math.Vec2;
import de.kjEngine.renderer.Renderable.RenderImplementation.ID;

/**
 * @author konst
 *
 */
public class SingleAxisBlurFilter extends Filter {

	public static final String INPUT_TEXTURE_TEXTURE_NAME = "texture";

	public static final String OUTPUT_TEXTURE_RESULT_NAME = "result";
	
	private static GraphicsPipeline pipeline;
	static {
		PipelineSource source = null;
		try {
			source = PipelineSource.parse(RL.create("jar://renderer/de/kjEngine/renderer/blur.shader"));
		} catch (ShaderCompilationException e) {
			e.printStackTrace();
		}
		
		pipeline = createPipeline(source);
	}

	private DescriptorSet descriptorSet;
	private ShaderBuffer ubo;

	public SingleAxisBlurFilter(InputProvider input) {
		super(input, pipeline);
		
		ubo = Graphics.createUniformBuffer(pipeline.getSource(), "data", "data", ShaderBuffer.FLAG_NONE);
		setAxis(Vec2.create(0.05f, 0f));
		setSampleCount(16);
		
		descriptorSet = Graphics.createDescriptorSet(pipeline.getSource().getDescriptorSet("data"));
		descriptorSet.set("data", ubo);
		descriptorSet.update();
	}

	public void setAxis(Vec2 axis) {
		ubo.getAccessor().set("axis", axis);
		ubo.update();
	}

	public void setSampleCount(int count) {
		ubo.getAccessor().set("sampleCount", count);
		ubo.update();
	}

	@Override
	protected void linkImplementation() {
		descriptorSet.set("texture", input.get(INPUT_TEXTURE_TEXTURE_NAME));
		descriptorSet.update();

		output.put(OUTPUT_TEXTURE_RESULT_NAME, frameBuffer.getColorAttachment("color"));
	}

	@Override
	protected void bindDescriptors(RenderList renderList, CommandBuffer cb) {
		cb.bindDescriptorSet(descriptorSet, "data");
	}

	@Override
	protected void updateDescriptorsImplementation() {
	}

	@Override
	public void getRequiredRenderImplementations(Set<ID> target) {
	}
}
