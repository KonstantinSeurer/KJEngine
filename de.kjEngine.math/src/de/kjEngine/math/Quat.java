package de.kjEngine.math;

public class Quat extends Vec4 {

	public static final Quat IDENTITY = new Quat();

	public Quat() {
		setIdentity();
	}

	public Quat(Vec4 src) {
		set(src);
	}

	public Quat(float x, float y, float z, float w) {
		set(x, y, z, w);
	}

	public Quat(Mat3 mat) {
		setFromMatrix(mat);
	}

	public Quat setIdentity() {
		return setIdentity(this);
	}

	public static Quat setIdentity(Quat q) {
		q.x = 0;
		q.y = 0;
		q.z = 0;
		q.w = 1;
		return q;
	}

	@Override
	public String toString() {
		return "Quaternion: " + x + " " + y + " " + z + " " + w;
	}

	public static Quat mul(Quat left, Quat right, Quat dest) {
		if (dest == null) {
			dest = new Quat();
		}
		dest.set(left.x * right.w + left.w * right.x + left.y * right.z - left.z * right.y, left.y * right.w + left.w * right.y + left.z * right.x - left.x * right.z,
				left.z * right.w + left.w * right.z + left.x * right.y - left.y * right.x, left.w * right.w - left.x * right.x - left.y * right.y - left.z * right.z);
		return dest;
	}

	public static Quat mulInverse(Quat left, Quat right, Quat dest) {
		float n = 1f / right.lengthSquared();
		if (dest == null) {
			dest = new Quat();
		}
		dest.set((left.x * right.w - left.w * right.x - left.y * right.z + left.z * right.y) * n, (left.y * right.w - left.w * right.y - left.z * right.x + left.x * right.z) * n,
				(left.z * right.w - left.w * right.z - left.x * right.y + left.y * right.x) * n, (left.w * right.w + left.x * right.x + left.y * right.y + left.z * right.z) * n);

		return dest;
	}

	public final Quat setFromAxisAngle(Vec3 axis, float angle) {
		float s = Real.sin(0.5f * angle) / axis.length();
		x = axis.x * s;
		y = axis.y * s;
		z = axis.z * s;
		w = Real.cos(0.5f * angle);
		return this;
	}

	public final Quat rotate(Vec3 axis, float angle) {
		Quat rotation = new Quat().setFromAxisAngle(axis, -angle);
		mul(this, rotation, this);
		return this;
	}

	public final Quat rotateX(float angle) {
		return rotate(Vec3.X, angle);
	}

	public final Quat rotateY(float angle) {
		return rotate(Vec3.Y, angle);
	}

	public final Quat rotateZ(float angle) {
		return rotate(Vec3.Z, angle);
	}

	public final Quat setFromMatrix(Mat3 m) {
		return setFromMatrix(m, this);
	}

	public static Quat setFromMatrix(Mat3 m, Quat q) {
		return q.setFromMat(m.xx, m.xy, m.xz, m.yx, m.yy, m.yz, m.zx, m.zy, m.zz);
	}

	private Quat setFromMat(float m00, float m01, float m02, float m10, float m11, float m12, float m20, float m21, float m22) {
		float s;
		float tr = m00 + m11 + m22;
		if (tr >= 0.0) {
			s = (float) Math.sqrt(tr + 1.0);
			w = s * 0.5f;
			s = 0.5f / s;
			x = (m21 - m12) * s;
			y = (m02 - m20) * s;
			z = (m10 - m01) * s;
		} else {
			float max = Math.max(Math.max(m00, m11), m22);
			if (max == m00) {
				s = (float) Math.sqrt(m00 - (m11 + m22) + 1.0);
				x = s * 0.5f;
				s = 0.5f / s;
				y = (m01 + m10) * s;
				z = (m20 + m02) * s;
				w = (m21 - m12) * s;
			} else if (max == m11) {
				s = (float) Math.sqrt(m11 - (m22 + m00) + 1.0);
				y = s * 0.5f;
				s = 0.5f / s;
				z = (m12 + m21) * s;
				x = (m01 + m10) * s;
				w = (m02 - m20) * s;
			} else {
				s = (float) Math.sqrt(m22 - (m00 + m11) + 1.0);
				z = s * 0.5f;
				s = 0.5f / s;
				x = (m20 + m02) * s;
				y = (m12 + m21) * s;
				w = (m10 - m01) * s;
			}
		}
		return this;
	}

	public Mat3 toRotationMatrix() {
		Mat3 matrix = new Mat3();
		get(matrix);
		return matrix;
	}

	public static Quat interpolate(Quat a, Quat b, float f) {
		return interpolate(a, b, f, null);
	}

	public static Quat interpolate(Quat a, Quat b, float f, Quat target) {
		if (target == null) {
			target = new Quat();
		}
		float dot = dot(a, b);
		float fI = 1f - f;
		if (dot < 0) {
			target.w = fI * a.w + f * -b.w;
			target.x = fI * a.x + f * -b.x;
			target.y = fI * a.y + f * -b.y;
			target.z = fI * a.z + f * -b.z;
		} else {
			target.w = fI * a.w + f * b.w;
			target.x = fI * a.x + f * b.x;
			target.y = fI * a.y + f * b.y;
			target.z = fI * a.z + f * b.z;
		}
		target.normalise();
		return target;
	}

	@Override
	public float max() {
		return Math.max(x, Math.max(y, Math.max(z, w)));
	}

	@Override
	public float min() {
		return Math.min(x, Math.min(y, Math.min(z, w)));
	}

	public Quat get(Mat3 target) {
		final float xy = x * y;
		final float xz = x * z;
		final float xw = x * w;
		final float yz = y * z;
		final float yw = y * w;
		final float zw = z * w;
		final float xSquared = x * x;
		final float ySquared = y * y;
		final float zSquared = z * z;
		target.xx = 1 - 2 * (ySquared + zSquared);
		target.xy = 2 * (xy - zw);
		target.xz = 2 * (xz + yw);
		target.yx = 2 * (xy + zw);
		target.yy = 1 - 2 * (xSquared + zSquared);
		target.yz = 2 * (yz - xw);
		target.zx = 2 * (xz - yw);
		target.zy = 2 * (yz + xw);
		target.zz = 1 - 2 * (xSquared + ySquared);
		return this;
	}
}
