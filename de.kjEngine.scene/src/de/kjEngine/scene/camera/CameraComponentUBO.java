/**
 * 
 */
package de.kjEngine.scene.camera;

import java.util.ArrayList;

import de.kjEngine.component.Component.RenderImplementation;
import de.kjEngine.graphics.DescriptorSet;
import de.kjEngine.graphics.Graphics;
import de.kjEngine.graphics.ShaderBuffer;
import de.kjEngine.graphics.StructAccessor;
import de.kjEngine.graphics.shader.BufferSource;
import de.kjEngine.graphics.shader.DescriptorSetSource;
import de.kjEngine.graphics.shader.PipelineSource;
import de.kjEngine.io.RL;

/**
 * @author konst
 *
 */
public class CameraComponentUBO implements RenderImplementation<CameraComponent> {

	static {
		PipelineSource.addLibrary("cameraUbo", RL.create("jar://scene/de/kjEngine/scene/camera/cameraUbo.shader"));
	}
	
	public static final PipelineSource LIBRARY_SOURCE = PipelineSource.getLibrary("cameraUbo");
	public static final DescriptorSetSource DESCRIPTOR_SET_SOURCE = LIBRARY_SOURCE.getDescriptorSet("camera");
	public static final BufferSource UBO_SOURCE = DESCRIPTOR_SET_SOURCE.getBuffer("transforms");
	
	private ShaderBuffer ubo;
	private DescriptorSet descriptorSet;
	
	public CameraComponentUBO() {
		ubo = Graphics.createUniformBuffer(UBO_SOURCE, new ArrayList<>(), ShaderBuffer.FLAG_NONE);
		descriptorSet = Graphics.createDescriptorSet(DESCRIPTOR_SET_SOURCE);
		descriptorSet.set("transforms", ubo);
		descriptorSet.update();
	}

	@Override
	public void dispose() {
		ubo.dispose();
		descriptorSet.dispose();
	}

	@Override
	public void updateDescriptors(CameraComponent component) {
		StructAccessor accessor = ubo.getAccessor();
		accessor.set("view", component.getView());
		accessor.set("projection", component.getFrustum().getProjection());
		accessor.set("viewProjection", component.getFrustum().getViewProjection());
		accessor.set("invView", component.getInvView());
		accessor.set("invProjection", component.getInvProjection());
		accessor.set("invViewProjection", component.getInvViewProjection());
		accessor.set("position", component.getParent().transform.getGlobalPosition());
		ubo.update();
	}

	@Override
	public void render(CameraComponent component) {
	}

	/**
	 * @return the ubo
	 */
	public ShaderBuffer getUbo() {
		return ubo;
	}

	/**
	 * @return the descriptorSet
	 */
	public DescriptorSet getDescriptorSet() {
		return descriptorSet;
	}

	@Override
	public void init(CameraComponent component) {
	}
}
