#source vertex

layout (location = 0) in vec3 pos;
layout (location = 1) in vec2 texCoord;
layout (location = 2) in vec3 norm;
layout (location = 3) in vec3 jointIds;
layout (location = 4) in vec3 jointWeights;

layout (location = 0) out vec2 tc;
layout (location = 1) out vec3 normal;
layout (location = 2) out vec3 tangent;
layout (location = 3) out vec3 bitangent;
layout (location = 4) out vec3 wpos;

#ifdef OPENGL
layout (location = 1) uniform float off;
#endif
#ifdef VULKAN
layout (push_constant, std140) uniform DATA {
	float off;
};
#endif

layout (binding = 0, set = 0) uniform CAMERA {
	mat4 vMat;
	mat4 pMat;
	mat4 vpMat;
	mat4 inv_vMat;
	mat4 inv_pMat;
	mat4 inv_vpMat;
	vec3 pos;
} camera;

#define MAX_OBJECT_COUNT 100000

layout (std430, binding = 4, set = 0) buffer OBJECTS {
	mat4 mMats[MAX_OBJECT_COUNT];
} objects;

layout (std430, binding = 0, set = 1) buffer INDICES {
	float index[MAX_OBJECT_COUNT];
} indices;

void main(void) {
	mat4 mMat = objects.mMats[int(indices.index[int(off) + instanceIndex])];
	wpos = (mMat * vec4(pos, 1.0)).xyz;
	tc = texCoord;
	normal = mat3(mMat) * norm;
	vertexPosition = camera.vpMat * vec4(wpos, 1.0);
	vec3 UP = vec3(0.0, 1.0, 0.0);
	if (normal == UP) {
		tangent = vec3(1.0, 0.0, 0.0);
	} else {
		tangent = cross(normal, UP);
	}
	bitangent = cross(normal, tangent);
}

#source fragment

layout (location = 0) in vec2 tc;
layout (location = 1) in vec3 normal;
layout (location = 2) in vec3 tangent;
layout (location = 3) in vec3 bitangent;
layout (location = 4) in vec3 worldPos;

layout (location = 0) out vec3 result;

layout (binding = 0, set = 0) uniform CAMERA {
	mat4 vMat;
	mat4 pMat;
	mat4 vpMat;
	mat4 inv_vMat;
	mat4 inv_pMat;
	mat4 inv_vpMat;
	vec3 pos;
} camera;

layout (binding = 1, set = 0) uniform SUN {
	vec3 dir;
	vec3 color;
	bool isCastingShadows;
	int shadowMapResolution;
	mat4 shadowMat;
} sun;

layout (binding = 2, set = 0) uniform sampler2D sunShadowMap;

layout (binding = 0, set = 2) uniform sampler2D albedo_map;
layout (binding = 1, set = 2) uniform sampler2D roughness_map;
layout (binding = 2, set = 2) uniform sampler2D metalness_map;
layout (binding = 3, set = 2) uniform sampler2D subsurface_map;
layout (binding = 4, set = 2) uniform sampler2D normal_map;
layout (binding = 5, set = 2) uniform sampler2D disp_map;

#define PI 3.14159265358979323846

#define SHADOW_MAP_PCF_R 2
#define SHADOW_MAP_PCF_COUNT 25

bool hiddenFromLight(sampler2D map, vec4 coord) {
	if (coord.z < 0.0 || coord.z > 1.0) {
		return false;
	}
	return coord.z - 0.001 > texture(map, coord.xy).r;
}

float shadowFactor(vec3 wpos, mat4 mat, sampler2D map, int shadowMapSize, float d) {
	vec4 ssc = mat * vec4(wpos, 1.0);
	ssc *= 0.5;
	ssc += 0.5;
	if (ssc.x < 0.0 || ssc.x > 1.0 || ssc.y < 0.0 || ssc.y > 1.0) {
		return 1.0;
	}
	if (d < 30.0) {
		ivec2 sunSMSize = ivec2(shadowMapSize);
		vec2 sunSMTexelSize = 1.0 / vec2(sunSMSize.x, sunSMSize.y);
		float sum = 0.0;
		for (int yo = -SHADOW_MAP_PCF_R; yo <= SHADOW_MAP_PCF_R; yo++) {
			for (int xo = -SHADOW_MAP_PCF_R; xo <= SHADOW_MAP_PCF_R; xo++) {
				vec2 off = vec2(xo, yo) * sunSMTexelSize;
				if (!hiddenFromLight(map, vec4(ssc.xy + off, ssc.z, ssc.w))) {
					sum += 1.0;
				}
			}
		}
		return sum / float(SHADOW_MAP_PCF_COUNT);
	} else {
		if (hiddenFromLight(map, ssc)) {
			return 0.0;
		} else {
			return 1.0;
		}
	}
}

vec3 F(float cos_a, vec3 F0) {
	return F0 + (1.0 - F0) * pow(1.0 - cos_a, 5.0);
}

vec3 F(float cos_a, vec3 F0, float roughness) {
	return F0 + (1.0 - F0) * pow(1.0 - cos_a, 5.0) * (1.0 - roughness);
}

float D(vec3 N, vec3 H, float roughness) {
	float a = roughness * roughness;
    float a2     = a*a;
    float NdotH  = max(dot(N, H), 0.0);
    float NdotH2 = NdotH*NdotH;
	
    float nom    = a2;
    float denom  = (NdotH2 * (a2 - 1.0) + 1.0);
    denom        = PI * denom * denom;
	
    return nom / denom;
}

float GeometrySchlickGGX(float NdotV, float roughness) {
    float r = (roughness + 1.0);
    float k = (r*r) / 8.0;

    float num   = NdotV;
    float denom = NdotV * (1.0 - k) + k;
	
    return num / denom;
}

float G(vec3 N, vec3 V, vec3 L, float roughness) {
    float NdotV = max(dot(N, V), 0.0);
    float NdotL = max(dot(N, L), 0.0);
    float ggx2  = GeometrySchlickGGX(NdotV, roughness);
    float ggx1  = GeometrySchlickGGX(NdotL, roughness);
	
    return ggx1 * ggx2;
}

void main(void) {
	vec4 albedoSample = texture(albedo_map, tc);
	if (albedoSample.a < 0.5) {
		discard;
	} else {
		vec3 albedo = albedoSample.rgb;
		float roughness = texture(roughness_map, tc).r;
		float metalness = texture(metalness_map, tc).r;
		// vec3 subsurface = texture(subsurface_map, tc).rgb;
		vec3 normalMapSample = texture(normal_map, tc).rgb;
		vec3 N = normalize(normalMapSample.x * tangent + normalMapSample.y * bitangent + normalMapSample.z * normal);
		
		vec3 toCam = camera.pos - worldPos;
		float d = length(toCam);
		vec3 V = toCam / d;
	
		result = vec3(0.0);
	
		vec3 F0 = mix(vec3(0.04), albedo, metalness);
	
		float NdotV = max(dot(N, V), 0.0);
	
		{ // sun
			vec3 L = -sun.dir;
			vec3 H = normalize(V + L);
			float NdotL = max(dot(N, L), 0.0);
		
			vec3 radience = sun.color * NdotL * shadowFactor(worldPos, sun.shadowMat, sunShadowMap, sun.shadowMapResolution, d);
		
			vec3 ks = F(max(dot(H, V), 0.0), F0);
			vec3 f_specular = ks * D(N, H, roughness) * G(N, V, L, roughness) / max(4.0 * max(dot(N, V), 0.0) * NdotL, 0.0001);
		
			float kd = (1.0 - (ks.r + ks.g * ks.b) * 0.3333) * (1.0 - metalness);
			vec3 f_diffuse = albedo * kd / PI;
		
			result += radience * (f_diffuse + f_specular);
		}
	
		{ // global
			vec3 ks = F(NdotV, F0, roughness);
			float kd = (1.0 - (ks.r + ks.g * ks.b) * 0.3333) * (1.0 - metalness);
			
			vec3 irradience = vec3(0.5);
			vec3 reflection = vec3(0.5);
		
			result += albedo * kd * irradience + ks * reflection;
		}
	}
}
