/**
 * 
 */
package de.kjEngine.scene.renderer;

import java.util.Set;

import de.kjEngine.graphics.CommandBuffer;
import de.kjEngine.renderer.RenderList;
import de.kjEngine.renderer.Renderable.RenderImplementation.ID;
import de.kjEngine.renderer.Stage;

/**
 * @author konst
 *
 */
public class SkyRenderer extends Stage {

	public SkyRenderer(InputProvider input) {
		super(input);
	}

	@Override
	public void dispose() {
	}

	@Override
	protected void renderImplementation(RenderList renderList, CommandBuffer cb) {
	}

	@Override
	protected void linkImplementation() {
	}

	@Override
	protected void resizeImplementation(int width, int height) {
	}

	@Override
	protected void updateDescriptorsImplementation() {
	}

	@Override
	public void getRequiredRenderImplementations(Set<ID> target) {
	}
}
