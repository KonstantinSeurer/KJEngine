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
import de.kjEngine.math.Vec3;
import de.kjEngine.renderer.Renderable.RenderImplementation.ID;

/**
 * @author konst
 *
 */
public class MulFilter extends Filter {

	public static final String INPUT_TEXTURE_A_NAME = "a";
	public static final String INPUT_TEXTURE_B_NAME = "b";

	public static final String OUTPUT_TEXTURE_RESULT_NAME = "result";
	
	private static GraphicsPipeline pipeline;
	static {
		PipelineSource source = null;
		try {
			source = PipelineSource.parse(RL.create("jar://renderer/de/kjEngine/renderer/mul.shader"));
		} catch (ShaderCompilationException e) {
			e.printStackTrace();
		}
		
		pipeline = createPipeline(source);
	}

	private DescriptorSet descriptorSet;
	private ShaderBuffer ubo;

	public MulFilter(InputProvider input) {
		super(input, pipeline);
		
		ubo = Graphics.createUniformBuffer(pipeline.getSource(), "data", "data", ShaderBuffer.FLAG_NONE);
		setColor(Vec3.ONE);
		
		descriptorSet = Graphics.createDescriptorSet(pipeline.getSource().getDescriptorSet("data"));
		descriptorSet.set("data", ubo);
		descriptorSet.update();
	}

	public void setColor(Vec3 color) {
		ubo.getAccessor().set("color", color);
		ubo.update();
	}

	@Override
	public void linkImplementation() {
		descriptorSet.set("a", input.get(INPUT_TEXTURE_A_NAME));
		descriptorSet.set("b", input.get(INPUT_TEXTURE_B_NAME));
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
