package de.kjEngine.math;

import de.kjEngine.io.serilization.Serialize;

public class Mat3 extends Mat2 {

	@Serialize
	public float xz, yz, zx, zy, zz = 1f;

	public Mat3() {
	}

	public Mat3(Vec3 x, Vec3 y, Vec3 z) {
		xx = x.x;
		yx = x.y;
		zx = x.z;

		xy = y.x;
		yy = y.y;
		zy = y.z;

		xz = z.x;
		yz = z.y;
		zz = z.z;
	}

	public Mat3 set(Mat3 src) {
		super.set(src);
		xz = src.xz;
		yz = src.yz;
		zx = src.zx;
		zy = src.zy;
		zz = src.zz;
		return this;
	}

	public static Mat3 add(Mat3 left, Mat3 right, Mat3 dest) {
		if (dest == null)
			dest = new Mat3();

		dest.xx = left.xx + right.xx;
		dest.xy = left.xy + right.xy;
		dest.xz = left.xz + right.xz;
		dest.yx = left.yx + right.yx;
		dest.yy = left.yy + right.yy;
		dest.yz = left.yz + right.yz;
		dest.zx = left.zx + right.zx;
		dest.zy = left.zy + right.zy;
		dest.zz = left.zz + right.zz;

		return dest;
	}

	public static Mat3 sub(Mat3 left, Mat3 right, Mat3 dest) {
		if (dest == null)
			dest = new Mat3();

		dest.xx = left.xx - right.xx;
		dest.xy = left.xy - right.xy;
		dest.xz = left.xz - right.xz;
		dest.yx = left.yx - right.yx;
		dest.yy = left.yy - right.yy;
		dest.yz = left.yz - right.yz;
		dest.zx = left.zx - right.zx;
		dest.zy = left.zy - right.zy;
		dest.zz = left.zz - right.zz;

		return dest;
	}

	public static Mat3 mul(Mat3 left, Mat3 right, Mat3 dest) {
		if (dest == null)
			dest = new Mat3();

		float m00 = left.xx * right.xx + left.yx * right.xy + left.zx * right.xz;
		float m01 = left.xy * right.xx + left.yy * right.xy + left.zy * right.xz;
		float m02 = left.xz * right.xx + left.yz * right.xy + left.zz * right.xz;
		float m10 = left.xx * right.yx + left.yx * right.yy + left.zx * right.yz;
		float m11 = left.xy * right.yx + left.yy * right.yy + left.zy * right.yz;
		float m12 = left.xz * right.yx + left.yz * right.yy + left.zz * right.yz;
		float m20 = left.xx * right.zx + left.yx * right.zy + left.zx * right.zz;
		float m21 = left.xy * right.zx + left.yy * right.zy + left.zy * right.zz;
		float m22 = left.xz * right.zx + left.yz * right.zy + left.zz * right.zz;

		dest.xx = m00;
		dest.xy = m01;
		dest.xz = m02;
		dest.yx = m10;
		dest.yy = m11;
		dest.yz = m12;
		dest.zx = m20;
		dest.zy = m21;
		dest.zz = m22;

		return dest;
	}

	public static Vec3 transform(Mat3 left, Vec3 right, Vec3 dest) {
		if (dest == null)
			dest = Vec3.create();

		float x = left.xx * right.x + left.yx * right.y + left.zx * right.z;
		float y = left.xy * right.x + left.yy * right.y + left.zy * right.z;
		float z = left.xz * right.x + left.yz * right.y + left.zz * right.z;

		dest.x = x;
		dest.y = y;
		dest.z = z;

		return dest;
	}

	public Mat3 transpose() {
		return transpose(this, this);
	}

	public Mat3 transpose(Mat3 dest) {
		return transpose(this, dest);
	}

	public static Mat3 transpose(Mat3 src, Mat3 dest) {
		if (dest == null) {
			dest = new Mat3();
		}

		float m00 = src.xx;
		float m01 = src.yx;
		float m02 = src.zx;
		float m10 = src.xy;
		float m11 = src.yy;
		float m12 = src.zy;
		float m20 = src.xz;
		float m21 = src.yz;
		float m22 = src.zz;

		dest.xx = m00;
		dest.xy = m01;
		dest.xz = m02;
		dest.yx = m10;
		dest.yy = m11;
		dest.yz = m12;
		dest.zx = m20;
		dest.zy = m21;
		dest.zz = m22;
		return dest;
	}

	public float determinant() {
		return xx * (yy * zz - yz * zy) + xy * (yz * zx - yx * zz) + xz * (yx * zy - yy * zx);
	}

	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append(xx).append(' ').append(yx).append(' ').append(zx).append(' ').append('\n');
		buf.append(xy).append(' ').append(yy).append(' ').append(zy).append(' ').append('\n');
		buf.append(xz).append(' ').append(yz).append(' ').append(zz).append(' ').append('\n');
		return buf.toString();
	}

	public Mat3 invert() {
		return invert(this, this);
	}

	public static Mat3 invert(Mat3 src, Mat3 dest) {
		float determinant = src.determinant();

		if (determinant != 0) {
			if (dest == null) {
				dest = new Mat3();
			}

			float determinant_inv = 1f / determinant;

			float t00 = src.yy * src.zz - src.yz * src.zy;
			float t01 = -src.yx * src.zz + src.yz * src.zx;
			float t02 = src.yx * src.zy - src.yy * src.zx;
			float t10 = -src.xy * src.zz + src.xz * src.zy;
			float t11 = src.xx * src.zz - src.xz * src.zx;
			float t12 = -src.xx * src.zy + src.xy * src.zx;
			float t20 = src.xy * src.yz - src.xz * src.yy;
			float t21 = -src.xx * src.yz + src.xz * src.yx;
			float t22 = src.xx * src.yy - src.xy * src.yx;

			dest.xx = t00 * determinant_inv;
			dest.yy = t11 * determinant_inv;
			dest.zz = t22 * determinant_inv;
			dest.xy = t10 * determinant_inv;
			dest.yx = t01 * determinant_inv;
			dest.zx = t02 * determinant_inv;
			dest.xz = t20 * determinant_inv;
			dest.yz = t21 * determinant_inv;
			dest.zy = t12 * determinant_inv;

			return dest;
		}
		return null;
	}

	public Mat3 negate() {
		return negate(this);
	}

	public Mat3 negate(Mat3 dest) {
		return negate(this, dest);
	}

	public static Mat3 negate(Mat3 src, Mat3 dest) {
		if (dest == null) {
			dest = new Mat3();
		}

		dest.xx = -src.xx;
		dest.xy = -src.xz;
		dest.xz = -src.xy;
		dest.yx = -src.yx;
		dest.yy = -src.yz;
		dest.yz = -src.yy;
		dest.zx = -src.zx;
		dest.zy = -src.zz;
		dest.zz = -src.zy;
		return dest;
	}

	public Mat3 setIdentity() {
		return setIdentity(this);
	}

	public static Mat3 setIdentity(Mat3 m) {
		m.xx = 1.0f;
		m.xy = 0.0f;
		m.xz = 0.0f;
		m.yx = 0.0f;
		m.yy = 1.0f;
		m.yz = 0.0f;
		m.zx = 0.0f;
		m.zy = 0.0f;
		m.zz = 1.0f;
		return m;
	}

	public Mat3 setZero() {
		return setZero(this);
	}

	public static Mat3 setZero(Mat3 m) {
		m.xx = 0.0f;
		m.xy = 0.0f;
		m.xz = 0.0f;
		m.yx = 0.0f;
		m.yy = 0.0f;
		m.yz = 0.0f;
		m.zx = 0.0f;
		m.zy = 0.0f;
		m.zz = 0.0f;
		return m;
	}

	public Vec3 getX() {
		return Vec3.create(xx, xy, xz);
	}

	public Vec3 getY() {
		return Vec3.create(yx, yy, yz);
	}
	
	public void getY(Vec3 v) {
		v.x = yx;
		v.y = yy;
		v.z = yz;
	}

	public Vec3 getZ() {
		return Vec3.create(zx, zy, zz);
	}

	public void setX(Vec3 v) {
		xx = v.x;
		xy = v.y;
		xz = v.z;
	}

	public void setY(Vec3 v) {
		yx = v.x;
		yy = v.y;
		yz = v.z;
	}
	
	public void setZ(Vec3 v) {
		zx = v.x;
		zy = v.y;
		zz = v.z;
	}
}
