package de.kjEngine.math.random;

import java.util.Random;

public class CosineNoiseImplementation implements NoiseImplementation {
	
	private Random random = new Random();

	public CosineNoiseImplementation() {
	}

	@Override
	public float noise(float x, float y) {
		int intX = (int) x;
		int intY = (int) y;
		float fracX = x - intX;
		float fracY = y - intY;

		float v1 = getSmoothValue(intX, intY);
		float v2 = getSmoothValue(intX + 1, intY);
		float v3 = getSmoothValue(intX, intY + 1);
		float v4 = getSmoothValue(intX + 1, intY + 1);
		float i1 = interpolate(v1, v2, fracX);
		float i2 = interpolate(v3, v4, fracX);
		return interpolate(i1, i2, fracY);
	}

	private float interpolate(float a, float b, float blend) {
		if (blend < 0f)
			blend *= -1f;
		double tetha = blend * Math.PI;
		float f = (float) (1f - Math.cos(tetha)) * 0.5f;
		return a * (1f - f) + b * f;
	}

	private float getSmoothValue(int x, int y) {
		float corners = (getValue(x - 1, y - 1) + getValue(x + 1, y - 1) + getValue(x + 1, y + 1)
				+ getValue(x - 1, y + 1)) / 16f;
		float sides = (getValue(x - 1, y) + getValue(x + 1, y) + getValue(x, y - 1) + getValue(x, y + 1)) / 8f;
		float center = getValue(x, y) / 4f;
		return corners + sides + center;
	}

	private float getValue(int x, int y) {
		random.setSeed(x * 99100 + y * 400370);
		return random.nextFloat();
	}

	@Override
	public float noise(float x) {
		return noise(x, 0f);
	}

	@Override
	public float noise(float x, float y, float z) {
		return noise(x, y);
	}

	@Override
	public float noise(float x, float y, float z, float w) {
		return noise(x, y);
	}
}
