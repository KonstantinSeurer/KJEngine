
include math;

computeShader {
	input {
		localSizeX = 8;
		localSizeY = 8;
		localSizeZ = 1;
	}
	
	void main() {
		vec2 coord = vec2(globalInvocationIndex.xy) / imageSize(target.opticalDepth);
		
		float inner = atmosphere.settings.radius_density.x;
		float outer = atmosphere.settings.radius_density.y;
		
		vec3 P = vec3(0.0, coord.y * (outer - inner) + inner, 0.0);
		float angle = coord.x * PI;
		vec3 D = vec3(sin(angle), cos(angle), 0.0);
		
		vec2 intersections = intersectSphere(P, D, vec3(0.0), outer);
		float distance = max(max(intersections.x, intersections.y), 0.0);
		
		imageStore(target.opticalDepth, ivec2(globalInvocationIndex.xy), vec4(calcOpticalDepth(P, D, distance), 0.0, 0.0, 1.0));
	}
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

float sampleDensity(vec3 P) {
	float heightAboveInner = length(P) - atmosphere.settings.radius_density.x;
	float height01 = heightAboveInner / (atmosphere.settings.radius_density.y - atmosphere.settings.radius_density.x);
	return exp(-height01 * atmosphere.settings.radius_density.w) * (1.0 - height01) * atmosphere.settings.radius_density.z;
}

float calcOpticalDepth(vec3 P, vec3 D, float rayLength) {
	int sampleCount = 5;
	float stepSize = rayLength / float(sampleCount - 1);
	float opticalDepth = 0.0;
	for (int i = 0; i < sampleCount; i++) {
		opticalDepth += sampleDensity(P + D * stepSize * float(i));
	}
	return opticalDepth * stepSize;
}
