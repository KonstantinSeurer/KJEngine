/**
 * 
 */
package de.kjEngine.scene.light;

import de.kjEngine.component.Component.RenderImplementation;

/**
 * @author konst
 *
 */
public class ShadowMapComponentShadowMapList implements RenderImplementation<ShadowMapComponent<?>> {
	
	public static final ID ID = new ID(ShadowMapComponent.class, "shadow_map_list");
	static {
		Registry.registerProvider(ID, new Provider() {
			
			@Override
			public de.kjEngine.renderer.Renderable.RenderImplementation create() {
				return new ShadowMapComponentShadowMapList();
			}
		});
	}

	public ShadowMapComponentShadowMapList() {
	}

	@Override
	public void dispose() {
	}

	@Override
	public void init(ShadowMapComponent<?> c) {
	}

	@Override
	public void updateDescriptors(ShadowMapComponent<?> c) {
	}

	@Override
	public void render(ShadowMapComponent<?> c) {
		c.getContainer().getRenderImplementation(SceneShadowMapList.class).shadowMaps.add(c);
	}
}
