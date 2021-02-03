/**
 * 
 */
package de.kjEngine.math.random;

import java.util.Random;

/**
 * @author konst
 *
 */
public class VeronoiNoiseImplementation implements NoiseImplementation {

	public VeronoiNoiseImplementation() {
	}
	
	private Random random = new Random();
	
	private float getXOff(int x, int y, int z) {
		random.setSeed(x * 99100 + y * 400370 + z * 23759);
		return random.nextFloat();
	}
	
	private float getYOff(int x, int y, int z) {
		random.setSeed(x * 99100 + y * 400370 + z * 23759 + 985);
		return random.nextFloat();
	}
	
	private float getZOff(int x, int y, int z) {
		random.setSeed(x * 99100 + y * 400370 + z * 23759 + 84358);
		return random.nextFloat();
	}

	@Override
	public float noise(float x) {
		return 0;
	}

	@Override
	public float noise(float x, float y) {
		return 0;
	}

	@Override
	public float noise(float x, float y, float z) {
		float min = 99999999f;
		for (int xo = -1; xo <= 1; xo++) {
			for (int yo = -1; yo <= 1; yo++) {
				for (int zo = -1; zo <= 1; zo++) {
					int cellX = (int) x + xo;
					int cellY = (int) y + yo;
					int cellZ = (int) z + zo;
					float xp = (float) cellX + getXOff(cellX, cellY, cellZ);
					float yp = (float) cellY + getYOff(cellX, cellY, cellZ);
					float zp = (float) cellZ + getZOff(cellX, cellY, cellZ);
					float xd = x - xp;
					float yd = y - yp;
					float zd = z - zp;
					min = Math.min(min, xd * xd + yd * yd + zd * zd);
				}
			}
		}
		return (float) Math.sqrt(min);
	}

	@Override
	public float noise(float x, float y, float z, float w) {
		return 0;
	}
}
