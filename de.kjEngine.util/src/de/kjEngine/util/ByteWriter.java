/**
 * 
 */
package de.kjEngine.util;

/**
 * @author konst
 *
 */
public class ByteWriter {

	public static int write(byte[] target, int pointer, short value, Endian endian) {
		if (endian == Endian.BIG) {
			target[pointer++] = (byte) ((value >> 8) & 0xFF);
			target[pointer++] = (byte) (value & 0xFF);
		} else {
			target[pointer++] = (byte) (value & 0xFF);
			target[pointer++] = (byte) ((value >> 8) & 0xFF);
		}
		return pointer;
	}

	public static int write(byte[] target, int pointer, int value, Endian endian) {
		if (endian == Endian.BIG) {
			target[pointer++] = (byte) ((value >> 24) & 0xFF);
			target[pointer++] = (byte) ((value >> 16) & 0xFF);
			target[pointer++] = (byte) ((value >> 8) & 0xFF);
			target[pointer++] = (byte) (value & 0xFF);
		} else {
			target[pointer++] = (byte) (value & 0xFF);
			target[pointer++] = (byte) ((value >> 8) & 0xFF);
			target[pointer++] = (byte) ((value >> 16) & 0xFF);
			target[pointer++] = (byte) ((value >> 24) & 0xFF);
		}
		return pointer;
	}

	public static int write(byte[] target, int pointer, float value) {
		int bits = Float.floatToIntBits(value);
		target[pointer++] = (byte) (bits & 0xFF);
		target[pointer++] = (byte) ((bits >> 8) & 0xFF);
		target[pointer++] = (byte) ((bits >> 16) & 0xFF);
		target[pointer++] = (byte) ((bits >> 24) & 0xFF);
		return pointer;
	}
}
