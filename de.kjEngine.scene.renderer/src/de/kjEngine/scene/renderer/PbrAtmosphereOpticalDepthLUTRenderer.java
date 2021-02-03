/**
 * 
 */
package de.kjEngine.scene.renderer;

import java.util.List;
import java.util.Set;

import de.kjEngine.graphics.CommandBuffer;
import de.kjEngine.graphics.ComputePipeline;
import de.kjEngine.graphics.Graphics;
import de.kjEngine.graphics.shader.PipelineSource;
import de.kjEngine.graphics.shader.ShaderCompilationException;
import de.kjEngine.io.RL;
import de.kjEngine.renderer.PrepassStage;
import de.kjEngine.renderer.RenderList;
import de.kjEngine.renderer.Renderable.RenderImplementation.ID;
import de.kjEngine.scene.Scene;
import de.kjEngine.scene.atmosphere.PbrAtmosphereComponent;
import de.kjEngine.scene.atmosphere.PbrAtmosphereComponentOpticalDepthLUT;
import de.kjEngine.scene.atmosphere.PbrAtmosphereComponentUBO;
import de.kjEngine.scene.atmosphere.ScenePbrAtmosphereList;

/**
 * @author konst
 *
 */
public class PbrAtmosphereOpticalDepthLUTRenderer implements PrepassStage {

	private static ComputePipeline pipeline;
	static {
		try {
			PipelineSource source = PipelineSource.parse(RL.create("jar://scene.renderer/de/kjEngine/scene/renderer/pbrAtmosphereOpticalDepthLUT.shader"));
			source.addDescriptorSet(PbrAtmosphereComponentUBO.DESCRIPTOR_SET_SOURCE);
			source.addDescriptorSet(PbrAtmosphereComponentOpticalDepthLUT.WRITE_DESCRIPTOR_SET_SOURCE.shallowCopy().setName("target"));
			pipeline = Graphics.createComputePipeline(source);
		} catch (ShaderCompilationException e) {
			e.printStackTrace();
		}
	}

	public PbrAtmosphereOpticalDepthLUTRenderer() {
	}

	@Override
	public void dispose() {
	}

	@Override
	public void updateDescriptors() {
	}

	@Override
	public void render(RenderList renderList, CommandBuffer cb) {
		cb.bindPipeline(pipeline);

		Scene scene = renderList.get(Scene.class);
		List<PbrAtmosphereComponent> atmospheres = scene.getRenderImplementation(ScenePbrAtmosphereList.class).atmospheres;

		for (PbrAtmosphereComponent atmosphere : atmospheres) {
			PbrAtmosphereComponentOpticalDepthLUT lut = atmosphere.getRenderImplementation(PbrAtmosphereComponentOpticalDepthLUT.class);

			if (lut.pollUpdate()) {
				cb.bindDescriptorSet(atmosphere.getRenderImplementation(PbrAtmosphereComponentUBO.class).descriptorSet, "atmosphere");
				cb.bindDescriptorSet(lut.writeDescriptorSet, "target");

				cb.compute(PbrAtmosphereComponentOpticalDepthLUT.RESOLUTION / 8, PbrAtmosphereComponentOpticalDepthLUT.RESOLUTION / 8, 1);
			}
		}
	}

	@Override
	public void getRequiredRenderImplementations(Set<ID> target) {
		target.add(ScenePbrAtmosphereList.ID);
		target.add(PbrAtmosphereComponentOpticalDepthLUT.ID);
		target.add(PbrAtmosphereComponentUBO.ID);
	}
}
