/**
 * 
 */
package de.kjEngine.math;

import org.json.JSONObject;

import de.kjEngine.io.serilization.Serializable;
import de.kjEngine.io.serilization.Serializer;

/**
 * @author konst
 *
 */
public class Transform implements Serializable {

	public final Quat rotation = new Quat();
	public final Vec3 position = Vec3.create(), scale = Vec3.scale(1f);

	public final Mat4 localTransform = new Mat4(), globalTransform = new Mat4();

	public Transform parent;

	public Transform() {
	}

	public Transform(Vec3 position, Vec3 scale, Quat rotation) {
		this.position.set(position);
		this.scale.set(scale);
		this.rotation.set(rotation);
	}

	public Transform(float x, float y, float z, float sx, float sy, float sz, float rx, float ry, float rz, float rw) {
		position.set(x, y, z);
		scale.set(sx, sy, sz);
		rotation.set(rx, ry, rz, rw);
	}

	public Transform(Mat4 m) {
		position.set(m.wx, m.wy, m.wz);
		scale.set((float) Math.sqrt(m.xx * m.xx + m.xy * m.xy + m.xz * m.xz), (float) Math.sqrt(m.yx * m.yx + m.yy * m.yy + m.yz * m.yz),
				(float) Math.sqrt(m.zx * m.zx + m.zy * m.zy + m.zz * m.zz));
		rotation.setFromMatrix(m);
	}
	
	public Transform(Transform t) {
		this(t.position, t.scale, t.rotation);
	}

	public void update() {
		localTransform.setIdentity();
		localTransform.translate(position);

		rotation.get(localTransform);

		localTransform.scale(scale);

		if (parent == null) {
			globalTransform.set(localTransform);
		} else {
			Mat4.mul(parent.globalTransform, localTransform, globalTransform);
		}
	}

	private Vec3 globalPositionBuffer = Vec3.create();

	public Vec3 getGlobalPosition() {
		return globalPositionBuffer.set(globalTransform.wx, globalTransform.wy, globalTransform.wz);
	}

	public Transform set(Transform t) {
		position.set(t.position);
		scale.set(t.scale);
		rotation.set(t.rotation);
		return this;
	}

	@Override
	public String toString() {
		return "Transform [rotation=" + rotation + ", position=" + position + ", scale=" + scale + ", parent=" + parent + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((globalTransform == null) ? 0 : globalTransform.hashCode());
		result = prime * result + ((localTransform == null) ? 0 : localTransform.hashCode());
		result = prime * result + ((parent == null) ? 0 : parent.hashCode());
		result = prime * result + ((position == null) ? 0 : position.hashCode());
		result = prime * result + ((rotation == null) ? 0 : rotation.hashCode());
		result = prime * result + ((scale == null) ? 0 : scale.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Transform other = (Transform) obj;
		if (globalTransform == null) {
			if (other.globalTransform != null)
				return false;
		} else if (!globalTransform.equals(other.globalTransform))
			return false;
		if (localTransform == null) {
			if (other.localTransform != null)
				return false;
		} else if (!localTransform.equals(other.localTransform))
			return false;
		if (parent == null) {
			if (other.parent != null)
				return false;
		} else if (!parent.equals(other.parent))
			return false;
		if (position == null) {
			if (other.position != null)
				return false;
		} else if (!position.equals(other.position))
			return false;
		if (rotation == null) {
			if (other.rotation != null)
				return false;
		} else if (!rotation.equals(other.rotation))
			return false;
		if (scale == null) {
			if (other.scale != null)
				return false;
		} else if (!scale.equals(other.scale))
			return false;
		return true;
	}

	@Override
	public void deserialize(JSONObject obj) {
		Serializer.deserialize(obj.getJSONObject("position"), position);
		Serializer.deserialize(obj.getJSONObject("scale"), scale);
		Serializer.deserialize(obj.getJSONObject("rotation"), rotation);
	}

	@Override
	public JSONObject serialize() {
		JSONObject o = new JSONObject();
		o.put("position", Serializer.serialize(position));
		o.put("scale", Serializer.serialize(scale));
		o.put("rotation", Serializer.serialize(rotation));
		return o;
	}
}
