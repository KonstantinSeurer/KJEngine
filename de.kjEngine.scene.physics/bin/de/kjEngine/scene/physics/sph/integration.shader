
uniforms {
	float dt;
}

computeShader {
	input {
		localSizeX = 8;
		localSizeY = 8;
		localSizeZ = 8;
	}
	
	void main() {
		int index = int(globalInvocationIndex.x + globalInvocationIndex.y * globalInvocationCount.x + globalInvocationIndex.z * globalInvocationCount.x * globalInvocationCount.y);
		if (index >= data.particles.count) {
			return;
		}
		Particle pi = data.particles.list[index];
		
		if (pi.density < 0.0001) {
			return;
		}
		
		int size = data.particles.gridSize;
		float gridFactor = float(size) * data.settings.invBoundarySize;
		float gridX = pi.pos.x * gridFactor;
		float gridY = pi.pos.y * gridFactor;
		float gridZ = pi.pos.z * gridFactor;
		
		int startX = max(int(gridX - 0.5), 0);
		int startY = max(int(gridY - 0.5), 0);
		int startZ = max(int(gridZ - 0.5), 0);
		
		int endX = min(int(gridX + 0.5), size - 1);
		int endY = min(int(gridY + 0.5), size - 1);
		int endZ = min(int(gridZ + 0.5), size - 1);
		
		float h = data.settings.particleRadius;
		
		float gradWFactor = -45.0 / 3.14159265358 / pow(h, 6.0);
		float Cfactor = 32.0 / 3.14159265358 / pow(h, 9.0);
		float Csubtraction = pow(h, 6.0) / 64.0;
		float h2 = h * h;
		float pressureI = data.settings.gassConstant * (pi.density - data.settings.restingDensity);
		
		vec3 fPressure = vec3(0.0);
		vec3 fViscosity = vec3(0.0);
		vec3 fCohesian = vec3(0.0);
		
		for (int x = startX; x <= endX; x++) {
			for (int y = startY; y <= endY; y++) {
				for (int z = startZ; z <= endZ; z++) {
					int baseIndex = x + y * size + z * size * size;
					int count = data.particles.gridParticleCount[baseIndex];
					baseIndex *= gridDepth;
					for (int j = 0; j < count; j++) {
						int indexJ = data.particles.grid[baseIndex + j];
						if (index == indexJ) {
							continue;
						}
						Particle pj = data.particles.list[indexJ];
						vec3 diff = pi.pos - pj.pos;
						float r2 = dot(diff, diff);
						if (r2 < h2) {
							float r = sqrt(r2);
							
							vec3 normalizedDiff = diff / r;
							
							fViscosity -= (pj.vel - pi.vel) * gradWFactor * (data.settings.particleRadius - r) / pj.density;
							
							float pressureJ = data.settings.gassConstant * (pj.density - data.settings.restingDensity);
							float f = (pressureI + pressureJ) / pj.density;
							fPressure -= normalizedDiff * gradWFactor * (data.settings.particleRadius - r) * (data.settings.particleRadius - r) * f;
							
							float C = Cfactor;
							if (r < h * 0.5) {
								C *= 2.0 * pow(h - r, 3.0) * r2 * r - Csubtraction;
							} else {
								C *= pow(h - r, 3.0) * r2 * r;
							}
							fCohesian -= C * normalizedDiff;
						}
					}
				}
			}
		}
		
		float gamma = 10.0;
		fCohesian *= gamma * data.settings.particleMass * data.settings.particleMass;
		
		if (pi.pos.x < data.settings.particleRadius || pi.pos.x > data.settings.boundarySize - data.settings.particleRadius ||
			pi.pos.y < data.settings.particleRadius || pi.pos.y > data.settings.boundarySize - data.settings.particleRadius ||
			pi.pos.z < data.settings.particleRadius || pi.pos.z > data.settings.boundarySize - data.settings.particleRadius) {
			float minR = min(min(pi.pos.x, min(pi.pos.y, pi.pos.z)), data.settings.boundarySize - max(pi.pos.x, max(pi.pos.y, pi.pos.z)));
			fViscosity += pi.vel * gradWFactor * (data.settings.particleRadius - minR) / data.settings.restingDensity;
		}
		
		fViscosity *= data.settings.viscosity * data.settings.particleMass;
		fPressure *= data.settings.particleMass * 0.5;
		
		pi.vel += (fPressure + fViscosity + fCohesian) * uniforms.dt / pi.density;
		pi.vel.y -= data.settings.gravity * uniforms.dt;
		
		float speed = length(pi.vel);
		if (speed > data.settings.maxVelocity) {
			pi.vel *= data.settings.maxVelocity / speed;
		}
		
		pi.pos += pi.vel * uniforms.dt;
		
		if (pi.pos.x < 0.0) {
			pi.pos.x = 0.0;
			pi.vel.x *= -data.settings.wallElasticity;
		}
	
		if (pi.pos.x > data.settings.boundarySize) {
			pi.pos.x = data.settings.boundarySize;
			pi.vel.x *= -data.settings.wallElasticity;
		}
	
		if (pi.pos.y < 0.0) {
			pi.pos.y = 0.0;
			pi.vel.y *= -data.settings.wallElasticity;
		}
	
		if (pi.pos.y > data.settings.boundarySize) {
			pi.pos.y = data.settings.boundarySize;
			pi.vel.y *= -data.settings.wallElasticity;
		}
	
		if (pi.pos.z < 0.0) {
			pi.pos.z = 0.0;
			pi.vel.z *= -data.settings.wallElasticity;
		}
	
		if (pi.pos.z > data.settings.boundarySize) {
			pi.pos.z = data.settings.boundarySize;
			pi.vel.z *= -data.settings.wallElasticity;
		}
		
		data.particles.list[index].vel = pi.vel;
		data.particles.list[index].pos = pi.pos;
	}
}