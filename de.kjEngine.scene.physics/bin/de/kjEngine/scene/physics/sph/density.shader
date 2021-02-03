
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
		pi.density = 0.0;
		
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
		
		float h2 = data.settings.particleRadius * data.settings.particleRadius;
		
		float wFactor = 315.0 / 64.0 / 3.14159265358 / pow(data.settings.particleRadius, 9.0);
		
		for (int x = startX; x <= endX; x++) {
			for (int y = startY; y <= endY; y++) {
				for (int z = startZ; z <= endZ; z++) {
					int baseIndex = x + y * size + z * size * size;
					int count = data.particles.gridParticleCount[baseIndex];
					baseIndex *= gridDepth;
					for (int j = 0; j < count; j++) {
						Particle pj = data.particles.list[data.particles.grid[baseIndex + j]];
						vec3 diff = pi.pos - pj.pos;
						float r2 = dot(diff, diff);
						if (r2 < h2) {
							pi.density += wFactor * (h2 - r2) * (h2 - r2) * (h2 - r2);
						}
					}
				}
			}
		}
		
		data.particles.list[index].density = pi.density * data.settings.particleMass;
	}
}
