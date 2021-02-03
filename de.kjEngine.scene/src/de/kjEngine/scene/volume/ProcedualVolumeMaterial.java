/**
 * 
 */
package de.kjEngine.scene.volume;

import java.util.List;

import de.kjEngine.graphics.CommandBuffer;
import de.kjEngine.graphics.ComputePipeline;
import de.kjEngine.graphics.DescriptorSet;
import de.kjEngine.graphics.Graphics;
import de.kjEngine.graphics.SamplingMode;
import de.kjEngine.graphics.ShaderBuffer;
import de.kjEngine.graphics.TextureFormat;
import de.kjEngine.graphics.WrappingMode;
import de.kjEngine.io.RL;
import de.kjEngine.math.Vec3;

/**
 * @author konst
 *
 */
public class ProcedualVolumeMaterial extends VolumeMaterial {

	private static final RL VOLUME_GENERATOR_BASE_RL = new RL("jar", "engine", "de/kjEngine/core/scene/volume/volumeGeneratorBase.glsl");

//	private static final ShaderBuffer.Layout GENERATOR_DATA_UBO_LAYOUT = new ShaderBuffer.Layout.Std140(new GraphicsPipeline.Struct(GraphicsPipeline.DataType.VEC3, GraphicsPipeline.DataType.VEC3));
//
//	private static final DescriptorSet.Layout GENERATOR_DESCRIPTOR_SET_LAYOUT = Graphics.createDescriptorSetLayout();
//	static {
//		GENERATOR_DESCRIPTOR_SET_LAYOUT.set(0, Descriptor.Type.UNIFORM_BUFFER);
//		GENERATOR_DESCRIPTOR_SET_LAYOUT.set(1, Descriptor.Type.IMAGE_3D);
//	}
//
//	public static ComputePipeline createGenerator(RL volumeGeneratorSource, List<DescriptorSet.Layout> layouts) {
//		ComputePipeline.Info generatorInfo = new ComputePipeline.Info();
//		generatorInfo.cmpSrc = ResourceManager.loadTextResource(VOLUME_GENERATOR_BASE_RL, true) + ResourceManager.loadTextResource(volumeGeneratorSource, false);
//		generatorInfo.descriptorSetLayouts.add(GENERATOR_DESCRIPTOR_SET_LAYOUT);
//		if (layouts != null) {
//			for (DescriptorSet.Layout layout : layouts) {
//				generatorInfo.descriptorSetLayouts.add(layout);
//			}
//		}
//		return Graphics.createComputePipeline(generatorInfo);
//	}

	private ComputePipeline generator;
	private DescriptorSet generatorDescriptorSet;
	private ShaderBuffer generatorData;
	private CommandBuffer updateCb = Graphics.createCommandBuffer(CommandBuffer.FLAG_NONE);
	protected List<DescriptorSet> descriptorSets;

	public ProcedualVolumeMaterial(ComputePipeline generator, List<DescriptorSet> descriptorSets, int resolutionX, int resolutionY, int resolutionZ, Vec3 scale, Vec3 sunDir) {
		super(Graphics.createTexture3D(resolutionX, resolutionY, resolutionZ, 1, null, SamplingMode.LINEAR, TextureFormat.R16F, WrappingMode.CLAMP), scale, sunDir);

		this.generator = generator;
		this.descriptorSets = descriptorSets;

		createResources(scale);

//		updateCb.submit();
//		super.update();
	}

	public ProcedualVolumeMaterial(RL volumeGeneratorSource, List<DescriptorSet> descriptorSets, int resolutionX, int resolutionY, int resolutionZ, Vec3 scale, Vec3 sunDir) {
		super(Graphics.createTexture3D(resolutionX, resolutionY, resolutionZ, 1, null, SamplingMode.LINEAR, TextureFormat.R16F, WrappingMode.CLAMP), scale, sunDir);

		this.descriptorSets = descriptorSets;

//		List<DescriptorSet.Layout> layouts = null;
//		if (descriptorSets != null) {
//			layouts = new ArrayList<>();
//			for (DescriptorSet set : descriptorSets) {
//				layouts.add(set.getLayout());
//			}
//		}
//		generator = createGenerator(volumeGeneratorSource, layouts);
//
//		createResources(scale);
//
//		updateCb.submit();
//		super.update();
	}

	private void createResources(Vec3 scale) {
//		generatorData = Graphics.createUniformBuffer(GENERATOR_DATA_UBO_LAYOUT, ShaderBuffer.FLAG_NONE);
//		generatorData.setMember(0, Vec3.create(1f / texture.getWidth(), 1f / texture.getHeight(), 1f / texture.getLength()));
//		generatorData.setMember(1, scale);
//		generatorData.update();
//
//		generatorDescriptorSet = Graphics.createDescriptorSet(GENERATOR_DESCRIPTOR_SET_LAYOUT);
//		generatorDescriptorSet.set(0, generatorData);
//		generatorDescriptorSet.set(1, texture);
//		generatorDescriptorSet.update();
//
//		generator.bindFrameBuffer(updateCb);
//		generatorDescriptorSet.bindDescriptorSet(updateCb, 0);
//		if (descriptorSets != null) {
//			int i = 1;
//			for (DescriptorSet set : descriptorSets) {
//				set.bindDescriptorSet(updateCb, i++);
//			}
//		}
//		generator.compute(updateCb, texture.getWidth() / 8, texture.getHeight() / 8, texture.getLength() / 8);
	}

	@Override
	public void update() {
//		updateCb.update();
//		super.update();
	}

	@Override
	public void dispose() {
//		super.dispose();
		generator.dispose();
		generatorData.dispose();
		generatorDescriptorSet.dispose();
	}
}
