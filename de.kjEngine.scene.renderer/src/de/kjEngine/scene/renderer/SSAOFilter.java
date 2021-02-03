/**
 * 
 */
package de.kjEngine.scene.renderer;

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
import de.kjEngine.renderer.Filter;
import de.kjEngine.renderer.RenderList;
import de.kjEngine.renderer.Renderable.RenderImplementation.ID;
import de.kjEngine.scene.Scene;
import de.kjEngine.scene.camera.CameraComponent;
import de.kjEngine.scene.camera.CameraComponentUBO;

/**
 * @author konst
 *
 */
public class SSAOFilter extends Filter implements Serializable {
	
	public static final String INPUT_TEXTURE_TEXTURE_NAME = "texture";
	public static final String INPUT_TEXTURE_DEPTH_NAME = "depth";
	public static final String INPUT_TEXTURE_NORMAL_NAME = "normal";
	public static final String OUTPUT_TEXTURE_RESULT_NAME = "result";
	
	private static GraphicsPipeline pipeline;
	static {
		PipelineSource source = null;
		try {
			source = PipelineSource.parse(RL.create("jar://scene.renderer/de/kjEngine/scene/renderer/ssao.shader"));
		} catch (ShaderCompilationException e) {
			e.printStackTrace();
		}
		source.add(CameraComponentUBO.LIBRARY_SOURCE);
		
		pipeline = createPipeline(source);
	}
	
	private DescriptorSet descriptorSet;
	private ShaderBuffer ubo;

	public SSAOFilter(InputProvider inputProvider) {
		super(inputProvider, pipeline);
		
		ubo = Graphics.createUniformBuffer(pipeline.getSource(), "data", "settings", ShaderBuffer.FLAG_NONE);
		setAmount(1f);
		setRadius(0.1f);
		setSampleCount(50);
		
		descriptorSet = Graphics.createDescriptorSet(pipeline.getSource().getDescriptorSet("data"));
		descriptorSet.set("settings", ubo);
		descriptorSet.update();
	}
	
	@Override
	public void linkImplementation() {
		descriptorSet.set("texture", input.get(INPUT_TEXTURE_TEXTURE_NAME));
		descriptorSet.set("depth", input.get(INPUT_TEXTURE_DEPTH_NAME));
		descriptorSet.set("normal", input.get(INPUT_TEXTURE_NORMAL_NAME));
		descriptorSet.update();

		output.put(OUTPUT_TEXTURE_RESULT_NAME, frameBuffer.getColorAttachment("color"));
	}
	
	public SSAOFilter setAmount(float amount) {
		ubo.getAccessor().set("amount", amount);
		ubo.update();
		return this;
	}
	
	public SSAOFilter setRadius(float radius) {
		ubo.getAccessor().set("radius", radius);
		ubo.update();
		return this;
	}
	
	public SSAOFilter setSampleCount(int count) {
		ubo.getAccessor().set("sampleCount", count);
		ubo.update();
		return this;
	}

	@Override
	protected void bindDescriptors(RenderList renderList, CommandBuffer cb) {
		cb.bindDescriptorSet(descriptorSet, "data");
		cb.bindDescriptorSet(renderList.get(Scene.class).camera.getRenderImplementation(CameraComponentUBO.class).getDescriptorSet(), "camera");
	}

	@Override
	public void deserialize(JSONObject obj) {
		if (obj.has("amount")) {
			setAmount(obj.getFloat("amount"));
		}
		if (obj.has("radius")) {
			setRadius(obj.getFloat("radius"));
		}
		if (obj.has("sampleCount")) {
			setSampleCount(obj.getInt("sampleCount"));
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
		target.add(CameraComponent.UBO);
	}
}
