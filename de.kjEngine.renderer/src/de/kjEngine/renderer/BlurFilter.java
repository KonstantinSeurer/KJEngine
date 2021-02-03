/**
 * 
 */
package de.kjEngine.renderer;

import java.util.Set;

import de.kjEngine.graphics.CommandBuffer;
import de.kjEngine.graphics.Texture2D;
import de.kjEngine.math.Vec2;
import de.kjEngine.renderer.Renderable.RenderImplementation.ID;

/**
 * @author konst
 *
 */
public class BlurFilter extends Stage {
	
	public static final String INPUT_TEXTURE_TEXTURE_NAME = SingleAxisBlurFilter.INPUT_TEXTURE_TEXTURE_NAME;
	public static final String OUTPUT_TEXTURE_RESULT_NAME = "result";
	
	private SingleAxisBlurFilter horizontal, vertical;
	
	private float aspect;
	private float radius;

	public BlurFilter(InputProvider input) {
		super(null);
		
		horizontal = new SingleAxisBlurFilter(input);
		vertical = new SingleAxisBlurFilter(new InputProvider() {
			
			@Override
			public void reset() {
				horizontal.reset();
			}
			
			@Override
			public void render(RenderList renderList, CommandBuffer cb) {
				horizontal.render(renderList, cb);
			}
			
			@Override
			public void link() {
				horizontal.link();
			}
			
			@Override
			public Texture2D get(String name) {
				return horizontal.getOutput().get(SingleAxisBlurFilter.OUTPUT_TEXTURE_RESULT_NAME);
			}

			@Override
			public void updateDescriptors() {
				horizontal.updateDescriptors();
			}

			@Override
			public void prepareResize() {
				horizontal.prepareResize();
			}

			@Override
			public void resize(int width, int height) {
				horizontal.resize(width, height);
			}
		});
		this.input = new InputProvider() {
			
			@Override
			public void reset() {
				vertical.reset();
			}
			
			@Override
			public void render(RenderList renderList, CommandBuffer cb) {
				vertical.render(renderList, cb);
			}
			
			@Override
			public void link() {
				vertical.link();
			}
			
			@Override
			public Texture2D get(String name) {
				return null;
			}

			@Override
			public void updateDescriptors() {
				vertical.updateDescriptors();
			}

			@Override
			public void prepareResize() {
				vertical.prepareResize();;
			}

			@Override
			public void resize(int width, int height) {
				vertical.resize(width, height);
			}
		};
		
		setRadius(0.05f);
	}
	
	public void setRadius(float r) {
		radius = r;
		horizontal.setAxis(Vec2.create(r / aspect, 0f));
		vertical.setAxis(Vec2.create(0f, r));
	}

	public void setSampleCount(int count) {
		horizontal.setSampleCount(count);
		vertical.setSampleCount(count);
	}

	@Override
	public void renderImplementation(RenderList renderList, CommandBuffer cb) {
	}

	@Override
	protected void linkImplementation() {
		output.put(OUTPUT_TEXTURE_RESULT_NAME, vertical.getOutput().get(SingleAxisBlurFilter.OUTPUT_TEXTURE_RESULT_NAME));
	}

	@Override
	public void dispose() {
		horizontal.dispose();
		vertical.dispose();
	}

	@Override
	protected void updateDescriptorsImplementation() {
	}

	@Override
	public void getRequiredRenderImplementations(Set<ID> target) {
		horizontal.getRequiredRenderImplementations(target);
		vertical.getRequiredRenderImplementations(target);
	}

	@Override
	protected void resizeImplementation(int width, int height) {
		aspect = (float) width / (float) height;
		setRadius(radius);
	}
}
