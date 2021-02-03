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
import de.kjEngine.scene.SceneEntityMap;
import de.kjEngine.scene.SceneTransformBuffer;
import de.kjEngine.scene.camera.CameraComponent;
import de.kjEngine.scene.camera.CameraComponentUBO;
import de.kjEngine.scene.camera.SceneTransformIndexBuffer;
import de.kjEngine.scene.material.PbrMaterial;
import de.kjEngine.scene.model.Model;
import de.kjEngine.scene.model.ModelComponent;
import de.kjEngine.scene.model.ModelVAO;

/**
 * @author konst
 *
 */
public class DeferredPbrModelRenderer extends DeferredPbrRenderer {

	private static final PipelineSource PIPELINE_SOURCE;
	static {
		PipelineSource source = null;
		try {
			source = PipelineSource.parse(RL.create("jar://scene.renderer/de/kjEngine/scene/renderer/deferred/pbrEntity.shader"));
		} catch (ShaderCompilationException e) {
			e.printStackTrace();
		}
		PIPELINE_SOURCE = source;
		PIPELINE_SOURCE.addDescriptorSet(SceneTransformBuffer.DESCRIPTOR_SET_SOURCE);
		PIPELINE_SOURCE.addDescriptorSet(SceneTransformIndexBuffer.DESCRIPTOR_SET_SOURCE);
		PIPELINE_SOURCE.getDescriptorSets().add(PbrMaterial.DESCRIPTOR_SET_SOURCE);
		PIPELINE_SOURCE.add(CameraComponentUBO.LIBRARY_SOURCE);
		PIPELINE_SOURCE.getShader(ShaderType.FRAGMENT).setOutput(FRAGMENT_SHADER_OUTPUT);
	}

	private GraphicsPipeline shader;

	public DeferredPbrModelRenderer(InputProvider input) {
		super(input);

		shader = Graphics.createGraphicsPipeline(PIPELINE_SOURCE);
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	protected void renderImplementation(RenderList renderList, CommandBuffer cb) {
		super.renderImplementation(renderList, cb);

		Scene scene = renderList.get(Scene.class);
		
		if (scene == null) {
			return;
		}

		SceneEntityMap entityMap = scene.getRenderImplementation(SceneEntityMap.class);
		SceneTransformIndexBuffer indices = scene.getRenderImplementation(SceneTransformIndexBuffer.class);

		if (entityMap.models.isNotEmpty()) {
			cb.bindFrameBuffer(frameBuffer);

			cb.bindPipeline(shader);
			cb.bindDescriptorSet(scene.camera.getRenderImplementation(CameraComponentUBO.class).getDescriptorSet(), "camera");
			cb.bindDescriptorSet(scene.getRenderImplementation(SceneTransformBuffer.class).getDescriptorSet(), "transforms");
			cb.bindDescriptorSet(indices.getDescriptorSet(scene.camera), "indices");

			int baseIndex = 0;
			for (int i = 0; i < entityMap.models.length(); i++) {
				Model m = entityMap.models.get(i);
				
				int batchSize = indices.getBatchSize(scene.camera, i);
				if (batchSize == 0) {
					continue;
				}

				if (m.getMaterial() instanceof PbrMaterial) {
					cb.bindVertexArray(m.getRenderImplementation(ModelVAO.class).getVao());
					cb.bindDescriptorSet(((PbrMaterial) m.getMaterial()).getDescriptorSet(), "material");

					shader.getUniformAccessor().set("instance_offset", baseIndex);
					cb.drawIndexed(m.getIndexCount(), batchSize, 0, 0);
				}
				baseIndex += batchSize;
			}

			cb.unbindFrameBuffer(frameBuffer);
		}
	}

	@Override
	protected void updateDescriptorsImplementation() {
	}

	@Override
	public void getRequiredRenderImplementations(Set<ID> target) {
		target.add(Scene.TRANSFORM_BUFFER);
		target.add(ModelComponent.TRANSFORM_BUFFER);
		target.add(CameraComponent.UBO);
		target.add(Model.VAO);
		target.add(Scene.ENTITY_MAP);
		target.add(SceneTransformIndexBuffer.ID);
	}
}
