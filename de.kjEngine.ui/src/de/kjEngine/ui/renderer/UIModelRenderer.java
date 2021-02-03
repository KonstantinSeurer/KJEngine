/**
 * 
 */
package de.kjEngine.ui.renderer;

import java.util.Set;

import de.kjEngine.graphics.CommandBuffer;
import de.kjEngine.graphics.FrameBuffer;
import de.kjEngine.graphics.Graphics;
import de.kjEngine.graphics.GraphicsPipeline;
import de.kjEngine.graphics.Texture2D;
import de.kjEngine.graphics.shader.PipelineSource;
import de.kjEngine.graphics.shader.ShaderCompilationException;
import de.kjEngine.io.RL;
import de.kjEngine.math.Vec2;
import de.kjEngine.renderer.RenderList;
import de.kjEngine.renderer.Renderable.RenderImplementation.ID;
import de.kjEngine.renderer.Stage;
import de.kjEngine.ui.UIComponent;
import de.kjEngine.ui.UIScene;
import de.kjEngine.ui.Window;
import de.kjEngine.ui.model.ModelComponent;
import de.kjEngine.ui.model.ModelVAO;
import de.kjEngine.ui.model.StandartMaterialDescriptorSet;

/**
 * @author konst
 *
 */
public class UIModelRenderer extends Stage {

	private static PipelineSource PIPELINE_SOURCE;
	static {
		try {
			PIPELINE_SOURCE = PipelineSource.parse(RL.create("jar://ui/de/kjEngine/ui/renderer/model.shader"));
			PIPELINE_SOURCE.addDescriptorSet(StandartMaterialDescriptorSet.DESCRIPTOR_SET_SOURCE);
		} catch (ShaderCompilationException e) {
			e.printStackTrace();
		}
	}

	private Texture2D base;

	private FrameBuffer frameBuffer;
	private GraphicsPipeline pipeline;

	public UIModelRenderer(InputProvider input) {
		super(input);

		pipeline = Graphics.createGraphicsPipeline(PIPELINE_SOURCE);
	}

	@Override
	public void dispose() {
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

		UIScene scene = renderList.get(UIScene.class);

		if (scene != null) {
			cb.bindPipeline(pipeline);
			pipeline.getUniformAccessor().set("invWindowSize", Vec2.create(2f / Window.getWidth(), 2f / Window.getHeight()));

			for (int layerIndex = 0; layerIndex < scene.layers.size(); layerIndex++) {
				for (UIComponent<?, ?> ui : scene.layers.get(layerIndex)) {
					if (ui instanceof ModelComponent) {
						ModelComponent model = (ModelComponent) ui;
						if (model.model != null) {
							pipeline.getUniformAccessor().set("transform", model.getParent().globalTransform);
							cb.bindVertexArray(model.model.getRenderImplementation(ModelVAO.class).getVao());
							cb.bindDescriptorSet(model.model.material.getRenderImplementation(StandartMaterialDescriptorSet.class).descriptorSet, "material");
							cb.drawIndexed(model.model.getIndexCount(), 1, 0, 0);
						}
					}
				}
			}
		}

		cb.unbindFrameBuffer(frameBuffer);
	}

	@Override
	protected void linkImplementation() {
		base = input.get("base");

		addFrameBufferToOutput(frameBuffer);
	}

	@Override
	protected void resizeImplementation(int width, int height) {
		if (frameBuffer != null) {
			frameBuffer.dispose();
		}
		frameBuffer = Graphics.createFramebuffer(width, height, PIPELINE_SOURCE);
	}

	@Override
	protected void updateDescriptorsImplementation() {
	}

	@Override
	public void getRequiredRenderImplementations(Set<ID> target) {
		target.add(ModelVAO.ID);
		target.add(StandartMaterialDescriptorSet.ID);
	}
}
