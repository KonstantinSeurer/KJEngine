layout (local_size_x = 8, local_size_y = 8, local_size_z = 8) in;

layout (binding = 0, std140) uniform DATA {
	vec3 invVolumeSize;
	vec3 scale;
};

layout (binding = 1, r16f) uniform image3D volume;

struct sampleInfo {
	vec3 volumeScale;
	vec3 pos;
};

float getDensity(sampleInfo info);

void main() {
	ivec3 id = ivec3(gl_GlobalInvocationID.xyz);
	sampleInfo info;
	info.volumeScale = scale;
	info.pos = id * invVolumeSize;
	float density = clamp(getDensity(info), 0.0, 1.0);
	imageStore(volume, id, vec4(density));
}

// from https://github.com/MaxBittker/glsl-voronoi-noise/blob/master/3d.glsl
vec3 rand3(vec3 p) {
	return fract(sin(vec3(dot(p, vec3(1.0, 57.0, 113.0)), dot(p, vec3(57.0, 113.0, 1.0)), dot(p, vec3(113.0, 1.0, 57.0)))) * 43758.5453);
}

float veronoi(vec3 pos) {
	float minD = 10.0;
	vec3 centerCell = floor(pos);
	for (int x = -1; x <= 1; x++) {
		for (int y = -1; y <= 1; y++) {
			for (int z = -1; z <= 1; z++) {
				vec3 cell = centerCell + vec3(x, y, z);
				vec3 cellPos = cell + rand3(cell);
				vec3 toCellPos = cellPos - pos;
				minD = min(minD, dot(toCellPos, toCellPos));
			}
		}
	}
	if (minD > 1.0) {
		return 0.0;
	}
	return 1.0 - sqrt(minD);
}

vec2 veronoi2(vec3 pos) {
	return vec2(veronoi(pos), veronoi(pos + vec3(12.34, 3.2, -2.43)));
}

vec3 veronoi3(vec3 pos) {
	return vec3(veronoi(pos), veronoi(pos + vec3(12.34, 3.2, -2.43)), veronoi(pos + vec3(2.28, 9.29, -10.3)));
}

float map(float f, float srcMin, float srcMax, float dstMin, float dstMax) {
	return dstMin + (f - srcMin) / (srcMax - srcMin) * (dstMax - dstMin);
}

struct cumulonimbus {
	vec3 off;
	vec3 bottomRadius;
	float topHeight;
	float topExpansionExp;
	float topExpansionScale;
	float topBaseRadius;
};

struct cumulus {
	vec3 off;
	vec3 radius;
};

float getDensity(cumulus c, sampleInfo info) {
	vec3 p = (info.pos * 2.0 - 1.0) * info.volumeScale - c.off;
	float noise = (0.5 - veronoi(p)) + (0.5 - veronoi(p * 3.0)) * 0.3;
	return pow(max(1.0 - length(p * (1.0 + noise * 0.4) / c.radius), 0.0), 0.5);
}

float getDensity(cumulonimbus c, sampleInfo info) {
	vec3 p = (info.pos * 2.0 - 1.0) * info.volumeScale - c.off;
	
	float noise = (0.5 - veronoi(p)) + (0.5 - veronoi(p * 3.0)) * 0.3;
	
	float bottom = pow(max(1.0 - length(p * (1.0 + noise * 0.4) / c.bottomRadius), 0.0), 0.5);
	
	float top = 0.0;
	if (p.y > 0.0) {
		vec3 samplePos = p;
		float top0 = length(samplePos.xz) * (1.0 + noise * 0.4) - c.topBaseRadius - c.topExpansionScale * pow(samplePos.y / c.topHeight, c.topExpansionExp);
		top = pow(1.0 - top0, 0.5) * clamp(map(p.y + noise * 0.1, c.topHeight * 0.9, c.topHeight * 1.1, 1.0, 0.0), 0.0, 1.0);
	}
	
	return max(bottom, top);
}
