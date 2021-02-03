#source vertex

layout (location = 0) in vec3 in_pos;

layout (location = 0) out vec3 pos;
layout (location = 1) out vec3 bb_center;
layout (location = 2) out vec3 bb_radius;

layout (binding = 0, set = 1) uniform CAMERA {
	mat4 vMat;
	mat4 pMat;
	mat4 vpMat;
	mat4 inv_vMat;
	mat4 inv_pMat;
	mat4 inv_vpMat;
	vec3 pos;
} camera;

layout (location = 0) uniform mat4 transform;

void main(void) {
	vec4 worldPos = transform * vec4(in_pos, 1.0);
	bb_center = (transform * vec4(0.0, 0.0, 0.0, 1.0)).xyz;
	bb_radius = abs((transform * vec4(1.0, 1.0, 1.0, 1.0)).xyz - bb_center);
	gl_Position = camera.vpMat * worldPos;
	pos = worldPos.xyz;
}

#source fragment

#define PI 3.14159265358979323846

layout (location = 0) in vec3 pos;
layout (location = 1) in vec3 bb_center;
layout (location = 2) in vec3 bb_radius;

layout (location = 0) out vec4 color;

layout (binding = 0, set = 0) uniform sampler2D color_tex;
layout (binding = 1, set = 0) uniform sampler2D depth_tex;

layout (binding = 0, set = 1) uniform CAMERA {
	mat4 vMat;
	mat4 pMat;
	mat4 vpMat;
	mat4 inv_vMat;
	mat4 inv_pMat;
	mat4 inv_vpMat;
	vec3 pos;
} camera;

layout (binding = 1, set = 1) uniform SUN {
	vec3 dir;
	vec3 color;
	bool isCastingShadows;
	int shadowMapResolution;
	mat4 shadowMat;
} sun;

layout (binding = 2, set = 1) uniform sampler2D sunShadowMap;

layout (binding = 0, set = 2) uniform sampler3D cloud_tex;
layout (binding = 1, set = 2) uniform sampler3D scattering_tex;

layout (binding = 2, set = 2) uniform DATA {
	vec3 scattering;
} data;

struct ray {
	vec3 o;
	vec3 d;
};

struct AABB {
	vec3 center;
	vec3 radius;
};

struct cloud {
	AABB bb;
	vec3 scattering;
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

float rand(float f) {
	return fract(sin(f * 12.9898) * 43758.5453);
}

float hg(float a, float g) {
	float g2 = g * g;
	return (1.0 - g2) / (4.0 * 3.1415 * pow(1.0 + g2 - 2.0 * g * a, 1.5));
}

vec4 phaseParams = vec4(0.7, 0.3, 0.3, 0.7);

float phase(float a) {
	float blend = 0.5;
	float hgBlend = hg(a, phaseParams.x) * (1.0 - blend) + hg(a, -phaseParams.y) * blend;
	return phaseParams.z + hgBlend * phaseParams.w;
}

vec4 calcScattering(ray r, float dist, cloud c) {
	vec3 local_pos = r.o - c.bb.center + c.bb.radius;
	
	float sun_scattering = 0.0;
	float sky_scattering = 0.0;
	
	float d = 0.2;
	float step_size = d;
	
	vec3 inv_bounds = 0.5 / c.bb.radius;
	
	float absorbtion = 1.0;
	
	float seed = r.d.x + r.d.y * 83.28 + r.d.z * 8.248;
	
	for (float t = 0.0; t < dist; t += step_size) {
		vec3 sample_pos = (local_pos + r.d * (t + step_size * rand((t + seed) * 239.248))) * inv_bounds;
		
		float density = texture(cloud_tex, sample_pos).r;
		
		if (density > 0.05) {
			absorbtion -= absorbtion * step_size * density;
			absorbtion = max(absorbtion, 0.0);
			
			float toSunScattering = texture(scattering_tex, sample_pos).r;
			float f = density * step_size * absorbtion;
			sun_scattering += f * toSunScattering;
			sky_scattering += f * mix(toSunScattering, absorbtion, 0.5);
			
			step_size = d / absorbtion;
		}
	}
	float a = 1.0 - absorbtion;
	vec3 radience = c.scattering * (sun_scattering * sun.color * phase(dot(sun.dir, r.d)) + sky_scattering * vec3(0.5, 0.7, 1.0));
	return vec4(radience / a, a);
}

vec3 calcWorldPosition(vec2 tc, float depth) {
	vec4 screen_pos = vec4(tc.x * 2.0 - 1.0, tc.y * 2.0 - 1.0, depth, 1.0);
	vec4 result = camera.inv_vpMat * screen_pos;
	return result.xyz / result.w;
}

vec3 calcScreenSpacePosition(vec3 pos) {
	vec4 result = camera.vpMat * vec4(pos, 1.0);
	return result.xyz / result.w;
}

void main(void) {
	cloud this_cloud = cloud(AABB(bb_center, bb_radius), data.scattering);

	vec3 screenSpace = calcScreenSpacePosition(pos);
	screenSpace.xy *= 0.5;
	screenSpace.xy += 0.5;
	float depth = texture(depth_tex, screenSpace.xy).r;
	float dist = length(camera.pos - calcWorldPosition(screenSpace.xy, depth));
	
	ray r = ray(camera.pos, normalize(pos - camera.pos));
	
	vec2 t = intersectAABB(r, this_cloud.bb);
	
	float near = max(t.x, 0.0);
	
	if (near > dist) {
		color = vec4(0.0, 0.0, 0.0, 0.0);
	} else {
		float far = min(t.y, dist);
		color = calcScattering(ray(r.o + r.d * near, r.d), far - near, this_cloud);
	}
}
