/**
 * 
 */
package de.kjEngine.audio.sampling;

import de.kjEngine.math.Real;

/**
 * @author konst
 *
 */
public class SinSampler implements Sampler {
	
	private float frequency;

	public SinSampler(float frequency) {
		this.frequency = frequency;
	}

	@Override
	public float sample(float t) {
		return Real.sin(t * frequency * Real.PI * 2f);
	}
}
