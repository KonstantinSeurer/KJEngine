/**
 * 
 */
package de.kjEngine.scene.volume;

import org.json.JSONObject;

import de.kjEngine.graphics.CommandBuffer;
import de.kjEngine.graphics.ComputePipeline;
import de.kjEngine.graphics.DescriptorSet;
import de.kjEngine.graphics.Graphics;
import de.kjEngine.graphics.ShaderBuffer;
import de.kjEngine.graphics.Texture3D;
import de.kjEngine.io.RL;
import de.kjEngine.math.Vec3;
import de.kjEngine.scene.material.Material;

/**
 * @author konst
 *
 */
public class VolumeMaterial extends Material {

//	public static final DescriptorSet.Layout DESCRIPTOR_SET_LAYOUT = Graphics.createDescriptorSetLayout();
//	static {
//		DESCRIPTOR_SET_LAYOUT.set(0, Descriptor.Type.TEXTURE_3D);
//		DESCRIPTOR_SET_LAYOUT.set(1, Descriptor.Type.TEXTURE_3D);
//		DESCRIPTOR_SET_LAYOUT.set(2, Descriptor.Type.UNIFORM_BUFFER);
//	}
//
//	private static final DescriptorSet.Layout SCATTERING_GENERATOR_DESCRIPTOR_SET_LAYOUT = Graphics.createDescriptorSetLayout();
//	static {
//		SCATTERING_GENERATOR_DESCRIPTOR_SET_LAYOUT.set(0, Descriptor.Type.UNIFORM_BUFFER);
//		SCATTERING_GENERATOR_DESCRIPTOR_SET_LAYOUT.set(1, Descriptor.Type.TEXTURE_3D);
//		SCATTERING_GENERATOR_DESCRIPTOR_SET_LAYOUT.set(2, Descriptor.Type.IMAGE_3D);
//	}
//
//	private static final ShaderBuffer.Layout SCATTERING_GENERATOR_DATA_UBO_LAYOUT = new ShaderBuffer.Layout.Std140(new GraphicsPipeline.Struct(GraphicsPipeline.DataType.VEC3, GraphicsPipeline.DataType.VEC3, GraphicsPipeline.DataType.VEC3));
//
//	public static final ShaderBuffer.Layout UBO_LAYOUT = new ShaderBuffer.Layout.Std140(new GraphicsPipeline.Struct(GraphicsPipeline.DataType.VEC3));

	private static final RL SCATTERING_GENERATOR_RL = new RL("jar", "engine", "de/kjEngine/core/scene/volume/scatteringGenerator.glsl");

	protected DescriptorSet descriptorSet;

	protected Texture3D texture;
	protected Texture3D scatteringTexture;
	protected ShaderBuffer ubo;

	private DescriptorSet scatteringGeneratorDescriptorSet;
	private ShaderBuffer scatteringGeneratorData;

	private ComputePipeline scatteringGenerator;

	private CommandBuffer updateCb = Graphics.createCommandBuffer(CommandBuffer.FLAG_NONE);

	public VolumeMaterial(Texture3D texture, Vec3 scale, Vec3 sunDir) {
		this.texture = texture;

//		ComputePipeline.Info scatteringGeneratorInfo = new ComputePipeline.Info();
//		scatteringGeneratorInfo.cmpSrc = ResourceManager.loadTextResource(SCATTERING_GENERATOR_RL, true);
//		scatteringGeneratorInfo.descriptorSetLayouts.add(SCATTERING_GENERATOR_DESCRIPTOR_SET_LAYOUT);
//		scatteringGenerator = Graphics.createComputePipeline(scatteringGeneratorInfo);
//
//		scatteringTexture = Graphics.createTexture3D(texture.getWidth(), texture.getHeight(), texture.getLength(), 1, null, SamplingMode.LINEAR, TextureFormat.R8, WrappingMode.CLAMP);
//
//		scatteringGeneratorData = Graphics.createUniformBuffer(SCATTERING_GENERATOR_DATA_UBO_LAYOUT, ShaderBuffer.FLAG_NONE);
//		scatteringGeneratorData.setMember(0, Vec3.create(1f / texture.getWidth(), 1f / texture.getHeight(), 1f / texture.getLength()));
//		scatteringGeneratorData.setMember(1, scale);
//		scatteringGeneratorData.setMember(2, sunDir);
//		scatteringGeneratorData.update();
//
//		scatteringGeneratorDescriptorSet = Graphics.createDescriptorSet(SCATTERING_GENERATOR_DESCRIPTOR_SET_LAYOUT);
//		scatteringGeneratorDescriptorSet.set(0, scatteringGeneratorData);
//		scatteringGeneratorDescriptorSet.set(1, texture);
//		scatteringGeneratorDescriptorSet.set(2, scatteringTexture);
//		scatteringGeneratorDescriptorSet.update();
//
//		scatteringGenerator.bindFrameBuffer(updateCb);
//		scatteringGeneratorDescriptorSet.bindDescriptorSet(updateCb, 0);
//		scatteringGenerator.compute(updateCb, texture.getWidth() / 8, texture.getHeight() / 8, texture.getLength() / 8);
//
//		ubo = Graphics.createUniformBuffer(UBO_LAYOUT, ShaderBuffer.FLAG_NONE);
//		ubo.setMember(0, Vec3.create());
//		ubo.update();
//
//		descriptorSet = Graphics.createDescriptorSet(DESCRIPTOR_SET_LAYOUT);
//		descriptorSet.set(0, texture);
//		descriptorSet.set(1, scatteringTexture);
//		descriptorSet.set(2, ubo);
//		descriptorSet.update();
	}

	public VolumeMaterial setScattering(Vec3 scattering) {
//		ubo.setMember(0, scattering);
		ubo.update();
		return this;
	}

	public void update() {
		updateCb.submit();
	}

	@Override
	public void dispose() {
		texture.dispose();
		scatteringTexture.dispose();
		descriptorSet.dispose();

		scatteringGenerator.dispose();
		scatteringGeneratorData.dispose();
		scatteringGeneratorDescriptorSet.dispose();
	}

	/**
	 * @return the descriptorSet
	 */
	public DescriptorSet getDescriptorSet() {
		return descriptorSet;
	}

	@Override
	public void deserialize(JSONObject obj) {
	}

	@Override
	public JSONObject serialize() {
		return null;
	}

	@Override
	public Material deepCopy() {
		return null;
	}

	@Override
	public Material shallowCopy() {
		return null;
	}
}
