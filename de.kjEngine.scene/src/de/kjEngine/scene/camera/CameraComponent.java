package de.kjEngine.scene.camera;

import de.kjEngine.math.Mat4;
import de.kjEngine.renderer.Renderable;
import de.kjEngine.renderer.Renderable.RenderImplementation.ID;
import de.kjEngine.scene.SceneComponent;
import de.kjEngine.scene.TransformComponent;

public class CameraComponent extends SceneComponent<TransformComponent<?, ?>, CameraComponent> {

	public static final ID UBO = new ID(CameraComponent.class, "ubo");

	static {
		Renderable.RenderImplementation.Registry.registerProvider(UBO, new Renderable.RenderImplementation.Provider() {

			@Override
			public Renderable.RenderImplementation create() {
				return new CameraComponentUBO();
			}
		});
	}
	
	private Mat4 view;
	private Mat4 invProjection, invView, invViewProjection;
	private Frustum frustum;

	public CameraComponent(Frustum frustum) {
		super(LATE);
		this.frustum = frustum;
		view = new Mat4();
		invProjection = Mat4.identity();
		invView = Mat4.identity();
		invViewProjection = Mat4.identity();
	}

	@Override
	public void update(float delta) {
		Mat4.invert(parent.transform.globalTransform, view);

		frustum.update(view);

		invView.set(parent.transform.globalTransform);

		Mat4.invert(frustum.getProjection(), invProjection);

		Mat4.invert(frustum.getViewProjection(), invViewProjection);
	}

	public Mat4 getProjection() {
		return frustum.getProjection();
	}

	public Mat4 getView() {
		return view;
	}

	public Mat4 getViewProjection() {
		return frustum.getViewProjection();
	}

	public Mat4 getInvProjection() {
		return invProjection;
	}

	public Mat4 getInvView() {
		return invView;
	}

	public Mat4 getInvViewProjection() {
		return invViewProjection;
	}

	/**
	 * @return the frustum
	 */
	public Frustum getFrustum() {
		return frustum;
	}
}
