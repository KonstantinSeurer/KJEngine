
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
		Particle p = data.particles.list[index];
		int size = data.particles.gridSize;
		float gridFactor = float(size) * data.settings.invBoundarySize;
		int gridX = min(int(p.pos.x * gridFactor), size - 1);
		int gridY = min(int(p.pos.y * gridFactor), size - 1);
		int gridZ = min(int(p.pos.z * gridFactor), size - 1);
		int gridBaseIndex = gridX + gridY * size + gridZ * size * size;
		int offset = atomicAdd(data.particles.gridParticleCount[gridBaseIndex], 1);
		if (offset >= gridDepth) {
			atomicAdd(data.particles.gridParticleCount[gridBaseIndex], -1);
			return;
		}
		data.particles.grid[gridBaseIndex * gridDepth + offset] = index;
	}
}