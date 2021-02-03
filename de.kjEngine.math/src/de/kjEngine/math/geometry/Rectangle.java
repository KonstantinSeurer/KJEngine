package de.kjEngine.math.geometry;

public class Rectangle {
	
	public float x, y, width, height;
	
	public Rectangle() {
		this(0f, 0f, 0f, 0f);
	}

	public Rectangle(float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public Rectangle(Rectangle r) {
		this(r.x, r.y, r.width, r.height);
	}
	
	public Rectangle set(Rectangle r) {
		x = r.x;
		y = r.y;
		width = r.width;
		height = r.height;
		return this;
	}

	@Override
	public String toString() {
		return "Rectangle [x=" + x + ", y=" + y + ", width=" + width + ", height=" + height + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(height);
		result = prime * result + Float.floatToIntBits(width);
		result = prime * result + Float.floatToIntBits(x);
		result = prime * result + Float.floatToIntBits(y);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Rectangle))
			return false;
		Rectangle other = (Rectangle) obj;
		if (height != other.height)
			return false;
		if (width != other.width)
			return false;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}
	
	public static boolean intersectsRect(float x0, float y0, float w0, float h0, float x1, float y1, float w1, float h1) {
		return !(x0 + w0 < x1 || x0 > x1 + w1 || y0 > y1 + h1 || y0 + h0 < y1);
	}

	public static boolean intersectsRect(Rectangle a, float x1, float y1, float w1, float h1) {
		return intersectsRect(a.x, a.y, a.width, a.height, x1, y1, w1, h1);
	}

	public static boolean intersectsRect(Rectangle a, Rectangle b) {
		return intersectsRect(a.x, a.y, a.width, a.height, b.x, b.y, b.width, b.height);
	}

	public static boolean intersectsPoint(float x, float y, float w, float h, float px, float py) {
		return px > x && px < x + w && py > y && py < y + h;
	}

	public static Rectangle intersection(Rectangle a, Rectangle b) {
		if (!intersectsRect(a, b)) {
			return new Rectangle();
		}
		Rectangle r = new Rectangle();
		r.x = Math.max(a.x, b.x);
		r.y = Math.max(a.y, b.y);
		r.width = Math.min(a.x + a.width, b.x + b.width) - r.x;
		r.height = Math.min(a.y + a.height, b.y + b.height) - r.y;
		return r;
	}
}
