package de.kjEngine.math;

import java.util.ArrayList;
import java.util.List;

import de.kjEngine.io.serilization.Serialize;
import de.kjEngine.math.random.Rand;

public class Vec2 {

	public static Vec2 rotation(float rot) {
		return new Vec2(Real.cos(rot), Real.sin(rot));
	}

	public static Vec2 create() {
		return new Vec2();
	}

	public static Vec2 create(float x, float y) {
		return new Vec2(x, y);
	}

	public static Vec2 create(Vec2 v) {
		return new Vec2(v);
	}

	@Serialize
	public float x, y;

	protected Vec2() {
	}

	protected Vec2(Vec2 src) {
		x = src.x;
		y = src.y;
	}

	protected Vec2(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public Vec2 set(float x, float y) {
		this.x = x;
		this.y = y;
		return this;
	}

	public Vec2 set(Vec2 src) {
		x = src.x;
		y = src.y;
		return this;
	}

	public float lengthSquared() {
		return x * x + y * y;
	}

	public float length() {
		return Real.sqrt(x * x + y * y);
	}

	public Vec2 add(float x, float y) {
		this.x += x;
		this.y += y;
		return this;
	}

	public Vec2 add(Vec2 v) {
		this.x += v.x;
		this.y += v.y;
		return this;
	}

	public Vec2 negate() {
		x = -x;
		y = -y;
		return this;
	}

	public Vec2 negate(Vec2 dest) {
		if (dest == null) {
			dest = new Vec2();
		}
		dest.x = -x;
		dest.y = -y;
		return dest;
	}

	public Vec2 normalise(Vec2 dest) {
		float l = length();

		if (dest == null) {
			dest = new Vec2(x / l, y / l);
		} else {
			dest.set(x / l, y / l);
		}

		return dest;
	}

	public static float dot(Vec2 left, Vec2 right) {
		return left.x * right.x + left.y * right.y;
	}

	public static float angle(Vec2 a, Vec2 b) {
		float dls = dot(a, b) / (a.length() * b.length());
		if (dls < -1f) {
			dls = -1f;
		} else if (dls > 1.0f) {
			dls = 1.0f;
		}
		return (float) Math.acos(dls);
	}

	public static Vec2 add(Vec2 left, Vec2 right, Vec2 dest) {
		if (dest == null) {
			return new Vec2(left.x + right.x, left.y + right.y);
		} else {
			dest.set(left.x + right.x, left.y + right.y);
			return dest;
		}
	}

	public static Vec2 sub(Vec2 left, Vec2 right, Vec2 dest) {
		if (dest == null) {
			return new Vec2(left.x - right.x, left.y - right.y);
		} else {
			dest.set(left.x - right.x, left.y - right.y);
			return dest;
		}
	}

	public Vec2 mul(float scale) {
		x *= scale;
		y *= scale;
		return this;
	}

	public Vec2 mul(float sx, float sy) {
		x *= sx;
		y *= sy;
		return this;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder(64);

		sb.append("Vector2f[");
		sb.append(x);
		sb.append(", ");
		sb.append(y);
		sb.append(']');
		return sb.toString();
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vec2 other = (Vec2) obj;

		if (x == other.x && y == other.y)
			return true;

		return false;
	}

	public float max() {
		return Math.max(x, y);
	}

	public float min() {
		return Math.max(x, y);
	}

	private static Vec2 distSqDiffBuffer2 = new Vec2();

	public static float distSq(Vec2 a, Vec2 b) {
		Vec2 diff = Vec2.sub(a, b, distSqDiffBuffer2);
		return diff.x * diff.x + diff.y * diff.y;
	}

	private static Vec2 distDiffBuffer2 = new Vec2();

	public static float dist(Vec2 a, Vec2 b) {
		Vec2 diff = Vec2.sub(a, b, distDiffBuffer2);
		return (float) Math.sqrt(diff.x * diff.x + diff.y * diff.y);
	}

	public static Vec2[] evenDistribution(float xMin, float xMax, float yMin, float yMax, float radius, int count, int tries) {
		List<Vec2> result = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			Vec2 vec = new Vec2();
			for (int j = 0; j < tries; j++) {
				vec.x = Rand.value(xMin, xMax);
				vec.y = Rand.value(yMin, yMax);
				boolean valid = true;
				for (Vec2 sample : result) {
					if (Vec2.dist(sample, vec) < radius) {
						valid = false;
						break;
					}
				}
				if (valid) {
					result.add(vec);
					break;
				}
			}
		}
		return result.toArray(new Vec2[result.size()]);
	}

	public static Vec2 interpolate(Vec2 a, Vec2 b, float factor) {
		return new Vec2(Real.interpolate(a.x, b.x, factor), Real.interpolate(a.y, b.y, factor));
	}
}
