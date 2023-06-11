/**
 * 
 */
package de.kjEngine.audio.sampling;

import de.kjEngine.math.random.Rand;

/**
 * @author konst
 *
 */
public class NoiseSampler implements Sampler {
	
	private float frequency;

	public NoiseSampler(float frequency) {
		this.frequency = frequency;
	}

	@Override
	public float sample(float t) {
		return Rand.noise(Rand.SIMPLEX, frequency * t) * 2f - 1f;
	}
}
