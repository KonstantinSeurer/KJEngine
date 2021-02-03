/**
 * 
 */
package de.kjEngine.scene.camera;

import java.util.ArrayList;

import de.kjEngine.component.Component.RenderImplementation;
import de.kjEngine.graphics.DescriptorSet;
import de.kjEngine.graphics.DoubleBufferedDescriptor;
import de.kjEngine.graphics.Graphics;
import de.kjEngine.graphics.ShaderBuffer;
import de.kjEngine.renderer.Renderable;

/**
 * @author konst
 *
 */
public class CameraComponentTransformIndexBuffer implements RenderImplementation<CameraComponent> {

	public static final ID ID = new ID(CameraComponent.class, "model_map_transform_index_buffer");

	static {
		Renderable.RenderImplementation.Registry.registerProvider(ID, new Renderable.RenderImplementation.Provider() {

			@Override
			public Renderable.RenderImplementation create() {
				return new CameraComponentTransformIndexBuffer();
			}
		});
	}

	DoubleBufferedDescriptor<ShaderBuffer> indexSsbo;
	DoubleBufferedDescriptor<DescriptorSet> descriptorSet;
	CameraComponent camera;

	int[] batchSizes;

	public CameraComponentTransformIndexBuffer() {
		indexSsbo = new DoubleBufferedDescriptor<ShaderBuffer>(Graphics.createStorageBuffer(SceneTransformIndexBuffer.SSBO_SOURCE, new ArrayList<>(), ShaderBuffer.FLAG_NONE),
				Graphics.createStorageBuffer(SceneTransformIndexBuffer.SSBO_SOURCE, new ArrayList<>(), ShaderBuffer.FLAG_NONE));

		descriptorSet = new DoubleBufferedDescriptor<DescriptorSet>(Graphics.createDescriptorSet(SceneTransformIndexBuffer.DESCRIPTOR_SET_SOURCE),
				Graphics.createDescriptorSet(SceneTransformIndexBuffer.DESCRIPTOR_SET_SOURCE));

		descriptorSet.getA().set("indices", indexSsbo.getA());
		descriptorSet.getA().update();

		descriptorSet.getB().set("indices", indexSsbo.getB());
		descriptorSet.getB().update();
	}

	@Override
	public void dispose() {
	}

	@Override
	public void init(CameraComponent c) {
	}

	@Override
	public void updateDescriptors(CameraComponent c) {
	}

	@Override
	public void render(CameraComponent c) {
		c.getContainer().getRenderImplementation(SceneTransformIndexBuffer.class).buffers.add(this);
		camera = c;
	}
}
