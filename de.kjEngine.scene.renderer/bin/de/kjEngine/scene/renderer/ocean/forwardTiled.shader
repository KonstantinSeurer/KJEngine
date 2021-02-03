
set data {
	texture2D base;
	texture2D environment;
	texture2D depth;
	
	uniformBuffer settings {
		float dummy;
	}
}

vertexShader {
	input {
		topology = PATCH_LIST;
	}
	
	output {
		vec2 offset;
	}
	
	void main() {
		output.offset = vec2(vertexIndex % ocean.settings.tileCountX, vertexIndex / ocean.settings.tileCountX);
	}
}

tesselationControlShader {
	input {
		vec2 offset;
	
		patchSize = 1;
	}
	
	output {
		vec2 offset;
	}
	
	void main() {
		float inner = getTesselationLevel(length(camera.transforms.position - transformPoint(vec2(input.offset[0].x + 0.5, input.offset[0].y + 0.5))));
	
		outerTesselationLevel[0] = getTesselationLevel(length(camera.transforms.position - transformPoint(vec2(input.offset[0].x, input.offset[0].y + 0.5))));
		outerTesselationLevel[1] = getTesselationLevel(length(camera.transforms.position - transformPoint(vec2(input.offset[0].x + 0.5, input.offset[0].y))));
		outerTesselationLevel[2] = getTesselationLevel(length(camera.transforms.position - transformPoint(vec2(input.offset[0].x + 1.0, input.offset[0].y + 0.5))));
		outerTesselationLevel[3] = getTesselationLevel(length(camera.transforms.position - transformPoint(vec2(input.offset[0].x + 0.5, input.offset[0].y + 1.0))));
		innerTesselationLevel[0] = inner;
		innerTesselationLevel[1] = inner;
		
		output.offset[invocationIndex] = input.offset[invocationIndex];
	}
}

float getTesselationLevel(float distance) {
	return max(256.0 / (1.0 + distance), 1.0);
}

tesselationEvaluationShader {
	input {
		vec2 offset;
	
		topology = QUAD_LIST;
		spacing = EQUAL;
		windingOrder = CLOCKWISE;
	}
	
	output {
		vec3 pos;
		vec2 coord;
	}
	
	void main() {
		output.coord = (tesselationCoord.xy + input.offset[0]) / vec2(ocean.settings.tileCountX, ocean.settings.tileCountZ);
		output.pos = displacePoint(output.coord);
		vertexPosition = camera.transforms.viewProjection * vec4(output.pos, 1.0);
	}
}

fragmentShader {
	input {
		vec3 pos;
		vec2 coord;
		
		depthWrite = true;
		depthClamp = false;
		depthTest = true;
	}
	
	output {
		RGB16F vec3 color;
		
		depth = true;
	}
	
	void main() {
		vec2 heightMapPixelSize = 1.0 / vec2(textureSize(ocean.dx, 0).xy) / vec2(ocean.settings.textureCoordScaleX, ocean.settings.textureCoordScaleZ);
	
		vec3 center = displacePoint(input.coord);
		vec3 edge0 = displacePoint(input.coord + vec2(heightMapPixelSize.x, 0.0)) - center;
		vec3 edge1 = displacePoint(input.coord + vec2(0.0, heightMapPixelSize.y)) - center;
		vec3 N = normalize(cross(edge1, edge0));
		vec3 V = normalize(camera.transforms.position - input.pos);
		
		float filterFactor = pow(1.0 - max(V.y, 0.0), 8.0);
		
		vec3 waterColor = vec3(0.04, 0.06, 0.07);
		
		vec2 screenCoord = worldSpaceToTextureSpace(input.pos).xy;
		vec3 sampledPos = textureSpaceToWorldSpace(vec3(screenCoord, texture(data.depth, screenCoord).r));
		float distance = length(sampledPos - input.pos);
		
		float density = 5.0;
		
		float absorbtion = exp(-distance * density);
		float scattering = 1.0 - exp(-distance * density);
		
		vec3 result = vec3(0.0);
		
		int directionalLightCount = int(lights.lights.directionalLightCount);
		
		float NdotV = max(dot(N, V), 0.0);
		
		float roughness = 0.1;
			
		for (int i = 0; i < directionalLightCount; i++) { // directional lights
			DirectionalLight light = lights.lights.directionalLights[i];
		
			vec3 L = -light.direction;
			vec3 H = normalize(V + L);
			
			float NdotL = max(dot(N, L), 0.0);
			float NdotH = max(dot(N, H), 0.0);
			
			float shadowFactor = 1.0;
			if (light.shadowMapIndex != -1) {
				for (int j = 0; j < light.shadowMapCount; j++) {
					shadowFactor = min(shadowFactor, calcShadowFactorPcf(light.shadowMapIndex + j, input.pos, 1));
				}
			}
			
			float ks = fresnel(0.04, max(dot(H, V), 0.0));
			float f_specular = ks * D(NdotH, roughness) * G(NdotV, NdotL, roughness) / max(4.0 * NdotV * NdotL, 0.0001);
			
			float kd = 1.0 - ks;
			vec3 f_diffuse = waterColor * 5.0 * light.color * scattering / PI * max(L.y, 0.0);
			
			result += light.color * NdotL * shadowFactor * f_specular + f_diffuse;
		}
		
		float f = mix(fresnel(0.04, max(dot(N, V), 0.0)), 0.5, filterFactor);
		
		result += texture(data.base, screenCoord).rgb * absorbtion * (1.0 - f);
		
		{ // indirect lighting
			vec3 R = normalize(-reflect(V, mix(N, vec3(0.0, 1.0, 0.0), filterFactor)));
			vec3 averageEnvironmentLight = sampleEnvironmentMap(data.environment, vec3(0.0, 1.0, 0.0)) * 0.8 + sampleEnvironmentMap(data.environment, vec3(1.0, 0.0, 0.0)) * 0.2;
			vec3 diffuse = waterColor * averageEnvironmentLight / PI;
			vec3 specular = mix(sampleEnvironmentMap(data.environment, R), averageEnvironmentLight, filterFactor);
			vec3 Lbellow = diffuse * scattering;
			result += mix(Lbellow, specular, f);
		}
		
		output.color = result;
	}
}

const float PI = 3.14159265358979323;

vec3 transformPoint(vec2 p) {
	p /= vec2(ocean.settings.tileCountX, ocean.settings.tileCountZ);
	return (ocean.settings.transform * vec4(p.x, 0.0, p.y, 1.0)).xyz;
}

vec3 displacePoint(vec2 coord) {
	vec2 tc = coord * vec2(ocean.settings.textureCoordScaleX, ocean.settings.textureCoordScaleZ);

	float verticalDisplacementScale = 0.1;
	float horizontalDisplacementScale = 0.1;
	
	float dx = texture(ocean.dx, tc).r * horizontalDisplacementScale;
	float dy = texture(ocean.dy, tc).r * verticalDisplacementScale;
	float dz = texture(ocean.dz, tc).r * horizontalDisplacementScale;
	
	return (ocean.settings.transform * vec4(coord.x, 0.0, coord.y, 1.0)).xyz + vec3(-dx, dy, -dz);
}

float fresnel(float f0, float cosA) {
	return f0 + (1.0 - f0) * pow(1.0 - cosA, 5.0);
}

vec3 sampleEnvironmentMap(texture2D map, vec3 V) {
	vec2 uv = vec2(atan(-V.z, V.x), asin(V.y)) * invAtan + 0.5;
	return texture(map, uv).rgb;
}

const vec2 invAtan = vec2(0.1591, 0.3183);

float D(float NdotH, float roughness) {
	float a = roughness * roughness;
    float a2     = a*a;
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

float G(float NdotV, float NdotL, float roughness) {
    return GeometrySchlickGGX(NdotV, roughness) * GeometrySchlickGGX(NdotL, roughness);
}

float calcShadowFactorPcfImpl(texture2D shadowMap, mat4 shadowMatrix, vec3 worldPos, int radius) {
	vec4 projectedCoord = shadowMatrix * vec4(worldPos, 1.0);
	projectedCoord.xy / projectedCoord.w;
	projectedCoord.xy *= 0.5;
	projectedCoord.xy += 0.5;
	
	if (projectedCoord.x < 0.0 || projectedCoord.x > 1.0 || projectedCoord.y < 0.0 || projectedCoord.y > 1.0 || projectedCoord.z < 0.0 || projectedCoord.z > 1.0) {
		return 1.0;
	}
	
	int diameter = radius * 2 + 1;
	int area = diameter * diameter;
	
	vec2 invMapSize = 1.0 / vec2(textureSize(shadowMap, 0));
	
	float result = 0.0;
	
	for (int x = -radius; x <= radius; x++) {
		for (int y = -radius; y <= radius; y++) {
			vec2 sampleCoord = projectedCoord.xy + vec2(float(x), float(y)) * invMapSize;
			
			float depthSample = texture(shadowMap, sampleCoord).x;
	
			if (depthSample + 0.0005 > projectedCoord.z) {
				result += 1.0;
			}
		}
	}
	
	return result / float(area);
}

float calcShadowFactorPcf(int shadowMapIndex, vec3 worldPos, int radius) {	
	if (shadowMapIndex == 0) {
		return calcShadowFactorPcfImpl(lights.shadowMap0, lights.lights.shadowMapMatrices[0], worldPos, radius);
	} else if (shadowMapIndex == 1) {
		return calcShadowFactorPcfImpl(lights.shadowMap1, lights.lights.shadowMapMatrices[1], worldPos, radius);
	} else if (shadowMapIndex == 2) {
		return calcShadowFactorPcfImpl(lights.shadowMap2, lights.lights.shadowMapMatrices[2], worldPos, radius);
	}
	return calcShadowFactorPcfImpl(lights.shadowMap3, lights.lights.shadowMapMatrices[3], worldPos, radius);
}

