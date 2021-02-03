/**
 * 
 */
package de.kjEngine.scene.renderer.deferred;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.kjEngine.graphics.CommandBuffer;
import de.kjEngine.graphics.DescriptorSet;
import de.kjEngine.graphics.Graphics;
import de.kjEngine.graphics.GraphicsPipeline;
import de.kjEngine.graphics.shader.PipelineSource;
import de.kjEngine.graphics.shader.ShaderCompilationException;
import de.kjEngine.graphics.shader.ShaderType;
import de.kjEngine.io.RL;
import de.kjEngine.renderer.RenderList;
import de.kjEngine.renderer.Renderable.RenderImplementation.ID;
import de.kjEngine.scene.Scene;
import de.kjEngine.scene.camera.CameraComponentUBO;
import de.kjEngine.scene.decal.DecalComponent;
import de.kjEngine.scene.material.PbrMaterial;
import de.kjEngine.scene.model.Model;
import de.kjEngine.scene.model.ModelVAO;

/**
 * @author konst
 *
 */
public class DeferredPbrDecalRenderer extends DeferredPbrRenderer {

	private static final PipelineSource PIPELINE_SOURCE;
	static {
		PipelineSource source = null;
		try {
			source = PipelineSource.parse(RL.create("jar://scene.renderer/de/kjEngine/scene/renderer/deferred/pbrDecal.shader"));
		} catch (ShaderCompilationException e) {
			e.printStackTrace();
		}
		source.add(CameraComponentUBO.LIBRARY_SOURCE);
		source.getDescriptorSets().add(PbrMaterial.DESCRIPTOR_SET_SOURCE);
		source.getShader(ShaderType.FRAGMENT).setOutput(FRAGMENT_SHADER_OUTPUT);
		PIPELINE_SOURCE = source;
	}

	private GraphicsPipeline pipeline;
	private DescriptorSet descriptorSet;

	private List<DecalComponent> decalQueue = new ArrayList<>();

	public DeferredPbrDecalRenderer(InputProvider inputProvider) {
		super(inputProvider);

		pipeline = Graphics.createGraphicsPipeline(PIPELINE_SOURCE);
		
		descriptorSet = Graphics.createDescriptorSet(PIPELINE_SOURCE.getDescriptorSet("data"));
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	protected void renderImplementation(RenderList renderList, CommandBuffer cb) {
		super.renderImplementation(renderList, cb);

		cb.bindFrameBuffer(frameBuffer);

		cb.bindPipeline(pipeline);

		cb.bindDescriptorSet(renderList.get(Scene.class).camera.getRenderImplementation(CameraComponentUBO.class).getDescriptorSet(), "camera");
		cb.bindDescriptorSet(descriptorSet, "data");

		Model cube = Model.getDefaultCube();

		cb.bindVertexArray(cube.getRenderImplementation(ModelVAO.class).getVao());

		for (DecalComponent decal : decalQueue) {
			cb.bindDescriptorSet(((PbrMaterial) decal.getMaterial()).getDescriptorSet(), "material");
			pipeline.getUniformAccessor().set("transform", decal.getParent().transform.globalTransform);
			cb.drawIndexed(cube.getIndexCount(), 1, 0, 0);
		}
		
		decalQueue.clear();

		cb.unbindFrameBuffer(frameBuffer);
	}

	@Override
	protected void linkImplementation() {
		super.linkImplementation();
		
		descriptorSet.set("depth", input.get("depth"));
		descriptorSet.update();
	}

	@Override
	protected void updateDescriptorsImplementation() {
	}

	@Override
	public void getRequiredRenderImplementations(Set<ID> target) {
	}

	/**
	 * @return the decalQueue
	 */
	public List<DecalComponent> getDecalQueue() {
		return decalQueue;
	}
}
