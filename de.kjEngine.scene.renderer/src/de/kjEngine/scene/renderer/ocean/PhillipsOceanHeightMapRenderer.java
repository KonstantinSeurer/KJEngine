/**
 * 
 */
package de.kjEngine.scene.renderer.ocean;

import java.util.List;
import java.util.Set;

import de.kjEngine.graphics.CommandBuffer;
import de.kjEngine.graphics.ComputePipeline;
import de.kjEngine.graphics.DescriptorSet;
import de.kjEngine.graphics.Graphics;
import de.kjEngine.graphics.shader.PipelineSource;
import de.kjEngine.graphics.shader.ShaderCompilationException;
import de.kjEngine.io.RL;
import de.kjEngine.renderer.PrepassStage;
import de.kjEngine.renderer.RenderList;
import de.kjEngine.renderer.Renderable.RenderImplementation.ID;
import de.kjEngine.scene.Scene;
import de.kjEngine.scene.ocean.PhillipsOceanHeightMap;
import de.kjEngine.scene.ocean.ScenePhillipsOceanHeightMapList;

/**
 * @author konst
 *
 */
public class PhillipsOceanHeightMapRenderer implements PrepassStage {

	private static final PipelineSource H0K_PIPELINE_SOURCE;
	static {
		PipelineSource source = null;
		try {
			source = PipelineSource.parse(RL.create("jar://scene.renderer/de/kjEngine/scene/renderer/ocean/h0k.shader"));
		} catch (ShaderCompilationException e) {
			e.printStackTrace();
		}
		source.getDescriptorSets().add(PhillipsOceanHeightMap.H0K_DESCRIPTOR_SET_SOURCE);
		H0K_PIPELINE_SOURCE = source;
	}

	private static final PipelineSource HTK_PIPELINE_SOURCE;
	static {
		PipelineSource source = null;
		try {
			source = PipelineSource.parse(RL.create("jar://scene.renderer/de/kjEngine/scene/renderer/ocean/htk.shader"));
		} catch (ShaderCompilationException e) {
			e.printStackTrace();
		}
		source.getDescriptorSets().add(PhillipsOceanHeightMap.HTK_DESCRIPTOR_SET_SOURCE);
		HTK_PIPELINE_SOURCE = source;
	}

	private static final PipelineSource BUTTERFLY_PIPELINE_SOURCE;
	static {
		PipelineSource source = null;
		try {
			source = PipelineSource.parse(RL.create("jar://scene.renderer/de/kjEngine/scene/renderer/ocean/butterfly.shader"));
		} catch (ShaderCompilationException e) {
			e.printStackTrace();
		}
		source.getDescriptorSets().add(PhillipsOceanHeightMap.BUTTERFLY_DESCRIPTOR_SET_SOURCE);
		BUTTERFLY_PIPELINE_SOURCE = source;
	}

	private static final PipelineSource INVERSION_PIPELINE_SOURCE;
	static {
		PipelineSource source = null;
		try {
			source = PipelineSource.parse(RL.create("jar://scene.renderer/de/kjEngine/scene/renderer/ocean/inversion.shader"));
		} catch (ShaderCompilationException e) {
			e.printStackTrace();
		}
		source.getDescriptorSets().add(PhillipsOceanHeightMap.INVERSION_DESCRIPTOR_SET_SOURCE);
		INVERSION_PIPELINE_SOURCE = source;
	}

	private ComputePipeline h0kPipeline;
	private ComputePipeline htkPipeline;
	private ComputePipeline butterflyPipeline;
	private ComputePipeline inversionPipeline;

	public PhillipsOceanHeightMapRenderer() {
		h0kPipeline = Graphics.createComputePipeline(H0K_PIPELINE_SOURCE);
		htkPipeline = Graphics.createComputePipeline(HTK_PIPELINE_SOURCE);
		butterflyPipeline = Graphics.createComputePipeline(BUTTERFLY_PIPELINE_SOURCE);
		inversionPipeline = Graphics.createComputePipeline(INVERSION_PIPELINE_SOURCE);
	}

	@Override
	public void dispose() {
		h0kPipeline.dispose();
		htkPipeline.dispose();
		butterflyPipeline.dispose();
		inversionPipeline.dispose();
	}

	@Override
	public void updateDescriptors() {
	}

	@Override
	public void render(RenderList renderList, CommandBuffer cb) {
		List<PhillipsOceanHeightMap> oceans = renderList.get(Scene.class).getRenderImplementation(ScenePhillipsOceanHeightMapList.class).heightMaps;
		
		cb.bindPipeline(h0kPipeline);

		for (PhillipsOceanHeightMap ocean : oceans) {
			if (ocean.pollUpdateH0k()) {
				cb.bindDescriptorSet(ocean.getH0kDescriptorSet(), "h0k");

				cb.compute(ocean.getN() / 8, ocean.getN() / 8, 1);
			}
		}

		cb.bindPipeline(htkPipeline);

		for (PhillipsOceanHeightMap ocean : oceans) {
			cb.bindDescriptorSet(ocean.getHtkDescriptorSet(), "htk");

			cb.compute(ocean.getN() / 8, ocean.getN() / 8, 1);
		}

		for (PhillipsOceanHeightMap ocean : oceans) {
			computeDisplacement(ocean.getDxButterflyDescriptorSet(), ocean.getDxInversionDescriptorSet(), ocean.getN(), cb);
			computeDisplacement(ocean.getDyButterflyDescriptorSet(), ocean.getDyInversionDescriptorSet(), ocean.getN(), cb);
			computeDisplacement(ocean.getDzButterflyDescriptorSet(), ocean.getDzInversionDescriptorSet(), ocean.getN(), cb);
		}
	}

	private void computeDisplacement(DescriptorSet butterflyDescriptorSet, DescriptorSet inversionDescriptorSet, int N, CommandBuffer cb) {
		int log2N = (int) (Math.log(N) / Math.log(2f));
		int pingpong = 0;

		cb.bindPipeline(butterflyPipeline);
		cb.bindDescriptorSet(butterflyDescriptorSet, "butterfly");

		// horizontal
		butterflyPipeline.getUniformAccessor().set("direction", 0f);
		for (int i = 0; i < log2N; i++) {
			butterflyPipeline.getUniformAccessor().set("stage", i);
			butterflyPipeline.getUniformAccessor().set("pingpong", pingpong);
			cb.compute(N / 8, N / 8, 1);

			pingpong++;
			pingpong %= 2;
		}

		// vertical
		butterflyPipeline.getUniformAccessor().set("direction", 1f);
		for (int i = 0; i < log2N; i++) {
			butterflyPipeline.getUniformAccessor().set("stage", i);
			butterflyPipeline.getUniformAccessor().set("pingpong", pingpong);
			cb.compute(N / 8, N / 8, 1);

			pingpong++;
			pingpong %= 2;
		}

		// inversion
		cb.bindPipeline(inversionPipeline);
		cb.bindDescriptorSet(inversionDescriptorSet, "inversion");
		inversionPipeline.getUniformAccessor().set("pingpong", pingpong);
		cb.compute(N / 8, N / 8, 1);
	}

	@Override
	public void getRequiredRenderImplementations(Set<ID> target) {
		target.add(ScenePhillipsOceanHeightMapList.ID);
	}
}
