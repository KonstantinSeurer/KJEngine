/* 
 * Copyright (c) 2002-2008 LWJGL Project
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are 
 * met:
 * 
 * * Redistributions of source code must retain the above copyright 
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'LWJGL' nor the names of 
 *   its contributors may be used to endorse or promote products derived 
 *   from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR 
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING 
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package de.kjEngine.math;

import de.kjEngine.io.serilization.Serialize;

/**
 *
 * Holds a 4-tuple vector.
 * 
 * @author cix_foo <cix_foo@users.sourceforge.net>
 * @version $Revision$ $Id$
 */

public class Vec4 extends Vec3 {

	public static Vec4 create() {
		return new Vec4();
	}

	public static Vec4 create(float x, float y, float z, float w) {
		return new Vec4(x, y, z, w);
	}

	public static Vec4 create(Vec4 v) {
		return new Vec4(v);
	}

	public static Vec4 create(Vec3 v, float w) {
		return new Vec4(v.x, v.y, v.z, w);
	}

	public static Vec4 create(Vec2 v, float z, float w) {
		return new Vec4(v.x, v.y, z, w);
	}

	public static Vec4 create(Vec2 xy, Vec2 zw) {
		return new Vec4(xy.x, xy.y, zw.x, zw.y);
	}

	@Serialize
	public float w;

	/**
	 * Constructor for Vector4f.
	 */
	protected Vec4() {
	}

	/**
	 * Constructor
	 */
	protected Vec4(Vec4 src) {
		this(src.x, src.y, src.z, src.w);
	}

	/**
	 * Constructor
	 */
	protected Vec4(float x, float y, float z, float w) {
		super(x, y, z);
		this.w = w;
	}

	public Vec4 set(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
		return this;
	}

	/**
	 * Load from another Vector4f
	 * 
	 * @param src The source vector
	 * @return this
	 */
	public Vec4 set(Vec4 src) {
		x = src.x;
		y = src.y;
		z = src.z;
		w = src.w;
		return this;
	}

	public float lengthSquared() {
		return x * x + y * y + z * z + w * w;
	}

	@Override
	public float length() {
		return Real.sqrt(x * x + y * y + z * z + w * w);
	}

	public Vec4 add(float x, float y, float z, float w) {
		this.x += x;
		this.y += y;
		this.z += z;
		this.w += w;
		return this;
	}

	public static Vec4 add(Vec4 left, Vec4 right, Vec4 dest) {
		if (dest == null) {
			return new Vec4(left.x + right.x, left.y + right.y, left.z + right.z, left.w + right.w);
		} else {
			dest.set(left.x + right.x, left.y + right.y, left.z + right.z, left.w + right.w);
			return dest;
		}
	}

	public static Vec4 sub(Vec4 left, Vec4 right, Vec4 dest) {
		if (dest == null) {
			return new Vec4(left.x - right.x, left.y - right.y, left.z - right.z, left.w - right.w);
		} else {
			dest.set(left.x - right.x, left.y - right.y, left.z - right.z, left.w - right.w);
			return dest;
		}
	}

	public Vec4 negate() {
		x = -x;
		y = -y;
		z = -z;
		w = -w;
		return this;
	}

	public Vec4 negate(Vec4 dest) {
		if (dest == null)
			dest = new Vec4();
		dest.x = -x;
		dest.y = -y;
		dest.z = -z;
		dest.w = -w;
		return dest;
	}

	public Vec4 normalise(Vec4 dest) {
		float l = 1f / length();

		if (dest == null) {
			dest = new Vec4(x * l, y * l, z * l, w * l);
		} else {
			dest.set(x * l, y * l, z * l, w * l);
		}

		return dest;
	}

	public Vec4 normalise() {
		div(length());
		return this;
	}

	public static float dot(Vec4 left, Vec4 right) {
		return left.x * right.x + left.y * right.y + left.z * right.z + left.w * right.w;
	}

	public static float angle(Vec4 a, Vec4 b) {
		float dls = dot(a, b) / (a.length() * b.length());
		if (dls < -1f) {
			dls = -1f;
		} else if (dls > 1.0f) {
			dls = 1.0f;
		}
		return (float) Math.acos(dls);
	}

	public Vec4 mul(float scale) {
		x *= scale;
		y *= scale;
		z *= scale;
		w *= scale;
		return this;
	}

	public Vec4 div(float f) {
		mul(1f / f);
		return this;
	}

	public String toString() {
		return "Vector4f: " + x + " " + y + " " + z + " " + w;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vec4 other = (Vec4) obj;

		if (x == other.x && y == other.y && z == other.z && w == other.w)
			return true;

		return false;
	}

	@Override
	public float max() {
		return Math.max(x, Math.max(y, Math.max(z, w)));
	}

	@Override
	public float min() {
		return Math.min(x, Math.min(y, Math.min(z, w)));
	}

	public static Vec4 interpolate(Vec4 a, Vec4 b, float factor) {
		return new Vec4(Real.interpolate(a.x, b.x, factor), Real.interpolate(a.y, b.y, factor), Real.interpolate(a.z, b.z, factor), Real.interpolate(a.w, b.w, factor));
	}
}
