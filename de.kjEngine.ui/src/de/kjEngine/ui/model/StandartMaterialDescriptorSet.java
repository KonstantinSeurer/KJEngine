/**
 * 
 */
package de.kjEngine.ui.model;

import de.kjEngine.graphics.DescriptorSet;
import de.kjEngine.graphics.Graphics;
import de.kjEngine.graphics.shader.DescriptorSetSource;
import de.kjEngine.graphics.shader.TextureSource;
import de.kjEngine.ui.model.Material.RenderImplementation;

/**
 * @author konst
 *
 */
public class StandartMaterialDescriptorSet implements RenderImplementation {
	
	public static final ID ID = new ID(StandartMaterial.class, "descriptor_set");
	static {
		Registry.registerProvider(ID, new Provider() {
			
			@Override
			public de.kjEngine.renderer.Renderable.RenderImplementation create() {
				return new StandartMaterialDescriptorSet();
			}
		});
	}

	public static final DescriptorSetSource DESCRIPTOR_SET_SOURCE = new DescriptorSetSource("material");
	static {
		DESCRIPTOR_SET_SOURCE.addDescriptor(new TextureSource("texture", 2));
	}

	public final DescriptorSet descriptorSet;

	public StandartMaterialDescriptorSet() {
		descriptorSet = Graphics.createDescriptorSet(DESCRIPTOR_SET_SOURCE);
	}

	@Override
	public void dispose() {
		descriptorSet.dispose();
	}

	@Override
	public void updateDescriptors(Material material) {
		descriptorSet.set("texture", ((StandartMaterial) material).getTexture());
		descriptorSet.update();
	}
}
