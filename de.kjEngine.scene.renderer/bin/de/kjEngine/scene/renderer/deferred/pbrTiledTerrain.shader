
vertexShader {
	input {
		topology = PATCH_LIST;
	}
	
	output {
		vec2 offset;
	}
	
	void main() {
		output.offset = vec2(vertexIndex % terrain.settings.tileCountX, vertexIndex / terrain.settings.tileCountX);
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
		float inner = getTesselationLevel(length(camera.transforms.position - displaceAndTransformPoint(vec2(input.offset[0].x + 0.5, input.offset[0].y + 0.5))));
	
		outerTesselationLevel[0] = getTesselationLevel(length(camera.transforms.position - displaceAndTransformPoint(vec2(input.offset[0].x, input.offset[0].y + 0.5))));
		outerTesselationLevel[1] = getTesselationLevel(length(camera.transforms.position - displaceAndTransformPoint(vec2(input.offset[0].x + 0.5, input.offset[0].y))));
		outerTesselationLevel[2] = getTesselationLevel(length(camera.transforms.position - displaceAndTransformPoint(vec2(input.offset[0].x + 1.0, input.offset[0].y + 0.5))));
		outerTesselationLevel[3] = getTesselationLevel(length(camera.transforms.position - displaceAndTransformPoint(vec2(input.offset[0].x + 0.5, input.offset[0].y + 1.0))));
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
		vec2 coord;
		vec3 normal;
		vec3 tangent;
		vec3 bitangent;
	}
	
	void main() {
		const vec2 coord = (tesselationCoord.xy + input.offset[0]) / vec2(terrain.settings.tileCountX, terrain.settings.tileCountZ);
		const float epsilon = 1.0 / textureSize(terrain.heightMap, 0).x;
		
		vec2 normalSampleCoord = coord;
		if (normalSampleCoord.x + epsilon > 1.0) {
			normalSampleCoord.x -= epsilon;
		}
		if (normalSampleCoord.y + epsilon > 1.0) {
			normalSampleCoord.y -= epsilon;
		}
		vec3 pos = (terrain.settings.transform * vec4(displacePoint(normalSampleCoord), 1.0)).xyz;
		vec3 p1 = (terrain.settings.transform * vec4(displacePoint(normalSampleCoord + vec2(epsilon, 0.0)), 1.0)).xyz;
		vec3 p2 = (terrain.settings.transform * vec4(displacePoint(normalSampleCoord + vec2(0.0, epsilon)), 1.0)).xyz;
		
		output.tangent = normalize(p1 - pos);
		output.bitangent = normalize(p2 - pos);
		output.normal = normalize(cross(output.bitangent, output.tangent));
		
		output.coord = coord * terrain.settings.textureCoordScale;
		
		vec3 geometryPos = (terrain.settings.transform * vec4(displacePoint(coord), 1.0)).xyz;
		geometryPos.y += texture(material.displacement, output.coord).r;
		vertexPosition = camera.transforms.viewProjection * vec4(geometryPos, 1.0);
	}
}

fragmentShader {
	input {
		vec2 coord;
		vec3 normal;
		vec3 tangent;
		vec3 bitangent;
		
		drawMode = FILL;
		
		cullMode = BACK;
		frontFace = COUNTER_CLOCKWISE;
		
		depthWrite = true;
		depthClamp = false;
		depthTest = true;
	}
	
	void main() {
		vec3 camPos = camera.transforms.position;
		float seed = fragmentCoord.x * 0.2 + fragmentCoord.y * 0.7 + fragmentCoord.z * 2.3 + camPos.x + camPos.y * 3.1 + camPos.z * 0.4;
		vec2 tc = input.coord;
		output.albedo = texture(material.albedo, tc).rgb;
		output.roughness = texture(material.roughness, tc).r;
		output.metalness = texture(material.metalness, tc).r;
		output.subsurface = texture(material.subsurface, tc).rgb;
		output.emission = texture(material.emission, tc).rgb;
		vec3 normalMapSample = texture(material.normal, tc).rgb * 2.0 - 1.0;
		output.normal = dither_8(normalize(normalMapSample.x * input.tangent + normalMapSample.y * input.bitangent + normalMapSample.z * input.normal) * 0.5 + 0.5, seed);
	}
}

vec3 displacePoint(vec2 coord) {
	return vec3(coord.x, texture(terrain.heightMap, coord).x, coord.y);
}

vec3 displaceAndTransformPoint(vec2 p) {
	p /= vec2(terrain.settings.tileCountX, terrain.settings.tileCountZ);
	return (terrain.settings.transform * vec4(displacePoint(p), 1.0)).xyz;
}


float rand(input output float seed){
	seed += 0.23497;
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
