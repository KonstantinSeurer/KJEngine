/**
 * 
 */
package de.kjEngine.math;

/**
 * @author konst
 *
 */
public class HalfFloat {

	public static short floatToHalfFloat(float f) {
		int bits = Float.floatToIntBits(f);
		int sign = bits >> 31;
		int exponent = ((bits >> 23) & 0b11111111) - 127;
		int mantissa = bits & 0b11111111111111111111111;

		int newMantissa = mantissa >> 13;
		int newExponent = exponent + 15;

		if (bits == 0x7f800000 || bits == 0xff800000 || newExponent >= 31) { // infinity
			newExponent = 31;
			newMantissa = 0;
		} else if (bits == 0x7fc00000) { // NaN
			newExponent = 31;
			newMantissa = 1;
		} else if (f == 0f || newExponent <= 0) { // zero
			newExponent = 0;
			newMantissa = 0;
		}

		return (short) ((sign << 15) | (newExponent << 10) | newMantissa);
	}
	
	public static float halfFloatToFloat(short f) {
		int sign = f >> 15;
		int exponent = ((f >> 10) & 0b11111) - 15;
		int mantissa = f & 0b1111111111;
		
		if (exponent == 0 && mantissa == 0) {
			return 0f;
		} else if (exponent == 16) {
			if (mantissa == 0) {
				return sign == 0 ? Float.POSITIVE_INFINITY : Float.NEGATIVE_INFINITY;
			} else {
				return Float.NaN;
			}
		} else {
			return Float.intBitsToFloat((sign << 31) | ((exponent + 127) << 23) | (mantissa << 13));
		}
	}
}
