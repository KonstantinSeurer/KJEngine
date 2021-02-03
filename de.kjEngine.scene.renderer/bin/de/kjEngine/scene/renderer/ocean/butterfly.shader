
uniforms {
	float stage;
	float pingpong;
	float direction;
}

computeShader {
	input {
		localSizeX = 8;
		localSizeY = 8;
		localSizeZ = 1;
	}
	
	void main() {
		if(uniforms.direction < 0.5) {
			horizontalButterflies();
		} else {
			verticalButterflies();
		}
	}
}

const float PI = 3.14159;

vec2 complexMul(vec2 a, vec2 b) {
	return vec2(a.x * b.x - a.y * b.y, a.x * b.y + a.y * b.x);
}

void horizontalButterflies() {
	ivec2 x = ivec2(globalInvocationIndex.xy);
	
	if(uniforms.pingpong < 0.5) {
		vec4 data = texelFetch(butterfly.twiddleIndices, ivec2(int(uniforms.stage), x.x), 0).rgba;
		vec2 p = imageLoad(butterfly.pingpong0, ivec2(data.z, x.y)).rg;
		vec2 q = imageLoad(butterfly.pingpong0, ivec2(data.w, x.y)).rg;
		vec2 w = data.xy;
		
		//Butterfly operation
		vec2 H = p + complexMul(w, q);
		
		imageStore(butterfly.pingpong1, x, vec4(H, 0.0, 1.0));
	} else {
		vec4 data = texelFetch(butterfly.twiddleIndices, ivec2(int(uniforms.stage), x.x), 0).rgba;
		vec2 p = imageLoad(butterfly.pingpong1, ivec2(data.z, x.y)).rg;
		vec2 q = imageLoad(butterfly.pingpong1, ivec2(data.w, x.y)).rg;
		vec2 w = data.xy;
		
		//Butterfly operation
		vec2 H = p + complexMul(w, q);
		
		imageStore(butterfly.pingpong0, x, vec4(H, 0.0, 1.0));
	}
}
	
void verticalButterflies() {
	ivec2 x = ivec2(gl_GlobalInvocationID.xy);
	
	if(uniforms.pingpong < 0.5) {
		vec4 data = texelFetch(butterfly.twiddleIndices, ivec2(int(uniforms.stage), x.y), 0).rgba;
		vec2 p = imageLoad(butterfly.pingpong0, ivec2(x.x, data.z)).rg;
		vec2 q = imageLoad(butterfly.pingpong0, ivec2(x.x, data.w)).rg;
		vec2 w = data.xy;
		
		//Butterfly operation
		vec2 H = p + complexMul(w, q);
		
		imageStore(butterfly.pingpong1, x, vec4(H, 0.0, 1.0));
	} else {
		vec4 data = texelFetch(butterfly.twiddleIndices, ivec2(int(uniforms.stage), x.y), 0).rgba;
		vec2 p = imageLoad(butterfly.pingpong1, ivec2(x.x, data.z)).rg;
		vec2 q = imageLoad(butterfly.pingpong1, ivec2(x.x, data.w)).rg;
		vec2 w = data.xy;
		
		//Butterfly operation
		vec2 H = p + complexMul(w, q);
		
		imageStore(butterfly.pingpong0, x, vec4(H, 0.0, 1.0));
	}
}
