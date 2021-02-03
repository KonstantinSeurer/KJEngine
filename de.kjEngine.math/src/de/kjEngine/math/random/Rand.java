/**
 * 
 */
package de.kjEngine.math.random;

import java.util.Random;

import de.kjEngine.math.Vec2;
import de.kjEngine.math.Vec3;
import de.kjEngine.math.Vec4;
import de.kjEngine.util.container.Array;

/**
 * @author konst
 *
 */
public class Rand {
	
	private static Array<NoiseImplementation> noiseImplementations = new Array<>();
	private static Random random = new Random();
	
	public static final int SIMPLEX = registerNoiseImplementation(new SimplexNoiseImplementation());
	public static final int COS = registerNoiseImplementation(new CosineNoiseImplementation());
	public static final int VERONOI = registerNoiseImplementation(new VeronoiNoiseImplementation());
	
	public static int registerNoiseImplementation(NoiseImplementation noise) {
		noiseImplementations.add(noise);
		return noiseImplementations.length() - 1;
	}

	public static float noise(int impl, float x) {
		return noiseImplementations.get(impl).noise(x);
	}

	public static float noise(int impl, float x, float y) {
		return noiseImplementations.get(impl).noise(x, y);
	}

	public static float noise(int impl, Vec2 pos) {
		return noise(impl, pos.x, pos.y);
	}

	public static float noise(int impl, float x, float y, float z) {
		return noiseImplementations.get(impl).noise(x, y, z);
	}

	public static float noise(int impl, Vec3 pos) {
		return noise(impl, pos.x, pos.y, pos.z);
	}

	public static float noise(int impl, float x, float y, float z, float w) {
		return noiseImplementations.get(impl).noise(x, y, z, w);
	}

	public static float noise(int impl, Vec4 pos) {
		return noise(impl, pos.x, pos.y, pos.z, pos.w);
	}
	
	public static void setSeed(long seed) {
		random.setSeed(seed);
	}

	public static float value() {
		return (float) random.nextFloat();
	}

	public static float value(float min, float max) {
		return value() * (max - min) + min;
	}
}
