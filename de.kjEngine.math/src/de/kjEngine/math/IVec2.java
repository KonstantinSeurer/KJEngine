package de.kjEngine.math;

import de.kjEngine.io.serilization.Serialize;

public class IVec2 {

	@Serialize
	public int x, y;

	public IVec2() {
		this(0, 0);
	}

	public IVec2(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public IVec2(IVec2 src) {
		this(src.x, src.y);
	}

	public int length() {
		return (int) Math.sqrt(lengthSqared());
	}

	public int lengthSqared() {
		return x * x + y * y;
	}

	public IVec2 add(IVec2 b) {
		x += b.x;
		y += b.y;
		return this;
	}

	public IVec2 sub(IVec2 b) {
		x -= b.x;
		y -= b.y;
		return this;
	}

	public IVec2 mul(IVec2 b) {
		x *= b.x;
		y *= b.y;
		return this;
	}

	public IVec2 div(IVec2 b) {
		x /= b.x;
		y /= b.y;
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
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
		IVec2 other = (IVec2) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Vector2i [x=" + x + ", y=" + y + "]";
	}
}
