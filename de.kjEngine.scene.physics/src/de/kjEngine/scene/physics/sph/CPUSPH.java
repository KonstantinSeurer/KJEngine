/**
 * 
 */
package de.kjEngine.scene.physics.sph;

import java.util.ArrayList;
import java.util.List;

import de.kjEngine.math.Real;
import de.kjEngine.math.Vec3;

/**
 * @author konst
 *
 */
public class CPUSPH {

	private static class Particle extends SPHParticle {
		public Vec3 fPressure = Vec3.create(), fViscosity = Vec3.create(), fExternal = Vec3.create();
		public float density, pressure;
	}

	private final List<Particle> particles = new ArrayList<>();

	private float invBoundarySize;

	private float h2;
	private float wFactor, gradWFactor, laplaceWFactor;
	private int gridSize;
	private int gridDepth = 30;

	private Particle[] grid;
	private int[] gridParticleCounts;

	private SPHSettings settings;

	public CPUSPH(SPHSettings settings) {
		this.settings = settings;
		h2 = settings.particleRadius * settings.particleRadius;
		wFactor = 315f / 64f / Real.PI / Real.pow(settings.particleRadius, 9f);
		gradWFactor = -45f / Real.PI / Real.pow(settings.particleRadius, 6f);
		laplaceWFactor = -gradWFactor;

		gridSize = (int) (settings.boundarySize / settings.particleRadius * 0.5f);

		invBoundarySize = 1f / settings.boundarySize;

		grid = new Particle[gridSize * gridSize * gridSize * gridDepth];
		gridParticleCounts = new int[gridSize * gridSize * gridSize];
	}

	private float W(float r2) {
		float f = (h2 - r2);
		return wFactor * f * f * f;
	}

	private void gradW(float x, float y, float z, float r, Vec3 target) {
		target.set(x, y, z).mul(gradWFactor * (settings.particleRadius - r) * (settings.particleRadius - r) / r);
	}

	private float laplaceW(float r) {
		return laplaceWFactor * (settings.particleRadius - r);
	}

	private void getDensity(Particle p) {
		p.density = 0f;

		float gridX = p.position.x * invBoundarySize * gridSize;
		float gridY = p.position.y * invBoundarySize * gridSize;
		float gridZ = p.position.z * invBoundarySize * gridSize;

		int startX = Math.max((int) (gridX - 0.5f), 0);
		int startY = Math.max((int) (gridY - 0.5f), 0);
		int startZ = Math.max((int) (gridZ - 0.5f), 0);

		int endX = Math.min((int) (gridX + 0.5f), gridSize - 1);
		int endY = Math.min((int) (gridY + 0.5f), gridSize - 1);
		int endZ = Math.min((int) (gridZ + 0.5f), gridSize - 1);

		for (int px = startX; px <= endX; px++) {
			for (int py = startY; py <= endY; py++) {
				for (int pz = startZ; pz <= endZ; pz++) {
					int baseIndex = px + py * gridSize + pz * gridSize * gridSize;
					int count = gridParticleCounts[baseIndex];
					baseIndex *= gridDepth;
					for (int i = 0; i < count; i++) {
						Particle pi = grid[baseIndex + i];

						float dx = p.position.x - pi.position.x;
						float dy = p.position.y - pi.position.y;
						float dz = p.position.z - pi.position.z;
						float r2 = dx * dx + dy * dy + dz * dz;
						if (r2 < h2) {
							p.density += W(r2);
						}
					}
				}
			}
		}

		p.density *= settings.particleMass;
	}

	private Vec3 gradW = Vec3.create();

	private void getForces(Particle pi) {
		pi.fExternal.set(0f, 0f, 0f);
		pi.fPressure.set(0f, 0f, 0f);
		pi.fViscosity.set(0f, 0f, 0f);

		float gridX = pi.position.x * invBoundarySize * gridSize;
		float gridY = pi.position.y * invBoundarySize * gridSize;
		float gridZ = pi.position.z * invBoundarySize * gridSize;

		int startX = Math.max((int) (gridX - 0.5f), 0);
		int startY = Math.max((int) (gridY - 0.5f), 0);
		int startZ = Math.max((int) (gridZ - 0.5f), 0);

		int endX = Math.min((int) (gridX + 0.5f), gridSize - 1);
		int endY = Math.min((int) (gridY + 0.5f), gridSize - 1);
		int endZ = Math.min((int) (gridZ + 0.5f), gridSize - 1);

		for (int px = startX; px <= endX; px++) {
			for (int py = startY; py <= endY; py++) {
				for (int pz = startZ; pz <= endZ; pz++) {
					int baseIndex = px + py * gridSize + pz * gridSize * gridSize;
					int count = gridParticleCounts[baseIndex];
					baseIndex *= gridDepth;
					for (int j = 0; j < count; j++) {
						Particle pj = grid[baseIndex + j];
						if (pi == pj) {
							continue;
						}

						float dx = pi.position.x - pj.position.x;
						float dy = pi.position.y - pj.position.y;
						float dz = pi.position.z - pj.position.z;
						float r2 = dx * dx + dy * dy + dz * dz;

						if (r2 < h2) {
							float r = Real.sqrt(r2);

							// viscosity
							float laplace = laplaceW(r);

							float dvx = pj.velocity.x - pi.velocity.x;
							float dvy = pj.velocity.y - pi.velocity.y;
							float dvz = pj.velocity.z - pi.velocity.z;

							float fViscosity = laplace / pj.density;

							pi.fViscosity.x += dvx * fViscosity;
							pi.fViscosity.y += dvy * fViscosity;
							pi.fViscosity.z += dvz * fViscosity;

							// pressure
							gradW(dx, dy, dz, r, gradW);
							float fPressure = (pi.pressure + pj.pressure) / pj.density;
							pi.fPressure.x -= gradW.x * fPressure;
							pi.fPressure.y -= gradW.y * fPressure;
							pi.fPressure.z -= gradW.z * fPressure;
						}
					}
				}
			}
		}
		pi.fViscosity.mul(settings.viscosity * settings.particleMass);
		pi.fPressure.mul(settings.particleMass * 0.5f);

		// gravity
		pi.fExternal.y -= settings.gravity * pi.density;
	}

	public void update(float timeStep, int subSteps) {
		int N = particles.size();
		float dt = timeStep / subSteps;

		for (int t = 0; t < subSteps; t++) {
			for (int i = 0; i < gridParticleCounts.length; i++) {
				gridParticleCounts[i] = 0;
			}

			for (int i = 0; i < N; i++) {
				Particle p = particles.get(i);
				int gridX = Math.min((int) (p.position.x * invBoundarySize * gridSize), gridSize - 1);
				int gridY = Math.min((int) (p.position.y * invBoundarySize * gridSize), gridSize - 1);
				int gridZ = Math.min((int) (p.position.z * invBoundarySize * gridSize), gridSize - 1);
				int gridBaseIndex = gridX + gridY * gridSize + gridZ * gridSize * gridSize;
				int offset = gridParticleCounts[gridBaseIndex];
				if (offset == gridDepth) {
					continue;
				}
				gridParticleCounts[gridBaseIndex]++;
				int gridIndex = gridBaseIndex * gridDepth + offset;

				grid[gridIndex] = p;
			}

			for (int i = 0; i < N; i++) {
				Particle p = particles.get(i);
				getDensity(p);
				p.pressure = settings.gassConstant * (p.density - settings.restingDensity);
			}

			for (int i = 0; i < N; i++) {
				getForces(particles.get(i));
			}

			for (int i = 0; i < N; i++) {
				Particle p = particles.get(i);

				p.velocity.add(p.fExternal, dt / p.density);
				p.velocity.add(p.fPressure, dt / p.density);
				p.velocity.add(p.fViscosity, dt / p.density);
				// limit velocity
				float speed = p.velocity.length();
				if (speed > settings.maxVelocity) {
					p.velocity.mul(settings.maxVelocity / speed);
				}

				p.position.add(p.velocity, dt);

				if (p.position.x < 0f) {
					p.position.x = 0f;
					p.velocity.x *= -settings.wallElasticity;
				}

				if (p.position.x > settings.boundarySize) {
					p.position.x = settings.boundarySize;
					p.velocity.x *= -settings.wallElasticity;
				}

				if (p.position.y < 0f) {
					p.position.y = 0f;
					p.velocity.y *= -settings.wallElasticity;
				}

				if (p.position.y > settings.boundarySize) {
					p.position.y = settings.boundarySize;
					p.velocity.y *= -settings.wallElasticity;
				}

				if (p.position.z < 0f) {
					p.position.z = 0f;
					p.velocity.z *= -settings.wallElasticity;
				}

				if (p.position.z > settings.boundarySize) {
					p.position.z = settings.boundarySize;
					p.velocity.z *= -settings.wallElasticity;
				}
			}
		}
	}
	
	public void addParticle(SPHParticle particle) {
		Particle p = new Particle();
		p.position = particle.position;
		p.velocity = particle.velocity;
		particles.add(p);
	}
}
