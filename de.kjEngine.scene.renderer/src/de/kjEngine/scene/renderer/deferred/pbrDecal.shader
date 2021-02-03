
set data {
	texture2D depth;
}

uniforms {
	mat4 transform;
}

vertexShader {
	input {
		vec3 pos;
		
		topology = TRIANGLE_LIST;
	}
	
	output {
		vec3 pos;
	}
	
	void main() {
		output.pos = (uniforms.transform * vec4(input.pos, 1.0)).xyz;
		vertexPosition = camera.transforms.viewProjection * vec4(output.pos, 1.0);
	}
}

fragmentShader {
	input {
		vec3 pos;
	
		cullMaode = FRONT;
		frontFace = COUNTER_CLOCKWISE;
		
		depthTest = false;
		depthWrite = false;
		depthClamp = false;
	}
	
	output {
		vec3 albedo;
		vec3 normal;
		vec3 subsurface;
		vec3 emission;
		float roughness;
		float metalness;
		
		depth = true;
		
		blend = false;
		
		sourceFactor = SOURCE_ALPHA;
		destinationFactor = ONE_MINUS_SOURCE_ALPHA;
	}
	
	void main() {
		vec2 screenCoord = worldSpaceToTextureSpace(input.pos).xy;
		vec3 sampledPos = textureSpaceToWorldSpace(vec3(screenCoord, texture(data.depth, screenCoord).r));
		vec3 localSampledPos = (inverse(uniforms.transform) * vec4(sampledPos, 1.0)).xyz;
		if (localSampledPos.x < -1.0 || localSampledPos.x > 1.0 || localSampledPos.y < -1.0 || localSampledPos.y > 1.0
			|| localSampledPos.z < -1.0 || localSampledPos.z > 1.0) {
			discard;
		}
		vec2 materialCoord = localSampledPos.xz * 0.5 + 0.5;
		
		output.albedo = texture(material.albedo, materialCoord).rgb;
		output.roughness = texture(material.roughness, materialCoord).r;
		output.metalness = texture(material.metalness, materialCoord).r;
		output.subsurface = texture(material.subsurface, materialCoord).rgb;
		output.emission = texture(material.emission, materialCoord).rgb;
		// output.normal = vec3(0.0, 1.0, 0.0);
	}
}
