/**
 * 
 */
package de.kjEngine.scene.atmosphere;

import java.util.ArrayList;
import java.util.Arrays;

import de.kjEngine.component.Component.RenderImplementation;
import de.kjEngine.graphics.Descriptor.Type;
import de.kjEngine.graphics.DescriptorSet;
import de.kjEngine.graphics.Graphics;
import de.kjEngine.graphics.ShaderBuffer;
import de.kjEngine.graphics.shader.BufferSource;
import de.kjEngine.graphics.shader.BufferSource.Layout;
import de.kjEngine.graphics.shader.DescriptorSetSource;
import de.kjEngine.math.Vec4;

/**
 * @author konst
 *
 */
public class PbrAtmosphereComponentUBO implements RenderImplementation<PbrAtmosphereComponent> {
	
	public static final ID ID = new ID(PbrAtmosphereComponent.class, "ubo");
	
	static {
		Registry.registerProvider(ID, new Provider() {
			
			@Override
			public de.kjEngine.renderer.Renderable.RenderImplementation create() {
				return new PbrAtmosphereComponentUBO();
			}
		});
	}
	
	public static final BufferSource UBO_SOURCE = new BufferSource("settings", Type.UNIFORM_BUFFER, Layout.STANDARD);
	static {
		UBO_SOURCE.addMember("vec4", "radius_density");
		UBO_SOURCE.addMember("vec4", "position");
	}
	
	public static final DescriptorSetSource DESCRIPTOR_SET_SOURCE = new DescriptorSetSource("atmosphere", Arrays.asList(UBO_SOURCE));

	private ShaderBuffer ubo;
	public final DescriptorSet descriptorSet;
	
	public PbrAtmosphereComponentUBO() {
		ubo = Graphics.createUniformBuffer(UBO_SOURCE, new ArrayList<>(), ShaderBuffer.FLAG_NONE);
		
		descriptorSet = Graphics.createDescriptorSet(DESCRIPTOR_SET_SOURCE);
		descriptorSet.set("settings", ubo);
		descriptorSet.update();
	}

	@Override
	public void dispose() {
	}

	@Override
	public void init(PbrAtmosphereComponent c) {
	}

	@Override
	public void updateDescriptors(PbrAtmosphereComponent c) {
		ubo.getAccessor().set("position", c.getParent().transform.getGlobalPosition());
		ubo.getAccessor().set("radius_density", Vec4.create(c.innerRadius, c.outerRadius, c.baseDensity, c.densityFalloff));
		ubo.update();
	}

	@Override
	public void render(PbrAtmosphereComponent c) {
	}
}
