/**
 * 
 */
package de.kjEngine.math;

/**
 * @author konst
 *
 */
public class Real {

	public static final float PI = (float) Math.PI;
	public static final float TWO_PI = PI * 2f;;
	public static final float HALF_PI = PI * 0.5f;
	public static final float SQRT_2 = (float) Math.sqrt(2.0);

	private Real() {
	}

	public static float abs(float f) {
		return f < 0f ? -f : f;
	}

	public static int map(int i, int src_min, int src_max, int dst_min, int dst_max) {
		float f = (float) (i - src_min) / (float) (src_max - src_min);
		return dst_min + (int) (f * (float) (dst_max - dst_min));
	}

	public static float map(float f, float src_min, float src_max, float dst_min, float dst_max) {
		return dst_min + (f - src_min) * (dst_max - dst_min) / (src_max - src_min);
	}

	public static float fract(float f) {
		return (float) (f - StrictMath.floor(f));
	}

	public static float clamp(float v, float a, float b) {
		if (v < a) {
			return a;
		}
		if (v > b) {
			return b;
		}
		return v;
	}

	public static int clamp(int v, int a, int b) {
		if (v < a) {
			return a;
		}
		if (v > b) {
			return b;
		}
		return v;
	}

	public static float cos(float a) {
		return (float) StrictMath.cos(a);
	}

	public static float sin(float a) {
		return (float) StrictMath.sin(a);
	}

	public static float tan(float a) {
		return (float) StrictMath.tan(a);
	}

	public static float interpolate(float a, float b, float factor) {
		return (1f - factor) * a + factor * b;
	}

	public static float normalizeAngle(float a) {
		return TWO_PI * fract(a / TWO_PI);
	}

	public static final float toRadiens(float a) {
		return a / 360f * TWO_PI;
	}

	public static final float toDegrees(float a) {
		return a / TWO_PI * 360f;
	}

	public static float interpolateRotationAngle(float a, float b, float factor) {
		a = normalizeAngle(a);
		b = normalizeAngle(b);
		float d1 = abs(a - b);
		float d2 = abs(a - b - TWO_PI);
		float d3 = abs(a - b + TWO_PI);
		if (d2 < d1 && d2 < d3) {
			return normalizeAngle(interpolate(a, b + TWO_PI, factor));
		}
		if (d3 < d1 && d3 < d2) {
			return normalizeAngle(interpolate(a, b - TWO_PI, factor));
		}
		return normalizeAngle(interpolate(a, b, factor));
	}

	public static double log(double base, double f) {
		return Math.log(f) / Math.log(base);
	}

	public static float log(float base, float f) {
		return (float) (Math.log(f) / Math.log(base));
	}

	public static int log(int base, int f) {
		return (int) (Math.log(f) / Math.log(base));
	}

	public static float pow(float f, float exp) {
		return (float) Math.pow(f, exp);
	}

	public static float sqrt(float f) {
		return (float) StrictMath.sqrt(f);
	}

	public static float atan(float f) {
		return (float) StrictMath.atan(f);
	}

	public static float atan(float y, float x) {
		return normalizeAngle((float) StrictMath.atan2(y, x));
	}

	public static float exp(float x) {
		return (float) StrictMath.exp(x);
	}
}
