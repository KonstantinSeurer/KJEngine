package de.kjEngine.math;

import de.kjEngine.io.serilization.Serialize;

public class Mat4 extends Mat3 {

	public static final Mat4 IDENTITY = Mat4.setIdentity(new Mat4());

	@Serialize
	public float xw, yw, zw, wx, wy, wz, ww = 1f;

	public static Mat4 identity() {
		return setIdentity(new Mat4());
	}

	public static Mat4 perspective(float fov, float aspect, float n, float f, Mat4 target) {
		if (target == null) {
			target = new Mat4();
		}
		target.setIdentity();

		float y_scale = (float) ((1f / Math.tan(fov * 0.5f)));
		float x_scale = y_scale / aspect;
		float frustum_length = f - n;
		target.xx = x_scale;
		target.yy = -y_scale;
		target.zz = ((f + n) / frustum_length);
		target.zw = 1f;
		target.wz = -((2f * n * f) / frustum_length);
		target.ww = 0f;

//		float scale = 1f / Real.tan(fov * 0.5f);
//		target.m00 = scale / aspect; // scale the x coordinates of the projected point
//		target.m11 = scale; // scale the y coordinates of the projected point
//		target.m22 = -f / (f - n); // used to remap z to [0,1]
//		target.m32 = -f * n / (f - n); // used to remap z [0,1]
//		target.m23 = 1f; // set w = -z
//		target.m33 = 0f;

//		float tanHalfFov = Real.tan(fov * 0.5f);
//		target.setZero();
//		target.m00 = 1f / (tanHalfFov * aspect);
//		target.m12 = f / (f - n);
//		target.m13 = 1f;
//		target.m21 = -1f / tanHalfFov;
//		target.m32 = (n * f) / (n - f);

		return target;
	}

	public static Mat4 orthographic(float width, float height, float length, Mat4 target) {
		if (target == null) {
			target = new Mat4();
		}
		target.setIdentity();

		target.xx = 2f / width;
		target.yy = 2f / height;
		target.zz = 1f / length;
		target.wz = 0.5f;

		return target;
	}

	public static Mat4 orthographic(float width, float height, float length) {
		return orthographic(width, height, length, null);
	}

	public Mat4() {
	}

	public Mat4(Mat4 src) {
		set(src);
	}

	public Mat4(Mat3 src) {
		set(src);
	}

	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append(xx).append(' ').append(yx).append(' ').append(zx).append(' ').append(wx).append('\n');
		buf.append(xy).append(' ').append(yy).append(' ').append(zy).append(' ').append(wy).append('\n');
		buf.append(xz).append(' ').append(yz).append(' ').append(zz).append(' ').append(wz).append('\n');
		buf.append(xw).append(' ').append(yw).append(' ').append(zw).append(' ').append(ww).append('\n');
		return buf.toString();
	}

	public Mat4 setIdentity() {
		return setIdentity(this);
	}

	public static Mat4 setIdentity(Mat4 m) {
		m.xx = 1.0f;
		m.xy = 0.0f;
		m.xz = 0.0f;
		m.xw = 0.0f;
		m.yx = 0.0f;
		m.yy = 1.0f;
		m.yz = 0.0f;
		m.yw = 0.0f;
		m.zx = 0.0f;
		m.zy = 0.0f;
		m.zz = 1.0f;
		m.zw = 0.0f;
		m.wx = 0.0f;
		m.wy = 0.0f;
		m.wz = 0.0f;
		m.ww = 1.0f;

		return m;
	}

	public Mat4 setZero() {
		return setZero(this);
	}

	public static Mat4 setZero(Mat4 m) {
		m.xx = 0.0f;
		m.xy = 0.0f;
		m.xz = 0.0f;
		m.xw = 0.0f;
		m.yx = 0.0f;
		m.yy = 0.0f;
		m.yz = 0.0f;
		m.yw = 0.0f;
		m.zx = 0.0f;
		m.zy = 0.0f;
		m.zz = 0.0f;
		m.zw = 0.0f;
		m.wx = 0.0f;
		m.wy = 0.0f;
		m.wz = 0.0f;
		m.ww = 0.0f;

		return m;
	}

	public Mat4 set(Mat4 src) {
		super.set(src);
		xw = src.xw;
		yw = src.yw;
		zw = src.zw;
		wx = src.wx;
		wy = src.wy;
		wz = src.wz;
		ww = src.ww;
		return this;
	}

	public static Mat4 add(Mat4 left, Mat4 right, Mat4 dest) {
		if (dest == null) {
			dest = new Mat4();
		}

		dest.xx = left.xx + right.xx;
		dest.xy = left.xy + right.xy;
		dest.xz = left.xz + right.xz;
		dest.xw = left.xw + right.xw;
		dest.yx = left.yx + right.yx;
		dest.yy = left.yy + right.yy;
		dest.yz = left.yz + right.yz;
		dest.yw = left.yw + right.yw;
		dest.zx = left.zx + right.zx;
		dest.zy = left.zy + right.zy;
		dest.zz = left.zz + right.zz;
		dest.zw = left.zw + right.zw;
		dest.wx = left.wx + right.wx;
		dest.wy = left.wy + right.wy;
		dest.wz = left.wz + right.wz;
		dest.ww = left.ww + right.ww;

		return dest;
	}

	public static Mat4 sub(Mat4 left, Mat4 right, Mat4 dest) {
		if (dest == null) {
			dest = new Mat4();
		}

		dest.xx = left.xx - right.xx;
		dest.xy = left.xy - right.xy;
		dest.xz = left.xz - right.xz;
		dest.xw = left.xw - right.xw;
		dest.yx = left.yx - right.yx;
		dest.yy = left.yy - right.yy;
		dest.yz = left.yz - right.yz;
		dest.yw = left.yw - right.yw;
		dest.zx = left.zx - right.zx;
		dest.zy = left.zy - right.zy;
		dest.zz = left.zz - right.zz;
		dest.zw = left.zw - right.zw;
		dest.wx = left.wx - right.wx;
		dest.wy = left.wy - right.wy;
		dest.wz = left.wz - right.wz;
		dest.ww = left.ww - right.ww;

		return dest;
	}

	public static Mat4 mul(Mat4 left, Mat4 right, Mat4 dest) {
		if (dest == null) {
			dest = new Mat4();
		}

		float m00 = left.xx * right.xx + left.yx * right.xy + left.zx * right.xz + left.wx * right.xw;
		float m01 = left.xy * right.xx + left.yy * right.xy + left.zy * right.xz + left.wy * right.xw;
		float m02 = left.xz * right.xx + left.yz * right.xy + left.zz * right.xz + left.wz * right.xw;
		float m03 = left.xw * right.xx + left.yw * right.xy + left.zw * right.xz + left.ww * right.xw;
		float m10 = left.xx * right.yx + left.yx * right.yy + left.zx * right.yz + left.wx * right.yw;
		float m11 = left.xy * right.yx + left.yy * right.yy + left.zy * right.yz + left.wy * right.yw;
		float m12 = left.xz * right.yx + left.yz * right.yy + left.zz * right.yz + left.wz * right.yw;
		float m13 = left.xw * right.yx + left.yw * right.yy + left.zw * right.yz + left.ww * right.yw;
		float m20 = left.xx * right.zx + left.yx * right.zy + left.zx * right.zz + left.wx * right.zw;
		float m21 = left.xy * right.zx + left.yy * right.zy + left.zy * right.zz + left.wy * right.zw;
		float m22 = left.xz * right.zx + left.yz * right.zy + left.zz * right.zz + left.wz * right.zw;
		float m23 = left.xw * right.zx + left.yw * right.zy + left.zw * right.zz + left.ww * right.zw;
		float m30 = left.xx * right.wx + left.yx * right.wy + left.zx * right.wz + left.wx * right.ww;
		float m31 = left.xy * right.wx + left.yy * right.wy + left.zy * right.wz + left.wy * right.ww;
		float m32 = left.xz * right.wx + left.yz * right.wy + left.zz * right.wz + left.wz * right.ww;
		float m33 = left.xw * right.wx + left.yw * right.wy + left.zw * right.wz + left.ww * right.ww;

		dest.xx = m00;
		dest.xy = m01;
		dest.xz = m02;
		dest.xw = m03;
		dest.yx = m10;
		dest.yy = m11;
		dest.yz = m12;
		dest.yw = m13;
		dest.zx = m20;
		dest.zy = m21;
		dest.zz = m22;
		dest.zw = m23;
		dest.wx = m30;
		dest.wy = m31;
		dest.wz = m32;
		dest.ww = m33;

		return dest;
	}

	public static Vec4 transform(Mat4 left, Vec4 right, Vec4 dest) {
		if (dest == null) {
			dest = new Vec4();
		}

		float x = left.xx * right.x + left.yx * right.y + left.zx * right.z + left.wx * right.w;
		float y = left.xy * right.x + left.yy * right.y + left.zy * right.z + left.wy * right.w;
		float z = left.xz * right.x + left.yz * right.y + left.zz * right.z + left.wz * right.w;
		float w = left.xw * right.x + left.yw * right.y + left.zw * right.z + left.ww * right.w;

		dest.x = x;
		dest.y = y;
		dest.z = z;
		dest.w = w;

		return dest;
	}

	public Mat4 transpose() {
		return transpose(this);
	}

	public Mat4 translate(Vec2 vec) {
		return translate(vec, this);
	}

	public Mat4 translate(Vec3 vec) {
		return translate(vec, this);
	}

	public Mat4 translateX(float x) {
		wx += xx * x;
		wy += xy * x;
		wz += xz * x;
		ww += xw * x;
		return this;
	}

	public Mat4 translateY(float y) {
		wx += yx * y;
		wy += yy * y;
		wz += yz * y;
		ww += yw * y;
		return this;
	}

	public Mat4 translateZ(float z) {
		wx += zx * z;
		wy += zy * z;
		wz += zz * z;
		ww += zw * z;
		return this;
	}

	public Mat4 translate(float x, float y, float z) {
		return translate(x, y, z, this, this);
	}

	public Mat4 scale(Vec3 vec) {
		return scale(vec, this, this);
	}

	public Mat4 scale(float x, float y, float z) {
		xx *= x;
		xy *= x;
		xz *= x;
		xw *= x;
		yx *= y;
		yy *= y;
		yz *= y;
		yw *= y;
		zx *= z;
		zy *= z;
		zz *= z;
		zw *= z;
		return this;
	}

	public static Mat4 scale(Vec3 vec, Mat4 src, Mat4 dest) {
		if (dest == null) {
			dest = new Mat4();
		}

		dest.xx = src.xx * vec.x;
		dest.xy = src.xy * vec.x;
		dest.xz = src.xz * vec.x;
		dest.xw = src.xw * vec.x;
		dest.yx = src.yx * vec.y;
		dest.yy = src.yy * vec.y;
		dest.yz = src.yz * vec.y;
		dest.yw = src.yw * vec.y;
		dest.zx = src.zx * vec.z;
		dest.zy = src.zy * vec.z;
		dest.zz = src.zz * vec.z;
		dest.zw = src.zw * vec.z;

		return dest;
	}

	public Mat4 rotate(float angle, Vec3 axis) {
		return rotate(angle, axis, this);
	}

	public Mat4 rotate(float angle, Vec3 axis, Mat4 dest) {
		return rotate(angle, axis, this, dest);
	}

	public static Mat4 rotate(float angle, Vec3 axis, Mat4 src, Mat4 dest) {
		if (dest == null) {
			dest = new Mat4();
		}

		float c = (float) Math.cos(angle);
		float s = (float) Math.sin(angle);
		float oneminusc = 1.0f - c;
		float xy = axis.x * axis.y;
		float yz = axis.y * axis.z;
		float xz = axis.x * axis.z;
		float xs = axis.x * s;
		float ys = axis.y * s;
		float zs = axis.z * s;

		float f00 = axis.x * axis.x * oneminusc + c;
		float f01 = xy * oneminusc + zs;
		float f02 = xz * oneminusc - ys;

		float f10 = xy * oneminusc - zs;
		float f11 = axis.y * axis.y * oneminusc + c;
		float f12 = yz * oneminusc + xs;

		float f20 = xz * oneminusc + ys;
		float f21 = yz * oneminusc - xs;
		float f22 = axis.z * axis.z * oneminusc + c;

		float t00 = src.xx * f00 + src.yx * f01 + src.zx * f02;
		float t01 = src.xy * f00 + src.yy * f01 + src.zy * f02;
		float t02 = src.xz * f00 + src.yz * f01 + src.zz * f02;
		float t03 = src.xw * f00 + src.yw * f01 + src.zw * f02;
		float t10 = src.xx * f10 + src.yx * f11 + src.zx * f12;
		float t11 = src.xy * f10 + src.yy * f11 + src.zy * f12;
		float t12 = src.xz * f10 + src.yz * f11 + src.zz * f12;
		float t13 = src.xw * f10 + src.yw * f11 + src.zw * f12;

		dest.zx = src.xx * f20 + src.yx * f21 + src.zx * f22;
		dest.zy = src.xy * f20 + src.yy * f21 + src.zy * f22;
		dest.zz = src.xz * f20 + src.yz * f21 + src.zz * f22;
		dest.zw = src.xw * f20 + src.yw * f21 + src.zw * f22;
		dest.xx = t00;
		dest.xy = t01;
		dest.xz = t02;
		dest.xw = t03;
		dest.yx = t10;
		dest.yy = t11;
		dest.yz = t12;
		dest.yw = t13;

		return dest;
	}

	public Mat4 translate(Vec3 vec, Mat4 dest) {
		return translate(vec, this, dest);
	}

	public static Mat4 translate(Vec3 vec, Mat4 src, Mat4 dest) {
		return translate(vec.x, vec.y, vec.z, src, dest);
	}

	public static Mat4 translate(float x, float y, float z, Mat4 src, Mat4 dest) {
		if (dest == null) {
			dest = new Mat4();
		}

		dest.wx += src.xx * x + src.yx * y + src.zx * z;
		dest.wy += src.xy * x + src.yy * y + src.zy * z;
		dest.wz += src.xz * x + src.yz * y + src.zz * z;
		dest.ww += src.xw * x + src.yw * y + src.zw * z;

		return dest;
	}

	public Mat4 translate(Vec2 vec, Mat4 dest) {
		return translate(vec, this, dest);
	}

	public static Mat4 translate(Vec2 vec, Mat4 src, Mat4 dest) {
		if (dest == null) {
			dest = new Mat4();
		}

		dest.wx += src.xx * vec.x + src.yx * vec.y;
		dest.wy += src.xy * vec.x + src.yy * vec.y;
		dest.wz += src.xz * vec.x + src.yz * vec.y;
		dest.ww += src.xw * vec.x + src.yw * vec.y;

		return dest;
	}

	public Mat4 transpose(Mat4 dest) {
		return transpose(this, dest);
	}

	public static Mat4 transpose(Mat4 src, Mat4 dest) {
		if (dest == null) {
			dest = new Mat4();
		}

		float m00 = src.xx;
		float m01 = src.yx;
		float m02 = src.zx;
		float m03 = src.wx;
		float m10 = src.xy;
		float m11 = src.yy;
		float m12 = src.zy;
		float m13 = src.wy;
		float m20 = src.xz;
		float m21 = src.yz;
		float m22 = src.zz;
		float m23 = src.wz;
		float m30 = src.xw;
		float m31 = src.yw;
		float m32 = src.zw;
		float m33 = src.ww;

		dest.xx = m00;
		dest.xy = m01;
		dest.xz = m02;
		dest.xw = m03;
		dest.yx = m10;
		dest.yy = m11;
		dest.yz = m12;
		dest.yw = m13;
		dest.zx = m20;
		dest.zy = m21;
		dest.zz = m22;
		dest.zw = m23;
		dest.wx = m30;
		dest.wy = m31;
		dest.wz = m32;
		dest.ww = m33;

		return dest;
	}

	public float determinant() {
		return xx * ((yy * zz * ww + yz * zw * wy + yw * zy * wz) - yw * zz * wy - yy * zw * wz - yz * zy * ww)
				- xy * ((yx * zz * ww + yz * zw * wx + yw * zx * wz) - yw * zz * wx - yx * zw * wz - yz * zx * ww)
				+ xz * ((yx * zy * ww + yy * zw * wx + yw * zx * wy) - yw * zy * wx - yx * zw * wy - yy * zx * ww)
				- xw * ((yx * zy * wz + yy * zz * wx + yz * zx * wy) - yz * zy * wx - yx * zz * wy - yy * zx * wz);
	}

	private static float determinant3x3(float t00, float t01, float t02, float t10, float t11, float t12, float t20, float t21, float t22) {
		return t00 * (t11 * t22 - t12 * t21) + t01 * (t12 * t20 - t10 * t22) + t02 * (t10 * t21 - t11 * t20);
	}

	public Mat4 invert() {
		return invert(this, this);
	}

	public static Mat4 invert(Mat4 src, Mat4 dest) {
		float determinant = src.determinant();

		if (determinant != 0) {
			if (dest == null) {
				dest = new Mat4();
			}

			float determinant_inv = 1f / determinant;

			float t00 = determinant3x3(src.yy, src.yz, src.yw, src.zy, src.zz, src.zw, src.wy, src.wz, src.ww);
			float t01 = -determinant3x3(src.yx, src.yz, src.yw, src.zx, src.zz, src.zw, src.wx, src.wz, src.ww);
			float t02 = determinant3x3(src.yx, src.yy, src.yw, src.zx, src.zy, src.zw, src.wx, src.wy, src.ww);
			float t03 = -determinant3x3(src.yx, src.yy, src.yz, src.zx, src.zy, src.zz, src.wx, src.wy, src.wz);

			float t10 = -determinant3x3(src.xy, src.xz, src.xw, src.zy, src.zz, src.zw, src.wy, src.wz, src.ww);
			float t11 = determinant3x3(src.xx, src.xz, src.xw, src.zx, src.zz, src.zw, src.wx, src.wz, src.ww);
			float t12 = -determinant3x3(src.xx, src.xy, src.xw, src.zx, src.zy, src.zw, src.wx, src.wy, src.ww);
			float t13 = determinant3x3(src.xx, src.xy, src.xz, src.zx, src.zy, src.zz, src.wx, src.wy, src.wz);

			float t20 = determinant3x3(src.xy, src.xz, src.xw, src.yy, src.yz, src.yw, src.wy, src.wz, src.ww);
			float t21 = -determinant3x3(src.xx, src.xz, src.xw, src.yx, src.yz, src.yw, src.wx, src.wz, src.ww);
			float t22 = determinant3x3(src.xx, src.xy, src.xw, src.yx, src.yy, src.yw, src.wx, src.wy, src.ww);
			float t23 = -determinant3x3(src.xx, src.xy, src.xz, src.yx, src.yy, src.yz, src.wx, src.wy, src.wz);

			float t30 = -determinant3x3(src.xy, src.xz, src.xw, src.yy, src.yz, src.yw, src.zy, src.zz, src.zw);
			float t31 = determinant3x3(src.xx, src.xz, src.xw, src.yx, src.yz, src.yw, src.zx, src.zz, src.zw);
			float t32 = -determinant3x3(src.xx, src.xy, src.xw, src.yx, src.yy, src.yw, src.zx, src.zy, src.zw);
			float t33 = determinant3x3(src.xx, src.xy, src.xz, src.yx, src.yy, src.yz, src.zx, src.zy, src.zz);

			dest.xx = t00 * determinant_inv;
			dest.yy = t11 * determinant_inv;
			dest.zz = t22 * determinant_inv;
			dest.ww = t33 * determinant_inv;
			dest.xy = t10 * determinant_inv;
			dest.yx = t01 * determinant_inv;
			dest.zx = t02 * determinant_inv;
			dest.xz = t20 * determinant_inv;
			dest.yz = t21 * determinant_inv;
			dest.zy = t12 * determinant_inv;
			dest.xw = t30 * determinant_inv;
			dest.wx = t03 * determinant_inv;
			dest.yw = t31 * determinant_inv;
			dest.wy = t13 * determinant_inv;
			dest.wz = t23 * determinant_inv;
			dest.zw = t32 * determinant_inv;

			return dest;
		}
		return null;
	}

	public Mat4 negate() {
		return negate(this);
	}

	public Mat4 negate(Mat4 dest) {
		return negate(this, dest);
	}

	public static Mat4 negate(Mat4 src, Mat4 dest) {
		if (dest == null) {
			dest = new Mat4();
		}

		dest.xx = -src.xx;
		dest.xy = -src.xy;
		dest.xz = -src.xz;
		dest.xw = -src.xw;
		dest.yx = -src.yx;
		dest.yy = -src.yy;
		dest.yz = -src.yz;
		dest.yw = -src.yw;
		dest.zx = -src.zx;
		dest.zy = -src.zy;
		dest.zz = -src.zz;
		dest.zw = -src.zw;
		dest.wx = -src.wx;
		dest.wy = -src.wy;
		dest.wz = -src.wz;
		dest.ww = -src.ww;

		return dest;
	}

	public Mat4 normalise() {
		float ilx = 1f / Real.sqrt(xx * xx + xy * xy + xz * xz + xw * xw);
		xx *= ilx;
		xy *= ilx;
		xz *= ilx;
		xw *= ilx;

		float ily = 1f / Real.sqrt(yx * yx + yy * yy + yz * yz + yw * yw);
		yx *= ily;
		yy *= ily;
		yz *= ily;
		yw *= ily;

		float ilz = 1f / Real.sqrt(zx * zx + zy * zy + zz * zz + zw * zw);
		zx *= ilz;
		zy *= ilz;
		zz *= ilz;
		zw *= ilz;

		float ilw = 1f / Real.sqrt(wx * wx + wy * wy + wz * wz + ww * ww);
		wx *= ilw;
		wy *= ilw;
		wz *= ilw;
		ww *= ilw;

		return this;
	}

	public static void interpolate(Mat4 a, Mat4 b, float f, Mat4 target) {
		target.xx = a.xx + f * (b.xx - a.xx);
		target.xy = a.xy + f * (b.xy - a.xy);
		target.xz = a.xz + f * (b.xz - a.xz);
		target.xw = a.xw + f * (b.xw - a.xw);

		target.yx = a.yx + f * (b.yx - a.yx);
		target.yy = a.yy + f * (b.yy - a.yy);
		target.yz = a.yz + f * (b.yz - a.yz);
		target.yw = a.yw + f * (b.yw - a.yw);

		target.zx = a.zx + f * (b.zx - a.zx);
		target.zy = a.zy + f * (b.zy - a.zy);
		target.zz = a.zz + f * (b.zz - a.zz);
		target.zw = a.zw + f * (b.zw - a.zw);

		target.wx = a.wx + f * (b.wx - a.wx);
		target.wy = a.wy + f * (b.wy - a.wy);
		target.wz = a.wz + f * (b.wz - a.wz);
		target.ww = a.ww + f * (b.ww - a.ww);
	}

	public void setX(Vec4 v) {
		xx = v.x;
		xy = v.y;
		xz = v.z;
		xw = v.w;
	}

	public void setY(Vec4 v) {
		yx = v.x;
		yy = v.y;
		yz = v.z;
		yw = v.w;
	}

	public void setZ(Vec4 v) {
		zx = v.x;
		zy = v.y;
		zz = v.z;
		zw = v.w;
	}

	public void setW(Vec4 v) {
		wx = v.x;
		wy = v.y;
		wz = v.z;
		ww = v.w;
	}
	
	@Override
	public Vec4 getX() {
		return Vec4.create(xx, xy, xz, xw);
	}
	
	@Override
	public Vec4 getY() {
		return Vec4.create(yx, yy, yz, yw);
	}
	
	@Override
	public Vec4 getZ() {
		return Vec4.create(zx, zy, zz, zw);
	}
	
	public Vec4 getW() {
		return Vec4.create(wx, wy, wz, ww);
	}
}
