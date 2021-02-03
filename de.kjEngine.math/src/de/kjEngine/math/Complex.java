package de.kjEngine.math;

import de.kjEngine.io.serilization.Serialize;

public class Complex {

	@Serialize
	public float a, b;

	public Complex() {
		this(0f, 0f);
	}

	public Complex(float a, float b) {
		this.a = a;
		this.b = b;
	}

	public Vec2 asVector() {
		return new Vec2(a, b);
	}

	public float[] asArray() {
		return new float[] { a, b };
	}

	public Complex add(Complex c) {
		return add(this, c, this);
	}
	
	public static Complex add(Complex a, Complex b, Complex dest) {
		if (dest == null) {
			dest = new Complex();
		}
		dest.a = a.a + b.a;
		dest.b = a.b + b.b;
		return dest;
	}
	
	public Complex sub(Complex c) {
		return sub(this, c, this);
	}
	
	public static Complex sub(Complex a, Complex b, Complex dest) {
		if (dest == null) {
			dest = new Complex();
		}
		dest.a = a.a - b.a;
		dest.b = a.b - b.b;
		return dest;
	}

	public Complex mul(Complex c) {
		return mul(this, c, this);
	}
	
	public static Complex mul(Complex a, Complex b, Complex dest) {
		if (dest == null) {
			dest = new Complex();
		}
		dest.a = a.a * b.a - a.b * b.b;
		dest.b = a.a * b.b + a.b * b.a;
		return dest;
	}
}
