/**
 * 
 */
package de.kjEngine.math.random;

/**
 * @author konst
 *
 */
public interface NoiseImplementation {

	public float noise(float x);

	public float noise(float x, float y);

	public float noise(float x, float y, float z);

	public float noise(float x, float y, float z, float w);
}
