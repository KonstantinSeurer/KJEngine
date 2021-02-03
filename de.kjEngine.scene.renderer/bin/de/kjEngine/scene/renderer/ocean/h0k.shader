
computeShader {
	input {
		localSizeX = 8;
		localSizeY = 8;
		localSizeZ = 1;
	}
	
	void main() {
		vec2 size = vec2(imageSize(h0k.positive));
		vec2 coord = vec2(globalInvocationIndex) / size;
		vec2 x = vec2(globalInvocationIndex.xy);
		
		vec2 k = 2.0 * PI * x / h0k.settings.L;

		vec4 gauss_random = gaussRND(coord);
	
		imageStore(h0k.positive, ivec2(globalInvocationIndex), vec4(gauss_random.xy * phillips(k), 0.0, 1.0));
		imageStore(h0k.negative, ivec2(globalInvocationIndex), vec4(gauss_random.zw * phillips(-k), 0.0, 1.0));
	}
}

const float PI = 3.14159;

vec4 gaussRND(vec2 coord) {	
	float noise00 = clamp(texture(h0k.noise0, coord).r, 0.001, 1.0);
	float noise01 = clamp(texture(h0k.noise1, coord).r, 0.001, 1.0);
	float noise02 = clamp(texture(h0k.noise2, coord).r, 0.001, 1.0);
	float noise03 = clamp(texture(h0k.noise3, coord).r, 0.001, 1.0);
	
	float u0 = 2.0 * PI * noise00;
	float v0 = sqrt(-2.0 * log(noise01));
	float u1 = 2.0 * PI * noise02;
	float v1 = sqrt(-2.0 * log(noise03));
	
	vec4 rnd = vec4(v0 * cos(u0), v0 * sin(u0), v1 * cos(u1), v1 * sin(u1));
	
	return rnd;
}

const float G = 9.81;

float phillips(vec2 k) {
	vec2 wind = h0k.settings.w;
	
	float L = dot(wind, wind) * G;
	float magSq = max(dot(k, k), 0.00001);
	return clamp(sqrt(h0k.settings.A / (magSq * magSq) * pow(dot(normalize(k), normalize(wind)), 4.0) * exp(-1.0 / (magSq * L * L)) * 
						exp(-magSq * pow(L / 2000.0, 2.0))) / sqrt(2.0), 0.0, 100000.0);
}
