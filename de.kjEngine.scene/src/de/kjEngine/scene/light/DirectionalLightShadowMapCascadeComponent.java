/**
 * 
 */
package de.kjEngine.scene.light;

import de.kjEngine.math.Mat3;
import de.kjEngine.math.Mat4;
import de.kjEngine.math.Vec3;
import de.kjEngine.math.Vec4;
import de.kjEngine.scene.camera.OrthographicFrustum;

/**
 * @author konst
 *
 */
public class DirectionalLightShadowMapCascadeComponent extends ShadowMapComponent<DirectionalLightComponent> {

	public float near, far, lengthScale;

	public DirectionalLightShadowMapCascadeComponent(int resolution, float near, float far, float lengthScale) {
		super(new OrthographicFrustum(), resolution);
		this.near = near;
		this.far = far;
		this.lengthScale = lengthScale;
	}

	@Override
	public void update(float delta) {
		Mat3 lightDirection = new Mat3();

		Vec3 dir = Vec3.create(parent.direction);
		dir.normalise();

		lightDirection.setZ(dir);

		if (dir.y > 0.99f || dir.y < -0.99f) {
			lightDirection.setX(Vec3.cross(dir, Vec3.Z, null).normalise());
		} else {
			lightDirection.setX(Vec3.cross(dir, Vec3.Y, null).normalise());
		}

		lightDirection.setY(Vec3.cross(dir, lightDirection.getX(), null).normalise());

		transform.rotation.setFromMatrix(lightDirection);
		
		Vec4 pMaxNear = calcFrustumCorner(Vec3.create(0f, 0f, 0f));
		Vec4 pMaxFar = calcFrustumCorner(Vec3.create(0f, 0f, 1f));
		
		Vec4 pNear = Vec4.interpolate(pMaxNear, pMaxFar, near);
		Vec4 pFar = Vec4.interpolate(pMaxNear, pMaxFar, far);
		
		float near = calcDepth(pNear);
		float far = calcDepth(pFar);

		Mat4 invLightView = new Mat4(lightDirection);
		Mat4 lightView = Mat4.invert(invLightView, null);

		Vec4[] frustumCorners = new Vec4[8];
		frustumCorners[0] = calcFrustumCorner(Vec3.create(-1f, 1f, near));
		frustumCorners[1] = calcFrustumCorner(Vec3.create(-1f, -1f, near));
		frustumCorners[2] = calcFrustumCorner(Vec3.create(1f, -1f, near));
		frustumCorners[3] = calcFrustumCorner(Vec3.create(1f, 1f, near));
		frustumCorners[4] = calcFrustumCorner(Vec3.create(-1f, 1f, far));
		frustumCorners[5] = calcFrustumCorner(Vec3.create(-1f, -1f, far));
		frustumCorners[6] = calcFrustumCorner(Vec3.create(1f, -1f, far));
		frustumCorners[7] = calcFrustumCorner(Vec3.create(1f, 1f, far));

		for (int i = 0; i < 8; i++) {
			Mat4.transform(lightView, frustumCorners[i], frustumCorners[i]);
		}

		Vec3 min = Vec3.scale(Float.POSITIVE_INFINITY), max = Vec3.scale(Float.NEGATIVE_INFINITY);
		for (int i = 0; i < 8; i++) {
			min.x = Math.min(min.x, frustumCorners[i].x);
			min.y = Math.min(min.y, frustumCorners[i].y);
			min.z = Math.min(min.z, frustumCorners[i].z);
			
			max.x = Math.max(max.x, frustumCorners[i].x);
			max.y = Math.max(max.y, frustumCorners[i].y);
			max.z = Math.max(max.z, frustumCorners[i].z);
		}
		
		Vec4 center = Vec4.create(Vec3.interpolate(min, max, 0.5f), 1f);
		Mat4.transform(invLightView, center, center);
		
		((OrthographicFrustum) camera.getFrustum()).setSize(max.x - min.x, max.y - min.y, (max.z - min.z) * lengthScale);
		
		transform.position.set(center);

		super.update(delta);
	}

	private float calcDepth(Vec4 pos) {
		Vec4 result = Mat4.transform(container.camera.getViewProjection(), pos, null);
		return result.z / result.w;
	}

	private Vec4 calcFrustumCorner(Vec3 pos) {
		Vec4 result = Mat4.transform(container.camera.getInvViewProjection(), Vec4.create(pos, 1f), null);
		result.mul(1f / result.w);
		return result;
	}
}
