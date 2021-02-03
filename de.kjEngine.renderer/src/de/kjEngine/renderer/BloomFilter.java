/**
 * 
 */
package de.kjEngine.renderer;

import java.util.Set;

import org.json.JSONObject;

import de.kjEngine.graphics.CommandBuffer;
import de.kjEngine.graphics.Graphics;
import de.kjEngine.graphics.Texture2D;
import de.kjEngine.io.serilization.Serializable;
import de.kjEngine.math.Vec3;
import de.kjEngine.math.Vec4;
import de.kjEngine.renderer.Renderable.RenderImplementation.ID;

/**
 * @author konst
 *
 */
public class BloomFilter extends Stage implements Serializable {

	public static final String INPUT_TEXTURE_TEXTURE_NAME = ExposureFilter.INPUT_TEXTURE_TEXTURE_NAME;
	public static final String OUTPUT_TEXTURE_RESULT_NAME = "result";

	private ExposureFilter exposureFilter;
	private BlurFilter blurFilter;
	private MulFilter mulFilter;
	private AddFilter addFilter;

	public BloomFilter(InputProvider input) {
		super(null);

		exposureFilter = new ExposureFilter(input);
		blurFilter = new BlurFilter(new InputProvider() {

			@Override
			public void reset() {
				exposureFilter.reset();
			}

			@Override
			public void render(RenderList renderList, CommandBuffer cb) {
				exposureFilter.render(renderList, cb);
			}

			@Override
			public void link() {
				exposureFilter.link();
			}

			@Override
			public Texture2D get(String name) {
				return exposureFilter.getOutput().get(ExposureFilter.OUTPUT_TEXTURE_RESULT_NAME);
			}

			@Override
			public void updateDescriptors() {
				exposureFilter.updateDescriptors();
			}

			@Override
			public void prepareResize() {
				exposureFilter.prepareResize();
			}

			@Override
			public void resize(int width, int height) {
				exposureFilter.resize(width, height);
			}
		});
		mulFilter = new MulFilter(new InputProvider() {

			@Override
			public void reset() {
				blurFilter.reset();
			}

			@Override
			public void render(RenderList renderList, CommandBuffer cb) {
				blurFilter.render(renderList, cb);
			}

			@Override
			public void link() {
				blurFilter.link();
			}

			@Override
			public Texture2D get(String name) {
				switch (name) {
				case MulFilter.INPUT_TEXTURE_A_NAME:
					return blurFilter.getOutput().get(BlurFilter.OUTPUT_TEXTURE_RESULT_NAME);
				case MulFilter.INPUT_TEXTURE_B_NAME:
					return Graphics.createTexture2D(Vec4.create(1f, 1f, 1f, 1f));
				}
				return null;
			}

			@Override
			public void updateDescriptors() {
				blurFilter.updateDescriptors();
			}

			@Override
			public void prepareResize() {
				blurFilter.prepareResize();
			}

			@Override
			public void resize(int width, int height) {
				blurFilter.resize(width, height);
			}
		});
		addFilter = new AddFilter(new InputProvider() {

			@Override
			public void reset() {
				mulFilter.reset();
			}

			@Override
			public void render(RenderList renderList, CommandBuffer cb) {
				mulFilter.render(renderList, cb);
			}

			@Override
			public void link() {
				mulFilter.link();
			}

			@Override
			public Texture2D get(String name) {
				switch (name) {
				case AddFilter.INPUT_TEXTURE_A_NAME:
					return input.get(INPUT_TEXTURE_TEXTURE_NAME);
				case AddFilter.INPUT_TEXTURE_B_NAME:
					return mulFilter.getOutput().get(MulFilter.OUTPUT_TEXTURE_RESULT_NAME);
				}
				return null;
			}

			@Override
			public void updateDescriptors() {
				mulFilter.updateDescriptors();
			}

			@Override
			public void prepareResize() {
				mulFilter.prepareResize();
			}

			@Override
			public void resize(int width, int height) {
				mulFilter.resize(width, height);
			}
		});

		this.input = new InputProvider() {

			@Override
			public void reset() {
				addFilter.reset();
			}

			@Override
			public void render(RenderList renderList, CommandBuffer cb) {
				addFilter.render(renderList, cb);
			}

			@Override
			public void link() {
				addFilter.link();
			}

			@Override
			public Texture2D get(String name) {
				return null;
			}

			@Override
			public void updateDescriptors() {
				addFilter.updateDescriptors();
			}

			@Override
			public void prepareResize() {
				addFilter.prepareResize();
			}

			@Override
			public void resize(int width, int height) {
				addFilter.resize(width, height);
			}
		};

		setExposure(0.5f);
		setAmount(1f);
		setBlurRadius(0.05f);
		setSampleCount(32);
	}

	@Override
	public void dispose() {
		exposureFilter.dispose();
		blurFilter.dispose();
		mulFilter.dispose();
		addFilter.dispose();
	}

	public BloomFilter setExposure(float exposure) {
		exposureFilter.setExposure(exposure);
		return this;
	}

	public BloomFilter setAmount(float amount) {
		mulFilter.setColor(Vec3.scale(amount));
		return this;
	}

	public BloomFilter setBlurRadius(float radius) {
		blurFilter.setRadius(radius);
		return this;
	}

	public BloomFilter setSampleCount(int sampleCount) {
		blurFilter.setSampleCount(sampleCount);
		return this;
	}

	@Override
	protected void renderImplementation(RenderList renderList, CommandBuffer cb) {
	}

	@Override
	protected void linkImplementation() {
		getOutput().put(OUTPUT_TEXTURE_RESULT_NAME, addFilter.getOutput().get(AddFilter.OUTPUT_TEXTURE_RESULT_NAME));
	}

	@Override
	protected void updateDescriptorsImplementation() {
	}

	@Override
	public void getRequiredRenderImplementations(Set<ID> target) {
		exposureFilter.getRequiredRenderImplementations(target);
		blurFilter.getRequiredRenderImplementations(target);
		mulFilter.getRequiredRenderImplementations(target);
		addFilter.getRequiredRenderImplementations(target);
	}

	@Override
	public void deserialize(JSONObject obj) {
		if (obj.has("sampleCount")) {
			setSampleCount(obj.getInt("sampleCount"));
		}
		if (obj.has("blurRadius")) {
			setBlurRadius(obj.getFloat("blurRadius"));
		}
		if (obj.has("exposure")) {
			setExposure(obj.getFloat("exposure"));
		}
		if (obj.has("amount")) {
			setAmount(obj.getFloat("amount"));
		}
	}

	@Override
	public JSONObject serialize() {
		return null;
	}

	@Override
	protected void resizeImplementation(int width, int height) {
	}
}
