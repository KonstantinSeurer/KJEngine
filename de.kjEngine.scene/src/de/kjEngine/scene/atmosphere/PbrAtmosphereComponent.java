/**
 * 
 */
package de.kjEngine.scene.atmosphere;

import de.kjEngine.scene.SceneComponent;
import de.kjEngine.scene.TransformComponent;

/**
 * @author konst
 *
 */
public class PbrAtmosphereComponent extends SceneComponent<TransformComponent<?, ?>, PbrAtmosphereComponent> {
	
	public float innerRadius, outerRadius, baseDensity, densityFalloff;

	public PbrAtmosphereComponent(float innerRadius, float outerRadius, float baseDensity, float densityFalloff) {
		super(LATE);
		this.innerRadius = innerRadius;
		this.outerRadius = outerRadius;
		this.baseDensity = baseDensity;
		this.densityFalloff = densityFalloff;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(baseDensity);
		result = prime * result + Float.floatToIntBits(densityFalloff);
		result = prime * result + Float.floatToIntBits(innerRadius);
		result = prime * result + Float.floatToIntBits(outerRadius);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PbrAtmosphereComponent other = (PbrAtmosphereComponent) obj;
		if (Float.floatToIntBits(baseDensity) != Float.floatToIntBits(other.baseDensity))
			return false;
		if (Float.floatToIntBits(densityFalloff) != Float.floatToIntBits(other.densityFalloff))
			return false;
		if (Float.floatToIntBits(innerRadius) != Float.floatToIntBits(other.innerRadius))
			return false;
		if (Float.floatToIntBits(outerRadius) != Float.floatToIntBits(other.outerRadius))
			return false;
		return true;
	}
}
