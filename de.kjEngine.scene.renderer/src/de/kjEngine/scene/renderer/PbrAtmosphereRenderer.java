/**
 * 
 */
package de.kjEngine.scene.renderer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import de.kjEngine.graphics.Color;
import de.kjEngine.graphics.CommandBuffer;
import de.kjEngine.graphics.DescriptorSet;
import de.kjEngine.graphics.FrameBuffer;
import de.kjEngine.graphics.Graphics;
import de.kjEngine.graphics.GraphicsPipeline;
import de.kjEngine.graphics.Texture2D;
import de.kjEngine.graphics.shader.ObjectSource;
import de.kjEngine.graphics.shader.PipelineSource;
import de.kjEngine.graphics.shader.ShaderCompilationException;
import de.kjEngine.graphics.shader.ShaderSource;
import de.kjEngine.io.RL;
import de.kjEngine.io.ResourceManager;
import de.kjEngine.math.Vec3;
import de.kjEngine.renderer.RenderList;
import de.kjEngine.renderer.Renderable.RenderImplementation.ID;
import de.kjEngine.renderer.Stage;
import de.kjEngine.scene.Scene;
import de.kjEngine.scene.atmosphere.PbrAtmosphereComponent;
import de.kjEngine.scene.atmosphere.PbrAtmosphereComponentOpticalDepthLUT;
import de.kjEngine.scene.atmosphere.PbrAtmosphereComponentUBO;
import de.kjEngine.scene.atmosphere.ScenePbrAtmosphereList;
import de.kjEngine.scene.camera.CameraComponent;
import de.kjEngine.scene.camera.CameraComponentUBO;
import de.kjEngine.scene.light.SceneLightBuffer;

/**
 * @author konst
 *
 */
public class PbrAtmosphereRenderer extends Stage {

	private static PipelineSource pipelineSource;
	static {
		try {
			pipelineSource = PipelineSource.parse(RL.create("jar://scene.renderer/de/kjEngine/scene/renderer/pbrAtmosphere.shader"));
			pipelineSource.addDescriptorSet(PbrAtmosphereComponentUBO.DESCRIPTOR_SET_SOURCE);
			pipelineSource.add(CameraComponentUBO.LIBRARY_SOURCE);
			pipelineSource.add(SceneLightBuffer.LIBRARY_SOURCE);
			pipelineSource.addDescriptorSet(PbrAtmosphereComponentOpticalDepthLUT.READ_DESCRIPTOR_SET_SOURCE.shallowCopy().setName("lut"));
			pipelineSource.getShaders()
					.add(ShaderSource.parse(new ObjectSource("vertexShader", ResourceManager.loadTextResource(RL.create("jar://renderer/de/kjEngine/renderer/filterVertexShader.shader"), true))));
		} catch (ShaderCompilationException e) {
			e.printStackTrace();
		}
	}

	private FrameBuffer frameBuffer;
	private GraphicsPipeline pipeline;
	private DescriptorSet descriptorSet;

	private Texture2D base;

	/**
	 * @param inputProvider
	 */
	public PbrAtmosphereRenderer(InputProvider inputProvider) {
		super(inputProvider);

		pipeline = Graphics.createGraphicsPipeline(pipelineSource);

		descriptorSet = Graphics.createDescriptorSet(pipelineSource.getDescriptorSet("data"));
	}

	@Override
	protected void linkImplementation() {
		Texture2D depth = input.get("depth");
		if (depth == null) {
			descriptorSet.set("depth", Color.WHITE.getTexture());
		} else {
			descriptorSet.set("depth", input.get("depth"));
		}
		descriptorSet.update();

		base = input.get("base");

		addFrameBufferToOutput(frameBuffer);
	}

	@Override
	protected void updateDescriptorsImplementation() {
	}

	@Override
	public void getRequiredRenderImplementations(Set<ID> target) {
		target.add(ScenePbrAtmosphereList.ID);
		target.add(PbrAtmosphereComponentUBO.ID);
		target.add(CameraComponent.UBO);
		target.add(Scene.LIGHT_BUFFER);
		target.add(PbrAtmosphereComponentOpticalDepthLUT.ID);
	}

	@Override
	public void dispose() {
		frameBuffer.dispose();
		pipeline.dispose();
	}

	@Override
	protected void renderImplementation(RenderList renderList, CommandBuffer cb) {
		if (base != null) {
			cb.copyTexture2D(base, frameBuffer.getColorAttachment("result"));
		}

		cb.bindFrameBuffer(frameBuffer);
		if (base == null) {
			cb.clearFrameBuffer(frameBuffer);
		}

		Scene scene = renderList.get(Scene.class);
		List<PbrAtmosphereComponent> atmospheres = scene.getRenderImplementation(ScenePbrAtmosphereList.class).atmospheres;
		
		if (!atmospheres.isEmpty()) {
			cb.bindPipeline(pipeline);
			cb.bindDescriptorSet(descriptorSet, "data");
			cb.bindDescriptorSet(scene.camera.getRenderImplementation(CameraComponentUBO.class).getDescriptorSet(), "camera");
			cb.bindDescriptorSet(scene.getRenderImplementation(SceneLightBuffer.class).getDescriptorSet(), "lights");
			
			List<PbrAtmosphereComponent> sortedAtmospheres = new ArrayList<>(atmospheres);
			Vec3 camPos = scene.camera.getParent().transform.getGlobalPosition();
			Collections.sort(sortedAtmospheres, new Comparator<PbrAtmosphereComponent>() {

				@Override
				public int compare(PbrAtmosphereComponent a, PbrAtmosphereComponent b) {
					if (Vec3.distSq(camPos, a.getParent().transform.getGlobalPosition()) > Vec3.distSq(camPos, b.getParent().transform.getGlobalPosition())) {
						return -1;
					}
					return 1;
				}
			});
			
			for (PbrAtmosphereComponent atmosphere : sortedAtmospheres) {
				cb.bindDescriptorSet(atmosphere.getRenderImplementation(PbrAtmosphereComponentUBO.class).descriptorSet, "atmosphere");
				cb.bindDescriptorSet(atmosphere.getRenderImplementation(PbrAtmosphereComponentOpticalDepthLUT.class).readDescriptorSet, "lut");
				
				cb.draw(6, 1, 0, 0);
			}
		}

		cb.unbindFrameBuffer(frameBuffer);
	}

	@Override
	protected void resizeImplementation(int width, int height) {
		if (frameBuffer != null) {
			frameBuffer.dispose();
		}
		frameBuffer = Graphics.createFramebuffer(width, height, pipelineSource);
	}
}
