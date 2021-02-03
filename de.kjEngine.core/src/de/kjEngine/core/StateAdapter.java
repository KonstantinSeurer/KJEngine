/**
 * 
 */
package de.kjEngine.core;

import java.util.Set;

import de.kjEngine.renderer.RenderList;
import de.kjEngine.renderer.Renderable.RenderImplementation.ID;

/**
 * @author konst
 *
 */
public abstract class StateAdapter implements StateHandler {

	public StateAdapter() {
	}

	@Override
	public void dispose() {
	}

	@Override
	public void update(float delta) {
	}

	@Override
	public void render(RenderList renderList) {
	}

	@Override
	public void init() {
	}

	@Override
	public void updateDescriptors() {
	}

	@Override
	public void initRenderer(Set<ID> requiredImplementations) {
	}
}
