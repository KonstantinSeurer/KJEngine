package de.kjEngine.scene.camera;

import de.kjEngine.math.Mat4;

public class PerspectiveFrustum extends Frustum {
	
	private float fov, aspect, near, far;

	public PerspectiveFrustum(float fovy, float aspect, float near, float far) {
		this.fov = fovy;
		this.aspect = aspect;
		this.near = near;
		this.far = far;
		updateProjection();
	}

	public float getFov() {
		return fov;
	}

	public PerspectiveFrustum setFov(float fovy) {
		this.fov = fovy;
		updateProjection();
		return this;
	}

	public float getAspect() {
		return aspect;
	}

	public PerspectiveFrustum setAspect(float aspect) {
		this.aspect = aspect;
		updateProjection();
		return this;
	}

	public float getNear() {
		return near;
	}

	public PerspectiveFrustum setNear(float near) {
		this.near = near;
		updateProjection();
		return this;
	}

	public float getFar() {
		return far;
	}

	public PerspectiveFrustum setFar(float far) {
		this.far = far;
		updateProjection();
		return this;
	}

	private PerspectiveFrustum updateProjection() {
		Mat4.perspective(fov, aspect, near, far, projection);
		return this;
	}
}
