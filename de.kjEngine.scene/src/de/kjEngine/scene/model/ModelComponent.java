/**
 * 
 */
package de.kjEngine.scene.model;

import de.kjEngine.math.Mat4;
import de.kjEngine.renderer.Renderable;
import de.kjEngine.renderer.Renderable.RenderImplementation.ID;
import de.kjEngine.scene.Entity;
import de.kjEngine.scene.SceneComponent;

/**
 * @author konst
 *
 */
public class ModelComponent extends SceneComponent<Entity, ModelComponent> {

	public static final ID TRANSFORM_BUFFER = new ID(ModelComponent.class, "transform_buffer");

	static {
		Renderable.RenderImplementation.Registry.registerProvider(TRANSFORM_BUFFER, new Renderable.RenderImplementation.Provider() {

			@Override
			public RenderImplementation<ModelComponent> create() {
				return new ModelComponentTransformBuffer();
			}
		});
	}

	private Model model;
	private Mat4[] transforms;

	// for faster access
	public final ModelComponentTransformBuffer transformBufferGPUImplementation;

	public ModelComponent(Model model) {
		super(LATE);

		setModel(model);

		transformBufferGPUImplementation = getRenderImplementation(ModelComponentTransformBuffer.class);
	}

	public void setModel(Model model) {
		if (this.model == null || model.getTransformCount() != this.model.getTransformCount()) {
			transforms = new Mat4[model.getTransformCount()];
			for (int i = 0; i < transforms.length; i++) {
				transforms[i] = new Mat4();
			}
		}
		
		this.model = model;
	}

	public final Model getModel() {
		return model;
	}

	public final Mat4[] getTransforms() {
		return transforms;
	}

	@Override
	protected void update(float delta) {
	}
}
