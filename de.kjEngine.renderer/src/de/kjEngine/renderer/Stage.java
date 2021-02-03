/**
 * 
 */
package de.kjEngine.renderer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.kjEngine.graphics.CommandBuffer;
import de.kjEngine.graphics.FrameBuffer;
import de.kjEngine.graphics.Texture2D;
import de.kjEngine.graphics.shader.VariableSource;
import de.kjEngine.util.Disposable;

/**
 * @author konst
 *
 */
public abstract class Stage implements Disposable {

	public static interface InputProvider {

		public void link();

		public void reset();

		public void render(RenderList renderList, CommandBuffer cb);

		public Texture2D get(String name);
		
		public void updateDescriptors();

		public void prepareResize();

		public void resize(int width, int height);
	}

	protected InputProvider input;

	private boolean linked, resized;
	private boolean rendered, updated;
	protected Map<String, Texture2D> output = new HashMap<>();

	protected Stage(InputProvider input) {
		this.input = input;
	}

	public final void reset() {
		rendered = false;
		updated = false;
		input.reset();
	}
	
	public final void render(RenderList renderList, CommandBuffer cb) {
		if (rendered) {
			return;
		}
		input.render(renderList, cb);
		renderImplementation(renderList, cb);
		rendered = true;
	}

	protected abstract void renderImplementation(RenderList renderList, CommandBuffer cb);

	public final void link() {
		if (linked) {
			return;
		}
		linked = true;
		input.link();
		output.clear();
		linkImplementation();
	}

	protected abstract void linkImplementation();
	
	public final void prepareResize() {
		resized = false;
		linked = false;
		input.prepareResize();
	}
	
	public final void resize(int width, int height) {
		if (resized) {
			return;
		}
		input.resize(width, height);
		resizeImplementation(width, height);
		resized = true;
	}

	protected abstract void resizeImplementation(int width, int height);

	public final void updateDescriptors() {
		if (updated) {
			return;
		}
		input.updateDescriptors();
		updateDescriptorsImplementation();
		updated = true;
	}
	
	protected abstract void updateDescriptorsImplementation();

	/**
	 * @return the output
	 */
	public Map<String, Texture2D> getOutput() {
		return output;
	}
	
	protected void addFrameBufferToOutput(FrameBuffer frameBuffer) {
		for (VariableSource attachment : frameBuffer.getSource().getVariables()) {
			String name = attachment.getName();
			output.put(name, frameBuffer.getColorAttachment(name));
		}
		if (frameBuffer.getDepthAttachment() != null) {
			output.put("depth", frameBuffer.getDepthAttachment());
		}
	}
	
	public abstract void getRequiredRenderImplementations(Set<Renderable.RenderImplementation.ID> target);
}
