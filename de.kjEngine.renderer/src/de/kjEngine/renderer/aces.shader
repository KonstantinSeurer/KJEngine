
set textures {
	texture2D texture;
}

fragmentShader {
	input {
		vec2 coord;
	}
	
	output {
		RGB8 vec3 color;
	}
	
	void main() {
		float seed = input.coord.x * 47.294 + input.coord.y * 75.38;
		
		const vec3 c = texture(textures.texture, input.coord).rgb;
		const vec3 c2 = c * c;
		output.color = dither_8(clamp((2.51 * c2 + 0.03 * c) / (2.43 * c2 + 0.59 * c + 0.14), 0.0, 1.0), seed);
	}
}

float rand(input output float seed) {
	seed += 0.013781;
	return fract(sin(seed * 12.9898) * 43758.5453);
}

float dither_8(float f, input output float seed) {
	f *= 255.0;
	float fraction = fract(f);
	if (rand(seed) < fraction) {
		return floor(f + 1.0) / 255.0;
	}
	return floor(f) / 255.0;
}

vec2 dither_8(vec2 v, input output float seed) {
	return vec2(dither_8(v.x, seed), dither_8(v.y, seed));
}

vec3 dither_8(vec3 v, input output float seed) {
	return vec3(dither_8(v.x, seed), dither_8(v.y, seed), dither_8(v.z, seed));
}

vec4 dither_8(vec4 v, input output float seed) {
	return vec4(dither_8(v.x, seed), dither_8(v.y, seed), dither_8(v.z, seed), dither_8(v.w, seed));
}
