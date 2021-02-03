/**
 * 
 */
package de.kjEngine.scene.renderer.shadows;

import java.util.Set;

import de.kjEngine.graphics.CommandBuffer;
import de.kjEngine.graphics.FrameBuffer;
import de.kjEngine.graphics.Graphics;
import de.kjEngine.graphics.GraphicsPipeline;
import de.kjEngine.graphics.shader.PipelineSource;
import de.kjEngine.graphics.shader.ShaderCompilationException;
import de.kjEngine.graphics.shader.ShaderType;
import de.kjEngine.io.RL;
import de.kjEngine.renderer.RenderList;
import de.kjEngine.renderer.Renderable.RenderImplementation.ID;
import de.kjEngine.renderer.Stage;
import de.kjEngine.scene.Scene;
import de.kjEngine.scene.SceneEntityMap;
import de.kjEngine.scene.SceneTransformBuffer;
import de.kjEngine.scene.camera.CameraComponent;
import de.kjEngine.scene.camera.CameraComponentUBO;
import de.kjEngine.scene.camera.SceneTransformIndexBuffer;
import de.kjEngine.scene.light.ShadowMapComponent;
import de.kjEngine.scene.material.PbrMaterial;
import de.kjEngine.scene.model.Model;
import de.kjEngine.scene.model.ModelComponent;
import de.kjEngine.scene.model.ModelVAO;

/**
 * @author konst
 *
 */
public class ShadowDepthMapPbrModelRenderer extends Stage {

	private static PipelineSource PIPELINE_SOURCE;
	static {
		try {
			PIPELINE_SOURCE = PipelineSource.parse(RL.create("jar://scene.renderer/de/kjEngine/scene/renderer/shadows/depthMapModel.shader"));
			PIPELINE_SOURCE.addDescriptorSet(SceneTransformBuffer.DESCRIPTOR_SET_SOURCE);
			PIPELINE_SOURCE.addDescriptorSet(SceneTransformIndexBuffer.DESCRIPTOR_SET_SOURCE);
			PIPELINE_SOURCE.add(CameraComponentUBO.LIBRARY_SOURCE);
			PIPELINE_SOURCE.getDescriptorSets().add(PbrMaterial.DESCRIPTOR_SET_SOURCE);
		} catch (ShaderCompilationException e) {
			e.printStackTrace();
		}
	}

	private FrameBuffer frameBuffer;
	private GraphicsPipeline pipeline;

	public ShadowDepthMapPbrModelRenderer(InputProvider input) {
		super(input);
		pipeline = Graphics.createGraphicsPipeline(PIPELINE_SOURCE);
	}

	@Override
	public void dispose() {
	}

	@Override
	protected void renderImplementation(RenderList renderList, CommandBuffer cb) {
		Scene scene = renderList.get(Scene.class);

		SceneEntityMap entityMap = scene.getRenderImplementation(SceneEntityMap.class);
		CameraComponent camera = renderList.get(ShadowMapComponent.class).camera;
		SceneTransformIndexBuffer indices = scene.getRenderImplementation(SceneTransformIndexBuffer.class);

		cb.bindFrameBuffer(frameBuffer);
		cb.clearFrameBuffer(frameBuffer);
		
		if (entityMap.models.length() > 0) {
			cb.bindPipeline(pipeline);
			cb.bindDescriptorSet(camera.getRenderImplementation(CameraComponentUBO.class).getDescriptorSet(), "camera");
			cb.bindDescriptorSet(scene.getRenderImplementation(SceneTransformBuffer.class).getDescriptorSet(), "transforms");
			cb.bindDescriptorSet(indices.getDescriptorSet(camera), "indices");

			int baseIndex = 0;
			for (int i = 0; i < entityMap.models.length(); i++) {
				Model m = entityMap.models.get(i);
				
				int batchSize = indices.getBatchSize(camera, i);

				if (m.getMaterial() instanceof PbrMaterial) {
					cb.bindVertexArray(m.getRenderImplementation(ModelVAO.class).getVao());
					cb.bindDescriptorSet(((PbrMaterial) m.getMaterial()).getDescriptorSet(), "material");

					pipeline.getUniformAccessor().set("instance_offset", baseIndex);
					cb.drawIndexed(m.getIndexCount(), batchSize, 0, 0);
				}
				baseIndex += batchSize;
			}
		}
		cb.unbindFrameBuffer(frameBuffer);
	}

	@Override
	protected void linkImplementation() {
		addFrameBufferToOutput(frameBuffer);
	}

	@Override
	protected void resizeImplementation(int width, int height) {
		frameBuffer = Graphics.createFramebuffer(width, height, PIPELINE_SOURCE.getShader(ShaderType.FRAGMENT).getOutput());
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
