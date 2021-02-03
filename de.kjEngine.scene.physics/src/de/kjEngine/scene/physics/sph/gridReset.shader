
computeShader {
	input {
		localSizeX = 8;
		localSizeY = 8;
		localSizeZ = 8;
	}
	
	void main() {
		ivec3 id = ivec3(globalInvocationIndex);
		int size = data.particles.gridSize;
		if (id.x >= size || id.y >= size || id.z >= size) {
			return;
		}
		data.particles.gridParticleCount[id.x + id.y * size + id.z * size * size] = 0;
	}
}