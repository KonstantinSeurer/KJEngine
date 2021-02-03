package de.kjEngine.math;

import de.kjEngine.io.serilization.Serialize;

public class Mat2 {

	@Serialize
	public float xx = 1f, xy, yx, yy = 1f;

	public Mat2() {
	}

	public Mat2(Mat2 src) {
		set(src);
	}

	public Mat2 set(Mat2 src) {
		xx = src.xx;
		xy = src.xy;
		yx = src.yx;
		yy = src.yy;
		return this;
	}

	public static Mat2 add(Mat2 left, Mat2 right, Mat2 dest) {
		if (dest == null) {
			dest = new Mat2();
		}

		dest.xx = left.xx + right.xx;
		dest.xy = left.xy + right.xy;
		dest.yx = left.yx + right.yx;
		dest.yy = left.yy + right.yy;

		return dest;
	}

	public static Mat2 sub(Mat2 left, Mat2 right, Mat2 dest) {
		if (dest == null) {
			dest = new Mat2();
		}

		dest.xx = left.xx - right.xx;
		dest.xy = left.xy - right.xy;
		dest.yx = left.yx - right.yx;
		dest.yy = left.yy - right.yy;

		return dest;
	}

	public static Mat2 mul(Mat2 left, Mat2 right, Mat2 dest) {
		if (dest == null) {
			dest = new Mat2();
		}

		float m00 = left.xx * right.xx + left.yx * right.xy;
		float m01 = left.xy * right.xx + left.yy * right.xy;
		float m10 = left.xx * right.yx + left.yx * right.yy;
		float m11 = left.xy * right.yx + left.yy * right.yy;

		dest.xx = m00;
		dest.xy = m01;
		dest.yx = m10;
		dest.yy = m11;

		return dest;
	}

	public static Vec2 transform(Mat2 left, Vec2 right, Vec2 dest) {
		if (dest == null) {
			dest = new Vec2();
		}

		float x = left.xx * right.x + left.yx * right.y;
		float y = left.xy * right.x + left.yy * right.y;

		dest.x = x;
		dest.y = y;

		return dest;
	}

	public Mat2 transpose() {
		return transpose(this);
	}

	public Mat2 transpose(Mat2 dest) {
		return transpose(this, dest);
	}

	public static Mat2 transpose(Mat2 src, Mat2 dest) {
		if (dest == null) {
			dest = new Mat2();
		}

		float m01 = src.yx;
		float m10 = src.xy;

		dest.xy = m01;
		dest.yx = m10;

		return dest;
	}

	public Mat2 invert() {
		return invert(this, this);
	}

	public static Mat2 invert(Mat2 src, Mat2 dest) {
		float determinant = src.determinant();
		if (determinant != 0) {
			if (dest == null) {
				dest = new Mat2();
			}

			float determinant_inv = 1f / determinant;
			float t00 = src.yy * determinant_inv;
			float t01 = -src.xy * determinant_inv;
			float t11 = src.xx * determinant_inv;
			float t10 = -src.yx * determinant_inv;

			dest.xx = t00;
			dest.xy = t01;
			dest.yx = t10;
			dest.yy = t11;

			return dest;
		}
		return null;
	}

	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append(xx).append(' ').append(yx).append(' ').append('\n');
		buf.append(xy).append(' ').append(yy).append(' ').append('\n');
		return buf.toString();
	}

	public Mat2 negate() {
		return negate(this);
	}

	public Mat2 negate(Mat2 dest) {
		return negate(this, dest);
	}

	public static Mat2 negate(Mat2 src, Mat2 dest) {
		if (dest == null) {
			dest = new Mat2();
		}

		dest.xx = -src.xx;
		dest.xy = -src.xy;
		dest.yx = -src.yx;
		dest.yy = -src.yy;

		return dest;
	}

	public Mat2 setIdentity() {
		return setIdentity(this);
	}

	public static Mat2 setIdentity(Mat2 src) {
		src.xx = 1.0f;
		src.xy = 0.0f;
		src.yx = 0.0f;
		src.yy = 1.0f;
		return src;
	}

	public Mat2 setZero() {
		return setZero(this);
	}

	public static Mat2 setZero(Mat2 src) {
		src.xx = 0.0f;
		src.xy = 0.0f;
		src.yx = 0.0f;
		src.yy = 0.0f;
		return src;
	}

	public float determinant() {
		return xx * yy - xy * yx;
	}

	public void setX(Vec2 v) {
		xx = v.x;
		xy = v.y;
	}

	public void setY(Vec2 v) {
		yx = v.x;
		yy = v.y;
	}
}
