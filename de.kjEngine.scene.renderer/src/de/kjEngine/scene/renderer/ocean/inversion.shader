
uniforms {
	float pingpong;
}

computeShader {
	input {
		localSizeX = 8;
		localSizeY = 8;
		localSizeZ = 1;
	}
	
	void main() {
		ivec2 x = ivec2(globalInvocationIndex);
		ivec2 size = imageSize(inversion.displacement);
	
		float h;
		if (uniforms.pingpong < 0.5) {
			h = texelFetch(inversion.pingpong0, x, 0).r;
		} else {
			h = texelFetch(inversion.pingpong1, x, 0).r;
		}
		
		imageStore(inversion.displacement, x, vec4(h / float(size.x * size.y), 0.0, 0.0, 1.0));
	}
}