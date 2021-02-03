/**
 * 
 */
package de.kjEngine.scene.atmosphere;

import de.kjEngine.component.Component.RenderImplementation;

/**
 * @author konst
 *
 */
public class PbrAtmosphereComponentPbrAtmosphereList implements RenderImplementation<PbrAtmosphereComponent> {
	
	public static final ID ID = new ID(PbrAtmosphereComponent.class, "pbr_atmosphere_list");
	
	static {
		Registry.registerProvider(ID, new Provider() {
			
			@Override
			public de.kjEngine.renderer.Renderable.RenderImplementation create() {
				return new PbrAtmosphereComponentPbrAtmosphereList();
			}
		});
	}

	public PbrAtmosphereComponentPbrAtmosphereList() {
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
		c.getContainer().getRenderImplementation(ScenePbrAtmosphereList.class).atmospheres.add(c);
	}
}
