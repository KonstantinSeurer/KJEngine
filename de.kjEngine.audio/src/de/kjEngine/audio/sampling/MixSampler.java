/**
 * 
 */
package de.kjEngine.audio.sampling;

import de.kjEngine.math.Real;

/**
 * @author konst
 *
 */
public class MixSampler implements Sampler {
	
	private Sampler a, b;
	private float factor;

	public MixSampler(Sampler a, Sampler b, float factor) {
		this.a = a;
		this.b = b;
		this.factor = factor;
	}

	@Override
	public float sample(float t) {
		return Real.interpolate(a.sample(t), b.sample(t), factor);
	}
}
