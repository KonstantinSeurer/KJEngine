/**
 * 
 */
package de.kjEngine.scene.renderer.shadows;

import java.util.Set;

import de.kjEngine.graphics.CommandBuffer;
import de.kjEngine.graphics.Texture2D;
import de.kjEngine.renderer.RenderList;
import de.kjEngine.renderer.Renderable.RenderImplementation.ID;
import de.kjEngine.scene.light.ShadowMapComponent;
import de.kjEngine.scene.light.ShadowMapComponentDepth;
import de.kjEngine.renderer.Stage;

/**
 * @author konst
 *
 */
public class ShadowDepthMapOutput extends Stage {
	
	private Texture2D depth;

	public ShadowDepthMapOutput(InputProvider input) {
		super(input);
	}

	@Override
	public void dispose() {
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected void renderImplementation(RenderList renderList, CommandBuffer cb) {
		ShadowMapComponent shadowMap = renderList.get(ShadowMapComponent.class);
		cb.copyTexture2D(depth, ((ShadowMapComponentDepth) shadowMap.renderImplementationMap.get(ShadowMapComponentDepth.ID)).getDepth());
	}

	@Override
	protected void linkImplementation() {
		depth = input.get("depth");
	}

	@Override
	protected void resizeImplementation(int width, int height) {
	}

	@Override
	protected void updateDescriptorsImplementation() {
	}

	@Override
	public void getRequiredRenderImplementations(Set<ID> target) {
		target.add(ShadowMapComponentDepth.ID);
	}
}
