/**
 * 
 */
package de.kjEngine.audio.sampling;

import de.kjEngine.math.Real;

/**
 * @author konst
 *
 */
public class RectSampler implements Sampler {
	
	private float frequency;
	private float dutyCycle;

	public RectSampler(float frequency, float dutyCycle) {
		this.frequency = frequency;
		this.dutyCycle = dutyCycle;
	}

	@Override
	public float sample(float t) {
		float f = Real.fract(t * frequency);
		return f < dutyCycle ? 1f : -1f;
	}
}
