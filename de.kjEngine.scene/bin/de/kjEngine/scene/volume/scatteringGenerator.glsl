layout (local_size_x = 8, local_size_y = 8, local_size_z = 8) in;

layout (binding = 0, std140) uniform DATA {
	vec3 invVolumeSize;
	vec3 scale;
	vec3 sunDir;
};

layout (binding = 1) uniform sampler3D volume;

layout (binding = 2, r8) uniform image3D scatteringTexture;

struct ray {
	vec3 o;
	vec3 d;
};

struct AABB {
	vec3 center;
	vec3 radius;
};

vec2 intersectAABB(ray r, AABB aabb) {
	vec3 tMin = (aabb.center - aabb.radius - r.o) / r.d;
	vec3 tMax = (aabb.center + aabb.radius - r.o) / r.d;
	vec3 t1 = min(tMin, tMax);
	vec3 t2 = max(tMin, tMax);
	float tNear = max(max(t1.x, t1.y), t1.z);
	float tFar = min(min(t2.x, t2.y), t2.z);
	return vec2(tNear, tFar);
}

void main() {
	ivec3 id = ivec3(gl_GlobalInvocationID.xyz);
	
	vec3 pos = id * invVolumeSize;
	
	float integral = 0.0;
	float d = 0.1;
	
	ray r;
	r.o = (pos * 2.0 - 1.0) * scale;
	r.d = -sunDir;
	
	vec2 intersections = intersectAABB(r, AABB(vec3(0.0), scale));
	
	float far = min(intersections.y, 2.0 * length(scale));
	
	vec3 invScale = 1.0 / scale;
	
	for (float t = 0.0; t < far; t += d) {
		vec3 p = r.o + r.d * t;
		integral += d * texture(volume, p * invScale * 0.5 + 0.5).r;
	}
	
	imageStore(scatteringTexture, id, vec4(exp(-integral * 5.0)));
}
