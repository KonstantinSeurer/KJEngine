
vertexShader {
	input {
		vec3 pos;
		vec2 tc;
		vec3 normal;
		
		topology = TRIANGLE_LIST;
	}
	
	output {
		float density;
		vec3 vel;
		float lighting;
	}
	
	void main() {
		Particle p = data.particles.list[instanceIndex];
		float scale = data.settings.particleRadius;
		vertexPosition = camera.camera.vpMat * vec4(p.pos + input.pos * scale, 1.0);
		output.density = p.density;
		output.vel = p.vel;
		output.lighting = max(input.normal.y, 0.0) * 0.5 + 0.5;
	}
}

fragmentShader {
	input {
		float density;
		vec3 vel;
		float lighting;
	
		cullMode = BACK;
		frontFace = COUNTER_CLOCKWISE;
		
		depthTest = true;
		depthWrite = true;
		depthClamp = false;
	}
	
	output {
		vec4 result;
		depth = true;
		blend = false;
	}
	
	void main() {
		output.result = vec4((input.vel * 0.5 + 0.5) * input.lighting, 1.0);
	}
}