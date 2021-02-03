/**
 * 
 */
package de.kjEngine.scene.atmosphere;

import de.kjEngine.component.Component.RenderImplementation;
import de.kjEngine.graphics.DescriptorSet;
import de.kjEngine.graphics.Graphics;
import de.kjEngine.graphics.SamplingMode;
import de.kjEngine.graphics.Texture2D;
import de.kjEngine.graphics.Texture2DData;
import de.kjEngine.graphics.TextureFormat;
import de.kjEngine.graphics.WrappingMode;
import de.kjEngine.graphics.shader.DescriptorSetSource;
import de.kjEngine.graphics.shader.ImageSource;
import de.kjEngine.graphics.shader.TextureSource;

/**
 * @author konst
 *
 */
public class PbrAtmosphereComponentOpticalDepthLUT implements RenderImplementation<PbrAtmosphereComponent> {
	
	public static final ID ID = new ID(PbrAtmosphereComponent.class, "optical_depth_lut");
	
	static {
		Registry.registerProvider(ID, new Provider() {
			
			@Override
			public de.kjEngine.renderer.Renderable.RenderImplementation create() {
				return new PbrAtmosphereComponentOpticalDepthLUT();
			}
		});
	}
	
	public static final DescriptorSetSource READ_DESCRIPTOR_SET_SOURCE = new DescriptorSetSource("atmosphere");
	static {
		READ_DESCRIPTOR_SET_SOURCE.addDescriptor(new TextureSource("opticalDepth", 2));
	}
	
	public static final DescriptorSetSource WRITE_DESCRIPTOR_SET_SOURCE = new DescriptorSetSource("atmosphere");
	static {
		WRITE_DESCRIPTOR_SET_SOURCE.addDescriptor(new ImageSource("opticalDepth", 2, TextureFormat.R16F, false, true));
	}
	
	public static final int RESOLUTION = 128;
	
	public final Texture2D opticalDepth;
	public final DescriptorSet readDescriptorSet, writeDescriptorSet;
	private boolean update;
	private int lastHashCord;
	
	public PbrAtmosphereComponentOpticalDepthLUT() {
		opticalDepth = Graphics.createTexture2D(new Texture2DData(RESOLUTION, RESOLUTION, 1, null, TextureFormat.R16F, SamplingMode.LINEAR, WrappingMode.CLAMP));
		
		readDescriptorSet = Graphics.createDescriptorSet(READ_DESCRIPTOR_SET_SOURCE);
		readDescriptorSet.set("opticalDepth", opticalDepth);
		readDescriptorSet.update();
		
		writeDescriptorSet = Graphics.createDescriptorSet(WRITE_DESCRIPTOR_SET_SOURCE);
		writeDescriptorSet.set("opticalDepth", opticalDepth.getImage(0));
		writeDescriptorSet.update();
	}

	@Override
	public void dispose() {
	}

	@Override
	public void init(PbrAtmosphereComponent c) {
	}

	@Override
	public void updateDescriptors(PbrAtmosphereComponent c) {
	}

	@Override
	public void render(PbrAtmosphereComponent c) {
		int hashCord = c.hashCode();
		if (lastHashCord != hashCord) {
			lastHashCord = hashCord;
			update = true;
		}
	}
	
	public final boolean pollUpdate() {
		boolean result = update;
		update = false;
		return result;
	}
}
