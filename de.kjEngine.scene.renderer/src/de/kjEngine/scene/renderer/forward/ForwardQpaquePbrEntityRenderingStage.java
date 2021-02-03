/**
 * 
 */
package de.kjEngine.scene.renderer.forward;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.kjEngine.graphics.CommandBuffer;
import de.kjEngine.renderer.RenderList;
import de.kjEngine.renderer.Renderable.RenderImplementation.ID;
import de.kjEngine.renderer.Stage;
import de.kjEngine.scene.Entity;
import de.kjEngine.scene.model.Model;

/**
 * @author konst
 *
 */
public class ForwardQpaquePbrEntityRenderingStage extends Stage {

//	public static final String INPUT_TEXTURE_BASE_NAME = "base";
//
//	public static final String OUTPUT_TEXTURE_RESULT_NAME = "result";
//
//	public static final FrameBuffer.Layout BUFFER_LAYOUT = Graphics.createFramebufferLayout(new DepthAttachment(), new ColorAttachment(TextureFormat.RGB16F));
//
//	public static final ShaderBuffer.Layout INDEX_SSBO_LAYOUT = new ShaderBuffer.Layout.Std430(new GraphicsPipeline.Struct(new GraphicsPipeline.Array(GraphicsPipeline.DataType.FLOAT, 100000)));
//
//	public static final DescriptorSet.Layout DESCRIPTOR_SET_LAYOUT = Graphics.createDescriptorSetLayout();
//	static {
//		DESCRIPTOR_SET_LAYOUT.set(0, Descriptor.Type.STORAGE_BUFFER);
//	}
//
//	private FrameBuffer buffer;
//	private ShaderBuffer indexSsbo;
//	private DescriptorSet descriptorSet;
//	private GraphicsPipeline shader;

	public ForwardQpaquePbrEntityRenderingStage(InputProvider input) {
		super(input);

//		buffer = Graphics.createFramebuffer(width, height, BUFFER_LAYOUT);
//
//		GraphicsPipeline.Info info = GraphicsPipeline.Info.load(RL.create("jar://engine/de/kjEngine/core/scene/renderer/pipeline/forward/shaders/opaquePbr.shader"));
//		info.framebufferLayout = BUFFER_LAYOUT;
//		info.descriptorSetLayouts.add(Scene.DESCRIPTOR_SET_LAYOUT);
//		info.descriptorSetLayouts.add(DESCRIPTOR_SET_LAYOUT);
//		info.descriptorSetLayouts.add(OpaquePbrMaterial.DESCRIPTOR_SET_SOURCE);
//		info.depthTest = true;
//		info.depthClamp = true;
//		info.depthWrite = true;
//		info.blendMode = GraphicsPipeline.Info.BlendMode.NONE;
//		info.cullMode = GraphicsPipeline.Info.CullMode.BACK;
//		info.frontFace = GraphicsPipeline.Info.FrontFace.CW;
//		info.uniformLayout = new ShaderBuffer.Layout.Std140(new GraphicsPipeline.Struct(GraphicsPipeline.DataType.FLOAT));
//		info.viewportSize = new Dimension(width, height);
//		shader = Graphics.createGraphicsPipeline(info);
//
//		buffer = Graphics.createFramebuffer(width, height, BUFFER_LAYOUT);
//
//		indexSsbo = Graphics.createStorageBuffer(INDEX_SSBO_LAYOUT, ShaderBuffer.FLAG_NONE);
//
//		descriptorSet = Graphics.createDescriptorSet(DESCRIPTOR_SET_LAYOUT);
//		descriptorSet.set(0, indexSsbo);
//		descriptorSet.update();
//
//		copyFilter = new NullFilter(new InputProvider() {
//
//			@Override
//			public void reset(Scene scene) {
//				input.reset(scene);
//			}
//
//			@Override
//			public void render(Scene scene, CommandBuffer cb) {
//				input.render(scene, cb);
//			}
//
//			@Override
//			public void link() {
//				input.link();
//			}
//
//			@Override
//			public Texture2D get(String name) {
//				return input.get(INPUT_TEXTURE_BASE_NAME);
//			}
//
//			@Override
//			public void update(Scene scene) {
//				input.update(scene);
//			}
//		}, buffer);
	}

	@Override
	public void dispose() {
//		buffer.dispose();
	}

	private Map<Model, List<Entity>> entities_map = new HashMap<>();

	@Override
	protected void renderImplementation(RenderList renderLists, CommandBuffer cb) {
//		buffer.bindFrameBuffer(cb);
//		buffer.clearFrameBuffer(cb);
//		buffer.unbindFrameBuffer(cb);
//
//		copyFilter.reset(scene);
//		copyFilter.render(scene, cb);
//
//		buffer.bindFrameBuffer(cb);
//
//		if (!entities_map.isEmpty()) {
//			int i = 0;
//			shader.bindFrameBuffer(cb);
//			scene.getDescriptorSet().bindDescriptorSet(cb, 0);
//			descriptorSet.bindDescriptorSet(cb, 1);
//
//			for (Model m : entities_map.keySet()) {
//				m.getRasterizationImplementation().getVao().bindFrameBuffer(cb);
//
//				((OpaquePbrMaterial) m.getMaterial()).getDescriptorSet().bindDescriptorSet(cb, 2);
//
//				int count = entities_map.get(m).size();
//
//				shader.setUniform(cb, 0, i);
//				shader.drawIndexed(cb, m.getIndexCount(), count, 0, 0);
//
//				i += count;
//			}
//		}
//
//		buffer.unbindFrameBuffer(cb);
	}

	@Override
	protected void linkImplementation() {
//		output.put(OUTPUT_TEXTURE_RESULT_NAME, buffer.getColorAttachment(0));
//
//		copyFilter.link();
	}

	@Override
	protected void updateDescriptorsImplementation() {
//		int i = 0;
//		for (Model m : entities_map.keySet()) {
//			for (Entity e : entities_map.get(m)) {
//				for (ModelInstance model : e.getModels()) {
//					if (model.getModel() == m) {
//						indexSsbo.setMember(i++, model.getTransformIndex());
//						break;
//					}
//				}
//			}
//		}
//		indexSsbo.update();
	}

	@Override
	public void getRequiredRenderImplementations(Set<ID> target) {
	}

	@Override
	protected void resizeImplementation(int width, int height) {
	}
}
