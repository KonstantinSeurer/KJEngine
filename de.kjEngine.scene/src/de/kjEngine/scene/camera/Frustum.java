/**
 * 
 */
package de.kjEngine.scene.camera;

import de.kjEngine.math.Mat4;
import de.kjEngine.math.Vec3;
import de.kjEngine.math.Vec4;

/**
 * @author konst
 *
 */
public abstract class Frustum {

	protected Mat4 projection = new Mat4();
	protected Mat4 viewProjection = new Mat4();
	private Vec4[] planes = new Vec4[4];

	public Frustum() {
		planes[0] = Vec4.create();
		planes[1] = Vec4.create();
		planes[2] = Vec4.create();
		planes[3] = Vec4.create();
	}

	public void update(Mat4 vMat) {
		Mat4.mul(projection, vMat, viewProjection);

		planes[0].x = viewProjection.xw - viewProjection.xx;
		planes[0].y = viewProjection.yw - viewProjection.yx;
		planes[0].z = viewProjection.zw - viewProjection.zx;
		planes[0].w = viewProjection.ww - viewProjection.wx;

		planes[1].x = viewProjection.xw + viewProjection.xx;
		planes[1].y = viewProjection.yw + viewProjection.yx;
		planes[1].z = viewProjection.zw + viewProjection.zx;
		planes[1].w = viewProjection.ww + viewProjection.wx;

		planes[2].x = viewProjection.xw + viewProjection.xy;
		planes[2].y = viewProjection.yw + viewProjection.yy;
		planes[2].z = viewProjection.zw + viewProjection.zy;
		planes[2].w = viewProjection.ww + viewProjection.wy;

		planes[3].x = viewProjection.xw - viewProjection.xy;
		planes[3].y = viewProjection.yw - viewProjection.yy;
		planes[3].z = viewProjection.zw - viewProjection.zy;
		planes[3].w = viewProjection.ww - viewProjection.wy;
	}

	public final boolean intersectsSphere(Vec3 pos, float r) {
		for (int i = 0; i < 4; i++) {
			Vec4 p = planes[i];
			if (Vec3.dot(pos, p) + p.w + r < 0f) {
				return false;
			}
		}
		return true;
	}

	public final boolean intersectsAABB(Vec3 min, Vec3 max) {
		for (int i = 0; i < 4; i++) {
			if (!intersectsPlane(planes[i], min, max)) {
				return false;
			}
		}
		return true;
	}

	private static boolean intersectsPlane(Vec4 plane, Vec3 min, Vec3 max) {
		return insidePlane(plane, min.x, min.y, min.z) || insidePlane(plane, max.x, min.y, min.z) || insidePlane(plane, min.x, min.y, max.z) || insidePlane(plane, max.x, min.y, max.z)
				|| insidePlane(plane, min.x, max.y, min.z) || insidePlane(plane, max.x, max.y, min.z) || insidePlane(plane, min.x, max.y, max.z) || insidePlane(plane, max.x, max.y, max.z);
	}

	private static boolean insidePlane(Vec4 plane, float x, float y, float z) {
		return !(plane.x * x + plane.y * y + plane.z * z + plane.w < 0f);
	}

	/**
	 * @return the projection
	 */
	public Mat4 getProjection() {
		return projection;
	}

	/**
	 * @return the viewProjection
	 */
	public Mat4 getViewProjection() {
		return viewProjection;
	}
}
