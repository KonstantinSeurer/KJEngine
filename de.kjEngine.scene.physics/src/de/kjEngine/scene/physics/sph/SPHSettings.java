/**
 * 
 */
package de.kjEngine.scene.physics.sph;

import de.kjEngine.io.serilization.Serialize;
import de.kjEngine.math.Real;

/**
 * @author konst
 *
 */
public class SPHSettings {
	
	@Serialize
	public float boundarySize = 5f;
	
	@Serialize
	public float particleRadius = 0.05f;
	
	@Serialize
	public float restingDensity = 1000f;
	
	@Serialize
	public float particleMass = restingDensity * particleRadius * particleRadius * particleRadius * Real.PI / 6f;
	
	@Serialize
	public float viscosity = 1f;
	
	@Serialize
	public float gassConstant = 0.1f;
	
	@Serialize
	public float maxVelocity = 1f;
	
	@Serialize
	public float wallElasticity = 1f;
	
	@Serialize
	public float gravity = 0.1f;
}
