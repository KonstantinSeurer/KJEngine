
computeShader {
	input {
		localSizeX = 8;
		localSizeY = 8;
		localSizeZ = 1;
	}
	
	void main() {
		vec2 size = vec2(imageSize(htk.dx));
		vec2 coord = vec2(globalInvocationIndex) / size;
		vec2 x = vec2(globalInvocationIndex);
		
		vec2 k = 2.0 * PI * x / htk.settings.L;
		float mag = max(length(k), 0.0001);
		float w = sqrt(mag * G);
		
		vec2 h0k = texture(htk.h0k, coord).xy;
		vec2 h0mkConj = complexConj(texture(htk.h0mk, coord).xy);
		
		float t = htk.settings.t * 5.0;
		
		float c = cos(w * t);
		float s = sin(w * t);
		
		vec2 exp_iwt = vec2(c, s);
		vec2 exp_iwt_inv = vec2(c, -s);
		
		vec2 dy = complexMul(h0k, exp_iwt) + complexMul(h0mkConj, exp_iwt_inv);
		
		vec2 dx = complexMul(vec2(0.0, -k.x / mag), dy);
		
		vec2 dz = complexMul(vec2(0.0, -k.y / mag), dy);
		
		imageStore(htk.dx, ivec2(globalInvocationIndex), vec4(dx, 0.0, 1.0));
		imageStore(htk.dy, ivec2(globalInvocationIndex), vec4(dy, 0.0, 1.0));
		imageStore(htk.dz, ivec2(globalInvocationIndex), vec4(dz, 0.0, 1.0));
	}
}

const float PI = 3.14159;
const float G = 9.81;

vec2 complexMul(vec2 a, vec2 b) {
	return vec2(a.x * b.x - a.y * b.y, a.x * b.y + a.y * b.x);
}

vec2 complexConj(vec2 c) {
	return vec2(c.x, -c.y);
}
