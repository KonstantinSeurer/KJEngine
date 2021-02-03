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
import de.kjEngine.math.Vec3;
import de.kjEngine.renderer.Filter;
import de.kjEngine.renderer.RenderList;
import de.kjEngine.renderer.Renderable.RenderImplementation.ID;

/**
 * @author konst
 *
 */
public class SimpleAtmosphereRenderer extends Filter implements Serializable {

	private DescriptorSet descriptorSet;
	private ShaderBuffer ubo;

	private static GraphicsPipeline pipeline;
	static {
		PipelineSource source = null;
		try {
			source = PipelineSource.parse(RL.create("jar://scene.renderer/de/kjEngine/scene/renderer/simpleAtmosphere.shader"));
		} catch (ShaderCompilationException e) {
			e.printStackTrace();
		}
		pipeline = createPipeline(source);
	}

	public SimpleAtmosphereRenderer(InputProvider input) {
		super(input, pipeline);
		
		ubo = Graphics.createUniformBuffer(pipeline.getSource(), "data", "settings", ShaderBuffer.FLAG_NONE);
		setUpperColor(Vec3.create(0.1f, 0.2f, 0.5f));
		setLowerColor(Vec3.create(0.1f, 0.2f, 0.5f));
		
		descriptorSet = Graphics.createDescriptorSet(pipeline.getSource().getDescriptorSet("data"));
		descriptorSet.set("settings", ubo);
		descriptorSet.update();
	}

	@Override
	protected void linkImplementation() {
		output.put("result", frameBuffer.getColorAttachment("color"));
	}

	@Override
	protected void bindDescriptors(RenderList renderList, CommandBuffer cb) {
		cb.bindDescriptorSet(descriptorSet, "data");
	}

	public SimpleAtmosphereRenderer setUpperColor(Vec3 color) {
		ubo.getAccessor().set("upperColor", color);
		ubo.update();
		return this;
	}
	
	public SimpleAtmosphereRenderer setLowerColor(Vec3 color) {
		ubo.getAccessor().set("lowerColor", color);
		ubo.update();
		return this;
	}

	@Override
	public void deserialize(JSONObject obj) {
		if (obj.has("upperColor")) {
			JSONObject color = obj.getJSONObject("upperColor");
			float r = 0f, g = 0f, b = 0f;
			if (color.has("r")) {
				r = color.getFloat("r");
			}
			if (color.has("g")) {
				g = color.getFloat("g");
			}
			if (color.has("b")) {
				b = color.getFloat("b");
			}
			setUpperColor(Vec3.create(r, g, b));
		}
		if (obj.has("lowerColor")) {
			JSONObject color = obj.getJSONObject("lowerColor");
			float r = 0f, g = 0f, b = 0f;
			if (color.has("r")) {
				r = color.getFloat("r");
			}
			if (color.has("g")) {
				g = color.getFloat("g");
			}
			if (color.has("b")) {
				b = color.getFloat("b");
			}
			setLowerColor(Vec3.create(r, g, b));
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

	@Override
	protected void resizeImplementation(int width, int height) {
		super.resizeImplementation(1, height);
	}
}
