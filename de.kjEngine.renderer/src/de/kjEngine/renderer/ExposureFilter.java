/**
 * 
 */
package de.kjEngine.renderer;

import java.util.Set;

import org.json.JSONObject;

import de.kjEngine.graphics.CommandBuffer;
import de.kjEngine.graphics.DescriptorSet;
import de.kjEngine.graphics.Graphics;
import de.kjEngine.graphics.GraphicsPipeline;
import de.kjEngine.graphics.ShaderBuffer;
import de.kjEngine.graphics.shader.PipelineSource;
import de.kjEngine.graphics.shader.ShaderCompilationException;
import de.kjEngine.io.RL;
import de.kjEngine.io.serilization.Serializable;
import de.kjEngine.renderer.Renderable.RenderImplementation.ID;

/**
 * @author konst
 *
 */
public class ExposureFilter extends Filter implements Serializable {
	
	private static GraphicsPipeline pipeline;
	static {
		PipelineSource source = null;
		try {
			source = PipelineSource.parse(RL.create("jar://renderer/de/kjEngine/renderer/exposure.shader"));
		} catch (ShaderCompilationException e) {
			e.printStackTrace();
		}
		
		pipeline = createPipeline(source);
	}

	public static final String INPUT_TEXTURE_TEXTURE_NAME = "texture";
	public static final String OUTPUT_TEXTURE_RESULT_NAME = "result";

	private DescriptorSet descriptorSet;
	private ShaderBuffer ubo;

	public ExposureFilter(InputProvider input) {
		super(input, pipeline);

		ubo = Graphics.createUniformBuffer(pipeline.getSource(), "data", "data", ShaderBuffer.FLAG_NONE);
		setExposure(2f);

		descriptorSet = Graphics.createDescriptorSet(pipeline.getSource().getDescriptorSet("data"));
		descriptorSet.set("data", ubo);
		descriptorSet.update();
	}

	public void setExposure(float exposure) {
		ubo.getAccessor().set("exposure", exposure);
		ubo.update();
	}

	@Override
	public void linkImplementation() {
		descriptorSet.set("texture", input.get(INPUT_TEXTURE_TEXTURE_NAME));
		descriptorSet.update();

		output.put(OUTPUT_TEXTURE_RESULT_NAME, frameBuffer.getColorAttachment("color"));
	}

	@Override
	protected void bindDescriptors(RenderList renderList, CommandBuffer cb) {
		cb.bindDescriptorSet(descriptorSet, "data");
	}

	@Override
	public void deserialize(JSONObject obj) {
		if (obj.has("exposure")) {
			setExposure(obj.getFloat("exposure"));
		}
	}

	@Override
	public JSONObject serialize() {
		return null;
	}

	@Override
	protected void updateDescriptorsImplementation() {
	}

	@Override
	public void getRequiredRenderImplementations(Set<ID> target) {
	}
}
