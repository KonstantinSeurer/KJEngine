/**
 * 
 */
package de.kjEngine.scene.renderer.deferred;

import java.util.ArrayList;
import java.util.Set;

import de.kjEngine.graphics.Color;
import de.kjEngine.graphics.CommandBuffer;
import de.kjEngine.graphics.DescriptorSet;
import de.kjEngine.graphics.Graphics;
import de.kjEngine.graphics.GraphicsPipeline;
import de.kjEngine.graphics.shader.DescriptorSetSource;
import de.kjEngine.graphics.shader.PipelineSource;
import de.kjEngine.graphics.shader.ShaderCompilationException;
import de.kjEngine.graphics.shader.TextureSource;
import de.kjEngine.io.RL;
import de.kjEngine.renderer.Filter;
import de.kjEngine.renderer.RenderList;
import de.kjEngine.renderer.Renderable.RenderImplementation.ID;
import de.kjEngine.scene.Scene;
import de.kjEngine.scene.camera.CameraComponent;
import de.kjEngine.scene.camera.CameraComponentUBO;
import de.kjEngine.scene.light.SceneLightBuffer;

/**
 * @author konst
 *
 */
public class DeferredPbrShader extends Filter {

	public static final String INPUT_TEXTURE_BASE_NAME = "base";
	public static final String INPUT_TEXTURE_ALBEDO_NAME = "albedo";
	public static final String INPUT_TEXTURE_NORMAL_NAME = "normal";
	public static final String INPUT_TEXTURE_SUBSURFACE_NAME = "subsurface";
	public static final String INPUT_TEXTURE_EMISSION_NAME = "emission";
	public static final String INPUT_TEXTURE_ROUGHNESS_NAME = "roughness";
	public static final String INPUT_TEXTURE_METALNESS_NAME = "metalness";
	public static final String INPUT_TEXTURE_DISTANCE_NAME = "distance";
	public static final String INPUT_TEXTURE_DEPTH_NAME = "depth";
	public static final String INPUT_TEXTURE_GLOBAL_DIFFUSE_NAME = "globalDiffuse";
	public static final String INPUT_TEXTURE_GLOBAL_SPECULAR_NAME = "globalSpecular";

	public static final String OUTPUT_TEXTURE_RESULT_NAME = "result";

	private static final DescriptorSetSource DESCRIPTOR_SET_SOURCE = new DescriptorSetSource("textures", new ArrayList<>());
	static {
		DESCRIPTOR_SET_SOURCE.getDescriptors().add(new TextureSource("base", 2));
		DESCRIPTOR_SET_SOURCE.getDescriptors().add(new TextureSource("albedo", 2));
		DESCRIPTOR_SET_SOURCE.getDescriptors().add(new TextureSource("normal", 2));
		DESCRIPTOR_SET_SOURCE.getDescriptors().add(new TextureSource("subsurface", 2));
		DESCRIPTOR_SET_SOURCE.getDescriptors().add(new TextureSource("emission", 2));
		DESCRIPTOR_SET_SOURCE.getDescriptors().add(new TextureSource("roughness", 2));
		DESCRIPTOR_SET_SOURCE.getDescriptors().add(new TextureSource("metalness", 2));
		DESCRIPTOR_SET_SOURCE.getDescriptors().add(new TextureSource("distance", 2));
		DESCRIPTOR_SET_SOURCE.getDescriptors().add(new TextureSource("depth", 2));
		DESCRIPTOR_SET_SOURCE.getDescriptors().add(new TextureSource("diffuse", 2));
		DESCRIPTOR_SET_SOURCE.getDescriptors().add(new TextureSource("specular", 2));
		DESCRIPTOR_SET_SOURCE.getDescriptors().add(new TextureSource("brdf", 2));
	}
	
	private static GraphicsPipeline pipeline;
	static {
		try {
			PipelineSource source = PipelineSource.parse(RL.create("jar://scene.renderer/de/kjEngine/scene/renderer/deferred/pbr.shader"));
			source.getDescriptorSets().add(DESCRIPTOR_SET_SOURCE);
			source.add(CameraComponentUBO.LIBRARY_SOURCE);
			source.add(SceneLightBuffer.LIBRARY_SOURCE);
			pipeline = createPipeline(source);
		} catch (ShaderCompilationException e) {
			e.printStackTrace();
		}
	}

	private DescriptorSet descriptorSet;

	public DeferredPbrShader(InputProvider inputProvider) {
		super(inputProvider, pipeline);
		descriptorSet = Graphics.createDescriptorSet(DESCRIPTOR_SET_SOURCE);
	}

	@Override
	public void linkImplementation() {
		descriptorSet.set("base", input.get(INPUT_TEXTURE_BASE_NAME));
		descriptorSet.set("albedo", input.get(INPUT_TEXTURE_ALBEDO_NAME));
		descriptorSet.set("normal", input.get(INPUT_TEXTURE_NORMAL_NAME));
		descriptorSet.set("subsurface", input.get(INPUT_TEXTURE_SUBSURFACE_NAME));
		descriptorSet.set("emission", input.get(INPUT_TEXTURE_EMISSION_NAME));
		descriptorSet.set("roughness", input.get(INPUT_TEXTURE_ROUGHNESS_NAME));
		descriptorSet.set("metalness", input.get(INPUT_TEXTURE_METALNESS_NAME));
		descriptorSet.set("depth", input.get(INPUT_TEXTURE_DEPTH_NAME));
		descriptorSet.set("diffuse", input.get(INPUT_TEXTURE_GLOBAL_DIFFUSE_NAME));
		descriptorSet.set("specular", input.get(INPUT_TEXTURE_GLOBAL_SPECULAR_NAME));
		// descriptorSet.set("brdf", LightProbe.BRDF_LUT);
		descriptorSet.set("brdf", Color.BLACK.getTexture());
		descriptorSet.update();

		output.put(OUTPUT_TEXTURE_RESULT_NAME, frameBuffer.getColorAttachment("color"));
	}

	@Override
	protected void bindDescriptors(RenderList renderList, CommandBuffer cb) {
		cb.bindDescriptorSet(descriptorSet, "textures");
		
		Scene scene = renderList.get(Scene.class);
		if (scene == null) {
			return;
		}
		
		cb.bindDescriptorSet(scene.camera.getRenderImplementation(CameraComponentUBO.class).getDescriptorSet(), "camera");
		cb.bindDescriptorSet(scene.getRenderImplementation(SceneLightBuffer.class).getDescriptorSet(), "lights");
	}

	@Override
	protected void updateDescriptorsImplementation() {
	}

	@Override
	public void getRequiredRenderImplementations(Set<ID> target) {
		target.add(CameraComponent.UBO);
		target.add(Scene.LIGHT_BUFFER);
	}
}
