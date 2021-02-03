/**
 * 
 */
package de.kjEngine.renderer;

import java.util.Set;

import de.kjEngine.graphics.CommandBuffer;
import de.kjEngine.graphics.DescriptorSet;
import de.kjEngine.graphics.Graphics;
import de.kjEngine.graphics.GraphicsPipeline;
import de.kjEngine.graphics.Texture2D;
import de.kjEngine.graphics.shader.PipelineSource;
import de.kjEngine.graphics.shader.ShaderCompilationException;
import de.kjEngine.io.RL;
import de.kjEngine.renderer.Renderable.RenderImplementation.ID;

/**
 * @author konst
 *
 */
public class ACESTonemappingFilter extends Filter {

	public static final String INPUT_TEXTURE_TEXTURE_NAME = "texture";
	public static final String OUTPUT_TEXTURE_RESULT_NAME = "result";
	
	private static GraphicsPipeline pipeline;
	static {
		try {
			PipelineSource source = PipelineSource.parse(RL.create("jar://renderer/de/kjEngine/renderer/aces.shader"));
			pipeline = createPipeline(source);
		} catch (ShaderCompilationException e) {
			e.printStackTrace();
		}
	}

	private DescriptorSet descriptorSet;

	public ACESTonemappingFilter(InputProvider input) {
		super(input, pipeline);
		descriptorSet = Graphics.createDescriptorSet(pipeline.getSource().getDescriptorSet("textures"));
	}

	@Override
	public void linkImplementation() {
		setTexture(input.get(INPUT_TEXTURE_TEXTURE_NAME));
		output.put(OUTPUT_TEXTURE_RESULT_NAME, frameBuffer.getColorAttachment("color"));
	}

	public ACESTonemappingFilter setTexture(Texture2D texture) {
		descriptorSet.set("texture", texture);
		descriptorSet.update();
		return this;
	}

	@Override
	protected void bindDescriptors(RenderList renderList, CommandBuffer cb) {
		cb.bindDescriptorSet(descriptorSet, "textures");
	}

	@Override
	protected void updateDescriptorsImplementation() {
	}

	@Override
	public void getRequiredRenderImplementations(Set<ID> target) {
	}
}
