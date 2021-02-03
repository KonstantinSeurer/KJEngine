/**
 * 
 */
package de.kjEngine.scene.renderer.deferred;

import java.util.Set;

import de.kjEngine.graphics.CommandBuffer;
import de.kjEngine.graphics.Graphics;
import de.kjEngine.graphics.GraphicsPipeline;
import de.kjEngine.graphics.shader.PipelineSource;
import de.kjEngine.graphics.shader.ShaderCompilationException;
import de.kjEngine.graphics.shader.ShaderType;
import de.kjEngine.io.RL;
import de.kjEngine.renderer.RenderList;
import de.kjEngine.renderer.Renderable.RenderImplementation.ID;
import de.kjEngine.scene.Scene;
import de.kjEngine.scene.camera.CameraComponent;
import de.kjEngine.scene.camera.CameraComponentUBO;
import de.kjEngine.scene.material.PbrMaterial;
import de.kjEngine.scene.terrain.SceneTiledTerrainList;
import de.kjEngine.scene.terrain.TiledTerrainComponent;

/**
 * @author konst
 *
 */
public class DeferredPbrTiledTerrainRenderer extends DeferredPbrRenderer {

	private static final PipelineSource PIPELINE_SOURCE;
	static {
		PipelineSource source = null;
		try {
			source = PipelineSource.parse(RL.create("jar://scene.renderer/de/kjEngine/scene/renderer/deferred/pbrTiledTerrain.shader"));
		} catch (ShaderCompilationException e) {
			e.printStackTrace();
		}
		PIPELINE_SOURCE = source;
		PIPELINE_SOURCE.getShader(ShaderType.FRAGMENT).setOutput(FRAGMENT_SHADER_OUTPUT);
		PIPELINE_SOURCE.add(CameraComponentUBO.LIBRARY_SOURCE);
		PIPELINE_SOURCE.addDescriptorSet(TiledTerrainComponent.DESCRIPTOR_SET_SOURCE);
		PIPELINE_SOURCE.addDescriptorSet(PbrMaterial.DESCRIPTOR_SET_SOURCE);
	}

	private GraphicsPipeline pipeline;

	public DeferredPbrTiledTerrainRenderer(InputProvider inputProvider) {
		super(inputProvider);

		pipeline = Graphics.createGraphicsPipeline(PIPELINE_SOURCE);
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	protected void renderImplementation(RenderList renderList, CommandBuffer cb) {
		super.renderImplementation(renderList, cb);

		Scene scene = renderList.get(Scene.class);

		cb.bindFrameBuffer(frameBuffer);
		cb.bindPipeline(pipeline);

		cb.bindDescriptorSet(scene.camera.getRenderImplementation(CameraComponentUBO.class).getDescriptorSet(), "camera");

		for (TiledTerrainComponent terrain : scene.getRenderImplementation(SceneTiledTerrainList.class).terrains) {
			if (terrain.material instanceof PbrMaterial) {
				cb.bindDescriptorSet(terrain.descriptorSet, "terrain");
				cb.bindDescriptorSet(((PbrMaterial) terrain.material).getDescriptorSet(), "material");

				cb.draw(terrain.tileCountX * terrain.tileCountZ, 1, 0, 0);
			}
		}

		cb.unbindFrameBuffer(frameBuffer);
	}

	@Override
	protected void updateDescriptorsImplementation() {
	}

	@Override
	public void getRequiredRenderImplementations(Set<ID> target) {
		target.add(CameraComponent.UBO);
		target.add(Scene.TERRAIN_LIST);
	}
}
