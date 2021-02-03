
include math;

set data {
	texture2D depth;
}

fragmentShader {
	input {
		vec2 coord;
		
		cullMode = NONE;
	}
	
	output {
		RGBA16F vec4 result;
		depth = false;
		
		blend = true;
		sourceFactor = SOURCE_ALPHA;
		destinationFactor = ONE_MINUS_SOURCE_ALPHA;
	}
	
	void main() {
		vec3 V = normalize(mat3(camera.transforms.invView) * (camera.transforms.invProjection * vec4(input.coord * 2.0 - 1.0, 1.0, 1.0)).xyz);
		vec2 intersections = intersectSphere(camera.transforms.position, V, atmosphere.settings.position.xyz, atmosphere.settings.radius_density.y);
		if (intersections.x < 0.0 && intersections.y < 0.0) {
			discard;
		}
		float depth = texture(data.depth, input.coord).r;
		float near = max(intersections.x, 0.0);
		float distance = max(intersections.y, 0.0) - near;
		if (depth < 0.999) {
			distance = min(distance, length(camera.transforms.position - textureSpaceToWorldSpace(vec3(input.coord, depth))) - near);
		}
		output.result = vec4(max(calcLight(camera.transforms.position + V * near, V, distance), vec4(0.0)));
	}
}

const float SCATTER_R = 0.10662224;
const float SCATTER_G = 0.32444156;
const float SCATTER_B = 1.0;

float sampleDensity(vec3 P) {
	float heightAboveInner = length(P - atmosphere.settings.position.xyz) - atmosphere.settings.radius_density.x;
	float height01 = heightAboveInner / (atmosphere.settings.radius_density.y - atmosphere.settings.radius_density.x);
	return exp(-height01 * atmosphere.settings.radius_density.w) * (1.0 - height01) * atmosphere.settings.radius_density.z;
}

vec2 intersectSphere(vec3 rayOrigin, vec3 rayDir, vec3 sphereCenter, float sphereRadius) {
	vec3 v = rayOrigin - sphereCenter;
	float B = 2.0 * dot(rayDir, v);
	float C = dot(v, v) - sphereRadius * sphereRadius;
	float B2 = B * B;
	float f = B2 - 4.0 * C;
	
	if (f < 0.0) {
		return vec2(-1.0);
	}
	
	float sqrtF = sqrt(f);
	return 0.5 * (-B + vec2(-sqrtF, sqrtF));
}

float phase(float cosA) {
	return 0.75 + 0.75 * cosA * cosA;
}

vec4 calcLight(vec3 O, vec3 V, float distance) {
	vec3 inScatteredLight = vec3(0.0);

	int sampleCount = 20;
	float stepSize = distance / float(sampleCount - 1);
	
	float viewRayOpticalDepth = 0.0;
	
	for (int lightIndex = 0; lightIndex < lights.lights.directionalLightCount; lightIndex++) {
		DirectionalLight light = lights.lights.directionalLights[lightIndex];
		vec3 L = -light.direction;
		
		vec3 currentInScatteredLight = vec3(0.0);
		for (int i = 0; i < sampleCount; i++) {
			float t = float(i) * stepSize;
			vec3 P = O + V * t;
			
			float heightAboveInner = length(P - atmosphere.settings.position.xyz) - atmosphere.settings.radius_density.x;
			float height01 = heightAboveInner / (atmosphere.settings.radius_density.y - atmosphere.settings.radius_density.x);
			float x = acos(dot(L, normalize(P - atmosphere.settings.position.xyz))) / PI;
			float sunRayOpticalDepth = texture(lut.opticalDepth, vec2(x, height01)).x;
			
			float density = sampleDensity(P);
			viewRayOpticalDepth += density * stepSize;
			vec3 transmittance = exp(-(sunRayOpticalDepth + viewRayOpticalDepth) * vec3(SCATTER_R, SCATTER_G, SCATTER_B));
			currentInScatteredLight += transmittance * density;
		}
		inScatteredLight += currentInScatteredLight * light.color * phase(dot(L, V));
	}
	
	inScatteredLight *= vec3(SCATTER_R, SCATTER_G, SCATTER_B) * stepSize;
	
	float transmittance = exp(-viewRayOpticalDepth);
	return vec4(inScatteredLight / max(1.0 - transmittance, 0.001), 1.0 - transmittance);
}
