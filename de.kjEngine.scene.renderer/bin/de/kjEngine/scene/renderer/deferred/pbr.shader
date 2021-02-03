
fragmentShader {
	input {
		vec2 coord;
	}
	
	output {
		RGB16F vec3 color;
		depth = false;
	}
	
	void main() {
		float depth = texture(textures.depth, input.coord).r;
		
		vec3 worldPos = textureSpaceToWorldSpace(vec3(input.coord, depth));
		vec3 V = normalize(camera.transforms.position - worldPos);
		
		if (depth > 0.9999) {
			vec3 result = texture(textures.base, input.coord).rgb;
			
			int directionalLightCount = lights.lights.directionalLightCount;
			for (int i = 0; i < directionalLightCount; i++) { // directional lights
				DirectionalLight light = lights.lights.directionalLights[i];
				float factor = smoothstep(0.01, 0.012, acos(dot(light.direction, V)));
				result = mix(light.color * 100.0, result, factor);
			}
			
			output.color = result;
			return;
		}
		
		vec3 N = normalize(texture(textures.normal, input.coord).rgb * 2.0 - 1.0);
		vec3 albedo = texture(textures.albedo, input.coord).rgb;
		float roughness = max(texture(textures.roughness, input.coord).r, 0.05);
		float metalness = texture(textures.metalness, input.coord).r;
		vec3 subsurface = texture(textures.subsurface, input.coord).rgb;
		vec3 emission = texture(textures.emission, input.coord).rgb;
		vec3 base = texture(textures.base, input.coord).rgb;
		
		vec3 result = emission;
		
		vec3 F0 = mix(vec3(0.04), albedo, metalness);
		
		float NdotV = max(dot(N, V), 0.0);
		
		for (int i = lights.lights.directionalLightCount - 1; i >= 0; i--) { // directional lights
			DirectionalLight light = lights.lights.directionalLights[i];
		
			vec3 H = normalize(V - light.direction);
			
			float NdotL = max(-dot(N, light.direction), 0.0);
			if (NdotL < 0.0) {
				continue;
			}
			
			float NdotH = max(dot(N, H), 0.0);
			
			float shadowFactor = 1.0;
			if (light.shadowMapIndex != -1) {
				for (int j = 0; j < light.shadowMapCount; j++) {
					vec2 shadowResult = calcShadowFactorPcf(light.shadowMapIndex + j, worldPos, 4);
					shadowFactor = mix(shadowFactor, shadowResult.x, shadowResult.y);
				}
			}
			
			vec3 ks = F(max(dot(H, V), 0.0), F0);
			
			result += light.color * NdotL * shadowFactor
						 * (ks * D(NdotH, roughness) * G(NdotV, NdotL, roughness) / max(4.0 * NdotV * NdotL, 0.0001)
						 + (1.0 - (ks.r + ks.g + ks.b) * 0.3333) * (1.0 - metalness) * albedo / PI);
		}
		
		for (int i = lights.lights.pointLightCount - 1; i >= 0; i--) {
			PointLight light = lights.lights.pointLights[i];
			
			vec3 toLight = light.position - worldPos;
			vec3 L = normalize(toLight);
			vec3 H = normalize(V + L);
			float NdotL = max(dot(N, L), 0.0);
			float NdotH = max(dot(N, H), 0.0);
			
			vec3 radience = light.color * NdotL / (1.0 + dot(toLight, toLight));
			
			vec3 ks = F(max(dot(H, V), 0.0), F0);
			vec3 f_specular = ks * D(NdotH, roughness) * G(NdotV, NdotL, roughness) / max(4.0 * NdotV * NdotL, 0.0001);
		
			float kd = (1.0 - (ks.r + ks.g + ks.b) * 0.3333) * (1.0 - metalness);
			vec3 f_diffuse = albedo * kd / PI;
		
			result += radience * (f_diffuse + f_specular);
		}
		
		for (int i = lights.lights.sphereLightCount - 1; i >= 0; i--) {
			SphereLight light = lights.lights.sphereLights[i];
			
			vec3 toLight = light.position - worldPos;
			float dist = length(toLight);
			
			float coneAngle = clamp(asin(light.radius / dist), 0.0, 1.0);
			
			vec3 L = normalize(toLight);
			
			vec3 R = reflect(-V, N);
			
			float lightAngle = acos(dot(R, L));
			
			float D = exp(-abs(pow(lightAngle / coneAngle, coneAngle / roughness + 1.0)));
			
			vec3 H = normalize(V + L);
			
			float NdotL = max(dot(N, L), 0.0);
			float HdotV = max(dot(H, V), 0.0);
			
			float distToLight = max(dist - light.radius, 0.0);
			
			vec3 specular_radience = light.color * NdotL;
			vec3 diffuse_radience = specular_radience / (1.0 + distToLight * distToLight);
			
			vec3 ks = F(HdotV, F0);
			vec3 f_specular = ks * D;
		
			float kd = (1.0 - (ks.r + ks.g + ks.b) * 0.3333) * (1.0 - metalness);
			vec3 f_diffuse = albedo * kd / PI;
		
			result += diffuse_radience * f_diffuse + specular_radience * f_specular;
		}
		
		for (int i = lights.lights.spotLightCount - 1; i >= 0; i--) {
			SpotLight light = lights.lights.spotLights[i];
			
			vec3 toLight = light.position - worldPos;
			vec3 L = normalize(toLight);
			
			float angle = acos(dot(L, -light.direction));
			
			vec3 H = normalize(V + L);
			float NdotL = max(dot(N, L), 0.0);
			float NdotH = max(dot(N, H), 0.0);
			
			vec3 radience = light.color * NdotL / (1.0 + dot(toLight, toLight)) * pow(cos(clamp(2.0 * angle * HALF_PI / light.angle, -HALF_PI, HALF_PI)), light.falloff);
			
			vec3 ks = F(max(dot(H, V), 0.0), F0);
			vec3 f_specular = ks * D(NdotH, roughness) * G(NdotV, NdotL, roughness) / max(4.0 * NdotV * NdotL, 0.0001);
		
			float kd = (1.0 - (ks.r + ks.g + ks.b) * 0.3333) * (1.0 - metalness);
			vec3 f_diffuse = albedo * kd / PI;
		
			result += radience * (f_diffuse + f_specular);
		}
		
		{ // global
			vec3 ks = F(NdotV, F0, roughness);
			float kd = (1.0 - (ks.r + ks.g + ks.b) * 0.3333) * (1.0 - metalness);
			
			result += albedo * kd * texture(textures.diffuse, input.coord).rgb;
			
			vec2 integrated_brdf = texture(textures.brdf, vec2(NdotV, roughness)).rg;
			
			// result += texture(textures.specular, input.coord).rgb * (ks * integrated_brdf.x + integrated_brdf.y);
			result += texture(textures.specular, input.coord).rgb * ks;
		}
		
		output.color = result;
	}
}

const float PI = 3.1415926;
const float HALF_PI = 1.57079632;

vec3 F(float cos_a, vec3 F0) {
	return F0 + (1.0 - F0) * pow(1.0 - cos_a, 5.0);
}

vec3 F(float cos_a, vec3 F0, float roughness) {
	return F0 + (1.0 - F0) * pow(1.0 - cos_a, 5.0) * (1.0 - roughness);
}

float D(float NdotH, float roughness) {
	float a = roughness * roughness;
    float a2     = a * a;
    float NdotH2 = NdotH * NdotH;
	
    float nom    = a2;
    float denom  = (NdotH2 * (a2 - 1.0) + 1.0);
    denom        = PI * denom * denom;
	
    return nom / denom;
}

float GeometrySchlickGGX(float NdotV, float k) {
    return NdotV / (NdotV * (1.0 - k) + k);
}

float G(float NdotV, float NdotL, float roughness) {
	float r = (roughness + 1.0);
    float k = (r * r) / 8.0;
    
    return GeometrySchlickGGX(NdotV, k) * GeometrySchlickGGX(NdotL, k);
}

vec2 calcShadowFactorPcfImpl(texture2D shadowMap, mat4 shadowMatrix, vec3 worldPos, int radius) {
	vec4 projectedCoord = shadowMatrix * vec4(worldPos, 1.0);
	projectedCoord.xy / projectedCoord.w;
	projectedCoord.xy *= 0.5;
	projectedCoord.xy += 0.5;
	
	if (projectedCoord.x < 0.0 || projectedCoord.x > 1.0 || projectedCoord.y < 0.0 || projectedCoord.y > 1.0 || projectedCoord.z < 0.0 || projectedCoord.z > 1.0) {
		return vec2(1.0, 0.0);
	}
	
	int diameter = radius * 2 + 1;
	int area = diameter * diameter;
	
	vec2 invMapSize = 1.0 / vec2(textureSize(shadowMap, 0));
	
	float result = 0.0;
	
	int actualRadius = int(ceil(float(radius) * 0.5));
	
	for (int x = -actualRadius; x < actualRadius; x++) {
		for (int y = -actualRadius; y < actualRadius; y++) {
			vec2 sampleCoord = projectedCoord.xy + vec2(x, y) * invMapSize * 2.0;
			
			vec4 samples = textureGather(shadowMap, sampleCoord, 0) + 0.0005;
			bvec4 comparison = greaterThan(samples, vec4(projectedCoord.z));
			
			if (comparison.x) {
				result += 1.0;
			}
			if (comparison.y) {
				result += 1.0;
			}
			if (comparison.z) {
				result += 1.0;
			}
			if (comparison.w) {
				result += 1.0;
			}
		}
	}
	
	return vec2(result / float(area), 1.0);
}

vec2 calcShadowFactorPcf(int shadowMapIndex, vec3 worldPos, int radius) {	
	if (shadowMapIndex == 0) {
		return calcShadowFactorPcfImpl(lights.shadowMap0, lights.lights.shadowMapMatrices[0], worldPos, radius);
	} else if (shadowMapIndex == 1) {
		return calcShadowFactorPcfImpl(lights.shadowMap1, lights.lights.shadowMapMatrices[1], worldPos, radius);
	} else if (shadowMapIndex == 2) {
		return calcShadowFactorPcfImpl(lights.shadowMap2, lights.lights.shadowMapMatrices[2], worldPos, radius);
	}
	return calcShadowFactorPcfImpl(lights.shadowMap3, lights.lights.shadowMapMatrices[3], worldPos, radius);
}

vec3 diffuseOrenNayer(float NdotL, float NdotV, float LdotV, float roughness, vec3 albedo) {
	float s = LdotV - NdotL * NdotV;
	float t = mix(1.0, max(NdotL, NdotV), step(0.0, s));

	float sigma2 = roughness * roughness;
	vec3 A = 1.0 + sigma2 * (albedo / (sigma2 + 0.13) + 0.5 / (sigma2 + 0.33));
	float B = 0.45 * sigma2 / (sigma2 + 0.09);

	return albedo * max(0.0, NdotL) * (A + B * s / t) / PI;
}
