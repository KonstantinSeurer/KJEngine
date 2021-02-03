package de.kjEngine.core;

import java.util.Set;

import de.kjEngine.renderer.RenderList;
import de.kjEngine.renderer.Renderable;
import de.kjEngine.util.Disposable;

public interface StateHandler extends  Disposable {
	
	public void init();
	
	public void update(float delta);
	
	public void updateDescriptors();
	
	public void render(RenderList renderList);
	
	public void initRenderer(Set<Renderable.RenderImplementation.ID> requiredImplementations);
}
