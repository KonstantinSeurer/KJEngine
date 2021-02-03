/**
 * 
 */
package de.kjEngine.scene.renderer;

import java.util.Set;

import de.kjEngine.graphics.CommandBuffer;
import de.kjEngine.graphics.DescriptorSet;
import de.kjEngine.graphics.FrameBuffer;
import de.kjEngine.graphics.GraphicsPipeline;
import de.kjEngine.renderer.RenderList;
import de.kjEngine.renderer.Renderable.RenderImplementation.ID;
import de.kjEngine.renderer.Stage;

/**
 * @author konst
 *
 */
public class VolumeRenderer extends Stage {

//	private static final DescriptorSet.Layout DESCRIPTOR_SET_LAYOUT = Graphics.createDescriptorSetLayout();
//	static {
//		DESCRIPTOR_SET_LAYOUT.set(0, Descriptor.Type.TEXTURE_2D);
//		DESCRIPTOR_SET_LAYOUT.set(1, Descriptor.Type.TEXTURE_2D);
//	}
//
//	public static final ShaderBuffer.Layout INDEX_SSBO_LAYOUT = new ShaderBuffer.Layout.Std430(new GraphicsPipeline.Struct(new GraphicsPipeline.Array(GraphicsPipeline.DataType.FLOAT, 1000)));
//
//	public static final String INPUT_TEXTURE_COLOR_NAME = "color";
//	public static final String INPUT_TEXTURE_DEPTH_NAME = "depth";
//
//	public static final String OUTPUT_TEXTURE_RESULT_NAME = "result";
//	
//	private static final FrameBuffer.Layout FRAME_BUFFER_LAYOUT = Graphics.createFramebufferLayout(null, new ColorAttachment(TextureFormat.RGBA8));

	private DescriptorSet descriptorSet;

	protected FrameBuffer frameBuffer;
	protected GraphicsPipeline shader;

	public VolumeRenderer(InputProvider input) {
		super(input);

//		frameBuffer = Graphics.createFramebuffer(width, height, FRAME_BUFFER_LAYOUT);
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
//				return input.get(INPUT_TEXTURE_COLOR_NAME);
//			}
//
//			@Override
//			public void update(Scene scene) {
//				input.update(scene);
//			}
//		}, frameBuffer);
//
//		GraphicsPipeline.Info info = GraphicsPipeline.Info.load(RL.create("jar://engine/de/kjEngine/core/scene/renderer/shaders/volume.shader"));
//		info.descriptorSetLayouts.add(DESCRIPTOR_SET_LAYOUT);
//		info.descriptorSetLayouts.add(Scene.DESCRIPTOR_SET_LAYOUT);
//		info.descriptorSetLayouts.add(VolumeMaterial.DESCRIPTOR_SET_LAYOUT);
//		info.framebufferLayout = FRAME_BUFFER_LAYOUT;
//		info.primitiveType = GraphicsPipeline.Info.PrimitiveType.TRIANGLE_LIST;
//		info.drawMode = GraphicsPipeline.Info.DrawMode.FILL;
//		info.cullMode = GraphicsPipeline.Info.CullMode.BACK;
//		info.frontFace = GraphicsPipeline.Info.FrontFace.CW;
//		info.depthTest = false;
//		info.depthClamp = false;
//		info.depthWrite = false;
//		info.blendMode = GraphicsPipeline.Info.BlendMode.ALPHA;
//		info.viewportSize = new Dimension(width, height);
//
//		shader = Graphics.createGraphicsPipeline(info);
//		
//		descriptorSet = Graphics.createDescriptorSet(DESCRIPTOR_SET_LAYOUT);
	}

	@Override
	public void linkImplementation() {
//		descriptorSet.set(0, copyFilter.getOutput().get(NullFilter.OUTPUT_TEXTURE_RESULT_NAME));
//		descriptorSet.set(1, input.get(INPUT_TEXTURE_DEPTH_NAME));
//		descriptorSet.update();
//
//		output.put(OUTPUT_TEXTURE_RESULT_NAME, frameBuffer.getColorAttachment(0));
//
//		copyFilter.link();
	}

	@Override
	public void dispose() {
//		shader.dispose();
//		descriptorSet.dispose();
//		frameBuffer.dispose();
//		copyFilter.dispose();
	}

	@Override
	protected void renderImplementation(RenderList renderList, CommandBuffer cb) {
//		List<Entity> clouds = new ArrayList<>();
//		for (Entity e : scene.getEntityBuffer().getEntities()) {
//			ModelComponent m = e.getModel();
//			if (m != null && m.getModel().getMaterial() instanceof VolumeMaterial) {
//				clouds.add(e);
//			}
//		}
//
//		Vec3 cameraPos = scene.getCamera().getTransform().getGlobalPosition();
//		Collections.<Entity>sort(clouds, new Comparator<Entity>() {
//
//			@Override
//			public int compare(Entity o1, Entity o2) {
//				float d0 = Vec3.distSq(cameraPos, o1.getTransform().getGlobalPosition());
//				float d1 = Vec3.distSq(cameraPos, o2.getTransform().getGlobalPosition());
//				if (d0 < d1) {
//					return 1;
//				} else if (Math.abs(d0 - d1) < 0.001f) {
//					return 0;
//				}
//				return -1;
//			}
//		});
//
//		copyFilter.reset(scene);
//		copyFilter.render(scene, cb);
//
//		frameBuffer.bindFrameBuffer(cb);
//
//		shader.bindFrameBuffer(cb);
//		descriptorSet.bindDescriptorSet(cb, 0);
//		scene.getDescriptorSet().bindDescriptorSet(cb, 1);
//
//		Model.CUBE.getRasterizationImplementation().getVao().bindFrameBuffer(cb);
//
//		Material prevMaterial = null;
//
//		for (int i = 0; i < clouds.size(); i++) {
//			Material mat = clouds.get(i).getModel().getModel().getMaterial();
//			if (mat != prevMaterial) {
//				((VolumeMaterial) mat).getDescriptorSet().bindDescriptorSet(cb, 2);
//				prevMaterial = mat;
//			}
//
//			shader.setUniform(cb, 0, clouds.get(i).getTransform().getGlobalTransform());
//
//			shader.drawIndexed(cb, Model.CUBE.getIndexCount(), 1, 0, 0);
//		}
//
//		frameBuffer.unbindFrameBuffer(cb);
	}

	@Override
	protected void updateDescriptorsImplementation() {
	}

	@Override
	public void getRequiredRenderImplementations(Set<ID> target) {
	}

	@Override
	protected void resizeImplementation(int width, int height) {
	}
}
