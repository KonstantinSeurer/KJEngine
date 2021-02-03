
set data {
	texture2D texture;
	texture2D depth;
	texture2D normal;
	
	uniformBuffer settings {
		float amount;
		float radius;
		float sampleCount;
	}
}

fragmentShader {
	input {
		vec2 coord;
	}
	
	output {
		vec3 color;
	}
	
	void main() {
		float depth = texture(data.depth, input.coord).r;
		
		output.color = texture(data.texture, input.coord).rgb;
		
		if (depth > 0.999) {
			return;
		}
		
		vec3 worldPos = textureSpaceToWorldSpace(vec3(input.coord, depth));
		vec3 normal = texture(data.normal, input.coord).rgb * 2.0 - 1.0;
		
		float occ = 0.0;
		
		for (int i = 0; i < data.settings.sampleCount; i++) {
			vec3 seed = vec3(float(i), float(i) + 2.0, float(i) * 3.0 + 1.0) + worldPos * 3.45;
			vec3 off = (rand3(seed) * 2.0 - 1.0) * data.settings.radius;
			if (dot(normal, off) < 0.0) {
				off *= -1.0;
			}
			vec3 samplePos = worldPos + off;
			vec3 screenSpaceSamplePos = worldSpaceToClipSpace(samplePos);
			if (screenSpaceSamplePos.x < -1.0 || screenSpaceSamplePos.x > 1.0 || screenSpaceSamplePos.y < -1.0 || screenSpaceSamplePos.y > 1.0) {
				continue;
			}
			vec2 textureSpaceSamplePos = screenSpaceSamplePos.xy * 0.5 + 0.5;
			float sampledDepth = texture(data.depth, textureSpaceSamplePos).r;
			if (sampledDepth < screenSpaceSamplePos.z) {
				vec3 sampledPos = textureSpaceToWorldSpace(vec3(textureSpaceSamplePos, sampledDepth));
				vec3 diff = sampledPos - samplePos;
				if (dot(diff, diff) < 1.0) {
					occ += 1.0;
				}
			}
		}
		
		output.color *= max(1.0 - occ / data.settings.sampleCount * data.settings.amount, 0.0);
	}
}

// from https://github.com/MaxBittker/glsl-voronoi-noise/blob/master/3d.glsl
vec3 rand3(vec3 p) {
	return fract(sin(vec3(dot(p, vec3(1.0, 57.0, 113.0)), dot(p, vec3(57.0, 113.0, 1.0)), dot(p, vec3(113.0, 1.0, 57.0)))) * 43758.5453);
}
