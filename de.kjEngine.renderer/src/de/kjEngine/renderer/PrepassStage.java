/**
 * 
 */
package de.kjEngine.renderer;

import java.util.Set;

import de.kjEngine.graphics.CommandBuffer;
import de.kjEngine.util.Disposable;

/**
 * @author konst
 *
 */
public interface PrepassStage extends Disposable {

	public void updateDescriptors();
	
	public void render(RenderList renderList, CommandBuffer cb);
	
	public abstract void getRequiredRenderImplementations(Set<Renderable.RenderImplementation.ID> target);
}
