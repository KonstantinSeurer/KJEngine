/**
 * 
 */
package de.kjEngine.scene.light;

import de.kjEngine.component.Component.RenderImplementation;
import de.kjEngine.graphics.Graphics;
import de.kjEngine.graphics.SamplingMode;
import de.kjEngine.graphics.Texture2D;
import de.kjEngine.graphics.Texture2DData;
import de.kjEngine.graphics.TextureFormat;
import de.kjEngine.graphics.WrappingMode;

/**
 * @author konst
 *
 */
public class ShadowMapComponentDepth implements RenderImplementation<ShadowMapComponent<?>> {
	
	public static final ID ID = new ID(ShadowMapComponent.class, "depth");
	static {
		Registry.registerProvider(ID, new Provider() {
			
			@Override
			public de.kjEngine.renderer.Renderable.RenderImplementation create() {
				return new ShadowMapComponentDepth();
			}
		});
	}

	private Texture2D depth;

	public ShadowMapComponentDepth() {
	}

	@Override
	public void dispose() {
	}

	@Override
	public void init(ShadowMapComponent<?> c) {
		if (depth == null) {
			depth = Graphics.createTexture2D(new Texture2DData(c.resolution, c.resolution, 1, null, TextureFormat.R32F, SamplingMode.LINEAR, WrappingMode.CLAMP));
		}
	}

	@Override
	public void updateDescriptors(ShadowMapComponent<?> c) {
	}

	@Override
	public void render(ShadowMapComponent<?> c) {
	}

	/**
	 * @return the depth
	 */
	public Texture2D getDepth() {
		return depth;
	}
}
