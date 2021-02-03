package de.kjEngine.math;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import de.kjEngine.io.serilization.Serializable;
import de.kjEngine.io.serilization.Serialize;

public class Vec3 extends Vec2 implements Serializable {

	public static final Vec3 X = new Vec3(1f, 0f, 0f), Y = new Vec3(0f, 1f, 0f), Z = new Vec3(0f, 0f, 1f);
	public static final Vec3 ZERO = new Vec3(0f, 0f, 0f);
	public static final Vec3 ONE = new Vec3(1f, 1f, 1f);

	public static Vec3 scale(float scale) {
		return new Vec3(scale, scale, scale);
	}

	public static Vec3 create() {
		return new Vec3();
	}

	public static Vec3 create(Vec3 src) {
		return new Vec3(src);
	}

	public static Vec3 create(float x, float y, float z) {
		return new Vec3(x, y, z);
	}

		@Serialize
	public float z;

	protected Vec3() {
	}

	protected Vec3(Vec3 src) {
		this(src.x, src.y, src.z);
	}

	protected Vec3(float x, float y, float z) {
		super(x, y);
		this.z = z;
	}

	public Vec3 set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}

	public Vec3 set(Vec3 src) {
		x = src.x;
		y = src.y;
		z = src.z;
		return this;
	}

	@Override
	public float lengthSquared() {
		return x * x + y * y + z * z;
	}

	public float length() {
		return (float) Math.sqrt(x * x + y * y + z * z);
	}

	public static Vec3 add(Vec3 left, Vec3 right, Vec3 dest) {
		if (dest == null) {
			return new Vec3(left.x + right.x, left.y + right.y, left.z + right.z);
		} else {
			dest.set(left.x + right.x, left.y + right.y, left.z + right.z);
			return dest;
		}
	}

	public static Vec3 sub(Vec3 left, Vec3 right, Vec3 dest) {
		if (dest == null) {
			return new Vec3(left.x - right.x, left.y - right.y, left.z - right.z);
		} else {
			dest.set(left.x - right.x, left.y - right.y, left.z - right.z);
			return dest;
		}
	}

	public static Vec3 cross(Vec3 left, Vec3 right, Vec3 dest) {
		if (dest == null) {
			dest = new Vec3();
		}
		dest.set(left.y * right.z - left.z * right.y, right.x * left.z - right.z * left.x, left.x * right.y - left.y * right.x);
		return dest;
	}

	@Override
	public Vec3 negate() {
		x = -x;
		y = -y;
		z = -z;
		return this;
	}

	public Vec3 negate(Vec3 dest) {
		if (dest == null)
			dest = new Vec3();
		dest.x = -x;
		dest.y = -y;
		dest.z = -z;
		return dest;
	}

	public Vec3 normalise() {
		return div(length());
	}

	public Vec3 normalise(Vec3 dest) {
		float l = length();

		if (dest == null)
			dest = new Vec3(x / l, y / l, z / l);
		else
			dest.set(x / l, y / l, z / l);

		return dest;
	}

	public static float dot(Vec3 left, Vec3 right) {
		return left.x * right.x + left.y * right.y + left.z * right.z;
	}

	public static float angle(Vec3 a, Vec3 b) {
		float dls = dot(a, b) / (a.length() * b.length());
		if (dls < -1f) {
			dls = -1f;
		} else if (dls > 1.0f) {
			dls = 1.0f;
		}
		return (float) Math.acos(dls);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(64);
		sb.append("Vector3f[");
		sb.append(x);
		sb.append(", ");
		sb.append(y);
		sb.append(", ");
		sb.append(z);
		sb.append(']');
		return sb.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vec3 other = (Vec3) obj;

		if (x == other.x && y == other.y && z == other.z)
			return true;

		return false;
	}

	public Vec3 add(Vec3 v) {
		x += v.x;
		y += v.y;
		z += v.z;
		return this;
	}

	public Vec3 add(float x, float y, float z) {
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}

	public Vec3 add(Vec3 v, float scale) {
		x += v.x * scale;
		y += v.y * scale;
		z += v.z * scale;
		return this;
	}

	public Vec3 sub(Vec3 v) {
		x -= v.x;
		y -= v.y;
		z -= v.z;
		return this;
	}

	public Vec3 sub(float x, float y, float z) {
		this.x -= x;
		this.y -= y;
		this.z -= z;
		return this;
	}

	public Vec3 sub(Vec3 v, float scale) {
		x -= v.x * scale;
		y -= v.y * scale;
		z -= v.z * scale;
		return this;
	}

	@Override
	public Vec3 mul(float scale) {
		x *= scale;
		y *= scale;
		z *= scale;
		return this;
	}

	public Vec3 mul(float xScale, float yScale, float zScale) {
		x *= xScale;
		y *= yScale;
		z *= zScale;
		return this;
	}

	public Vec3 mul(Vec3 scale) {
		x *= scale.x;
		y *= scale.y;
		z *= scale.z;
		return this;
	}

	public Vec3 div(float f) {
		return mul(1f / f);
	}

	public Vec3 div(Vec3 div) {
		x /= div.x;
		y /= div.y;
		z /= div.z;
		return this;
	}

	@Override
	public float max() {
		return Math.max(x, Math.max(y, z));
	}

	@Override
	public float min() {
		return Math.min(x, Math.min(y, z));
	}

	@Override
	public void deserialize(JSONObject obj) {
		if (obj.has("x")) {
			x = obj.getFloat("x");
		}
		if (obj.has("y")) {
			y = obj.getFloat("y");
		}
		if (obj.has("z")) {
			z = obj.getFloat("z");
		}
	}

	@Override
	public JSONObject serialize() {
		JSONObject o = new JSONObject();
		o.put("x", x);
		o.put("y", y);
		o.put("z", z);
		return o;
	}

	public static Vec3 interpolate(Vec3 a, Vec3 b, float factor, Vec3 target) {
		target.x = Real.interpolate(a.x, b.x, factor);
		target.y = Real.interpolate(a.y, b.y, factor);
		target.z = Real.interpolate(a.z, b.z, factor);
		return target;
	}

	public static Vec3 interpolate(Vec3 a, Vec3 b, Vec3 c, float factor, Vec3 target) {
		target.x = Real.interpolate(Real.interpolate(a.x, b.x, factor), Real.interpolate(b.x, c.x, factor), factor);
		target.y = Real.interpolate(Real.interpolate(a.y, b.y, factor), Real.interpolate(b.y, c.y, factor), factor);
		target.z = Real.interpolate(Real.interpolate(a.z, b.z, factor), Real.interpolate(b.z, c.z, factor), factor);
		return target;
	}

	public static Vec3 interpolate(Vec3 a, Vec3 b, float factor) {
		return Vec3.create(Real.interpolate(a.x, b.x, factor), Real.interpolate(a.y, b.y, factor), Real.interpolate(a.z, b.z, factor));
	}

	private static Vec3 p0Buffer = Vec3.create(), p1Buffer = Vec3.create(), p2Buffer = Vec3.create();

	public static Vec3 interpolate(Vec3 a, Vec3 b, Vec3 c, Vec3 d, float factor, Vec3 target) {
		Vec3 p0 = interpolate(a, b, factor, p0Buffer);
		Vec3 p1 = interpolate(b, c, factor, p1Buffer);
		Vec3 p2 = interpolate(c, d, factor, p2Buffer);
		return interpolate(p0, p1, p2, factor, target);
	}

	public static Vec3 interpolate(List<Vec3> p, float factor, Vec3 target) {
		if (p.size() == 1) {
			target.set(p.get(0));
			return target;
		}
		if (p.size() == 2) {
			interpolate(p.get(0), p.get(1), factor, target);
			return target;
		}
		if (p.size() == 3) {
			interpolate(p.get(0), p.get(1), p.get(2), factor, target);
			return target;
		}
		if (p.size() == 4) {
			interpolate(p.get(0), p.get(1), p.get(2), p.get(3), factor, target);
			return target;
		}
		List<Vec3> controlPoints = new ArrayList<>(p.size() - 1);
		for (int i = 0; i < p.size() - 1; i++) {
			controlPoints.add(interpolate(p.get(i), p.get(i + 1), factor));
		}
		interpolate(controlPoints, factor, target);
		return target;
	}

	private static Vec3 distSqDiffBuffer3 = Vec3.create();

	public static float distSq(Vec3 a, Vec3 b) {
		Vec3 diff = Vec3.sub(a, b, distSqDiffBuffer3);
		return diff.x * diff.x + diff.y * diff.y + diff.z * diff.z;
	}

	private static Vec3 distDiffBuffer3 = Vec3.create();

	public static float dist(Vec3 a, Vec3 b) {
		Vec3 diff = Vec3.sub(a, b, distDiffBuffer3);
		return (float) Math.sqrt(diff.x * diff.x + diff.y * diff.y + diff.z * diff.z);
	}

	public static void max(Vec3 a, Vec3 b, Vec3 target) {
		target.x = Math.max(a.x, b.x);
		target.y = Math.max(a.y, b.y);
		target.z = Math.max(a.z, b.z);
	}
	
	public static void min(Vec3 a, Vec3 b, Vec3 target) {
		target.x = Math.min(a.x, b.x);
		target.y = Math.min(a.y, b.y);
		target.z = Math.min(a.z, b.z);
	}
}
