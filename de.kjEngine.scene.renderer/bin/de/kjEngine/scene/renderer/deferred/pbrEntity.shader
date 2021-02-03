
uniforms {
	float instance_offset;
}

vertexShader {
	input {
		vec3 pos;
		vec2 texCoord;
		vec3 normal;
		vec4 jointIds;
		vec4 jointWeights;
		
		topology = TRIANGLE_LIST;
	}
	
	output {
		vec2 tc;
		vec3 normal;
		vec3 tangent;
		vec3 bitangent;
		vec3 pos;
	}
	
	void main() {
		vec3 pos = vec3(0.0);
		output.normal = vec3(0.0);
		
		int baseIndex = indices.indices.indices[int(uniforms.instance_offset) + instanceIndex];
		
		{
			mat4 mMat = transforms.data.transforms[baseIndex + int(input.jointIds.x)];
			pos += (mMat * vec4(input.pos, 1.0)).xyz * input.jointWeights.x;
			output.normal += mat3(mMat) * input.normal * input.jointWeights.x;
		}
			
		{
			mat4 mMat = transforms.data.transforms[baseIndex + int(input.jointIds.y)];
			pos += (mMat * vec4(input.pos, 1.0)).xyz * input.jointWeights.y;
			output.normal += mat3(mMat) * input.normal * input.jointWeights.y;
		}
				
		{
			mat4 mMat = transforms.data.transforms[baseIndex + int(input.jointIds.z)];
			pos += (mMat * vec4(input.pos, 1.0)).xyz * input.jointWeights.z;
			output.normal += mat3(mMat) * input.normal * input.jointWeights.z;
		}
					
		{
			mat4 mMat = transforms.data.transforms[baseIndex + int(input.jointIds.w)];
			pos += (mMat * vec4(input.pos, 1.0)).xyz * input.jointWeights.w;
			output.normal += mat3(mMat) * input.normal * input.jointWeights.w;
		}

		vertexPosition = camera.transforms.viewProjection * vec4(pos, 1.0);
		
		output.normal = normalize(output.normal);
		output.tangent = cross(output.normal, vec3(0.0, 1.0, 0.0));
		output.bitangent = cross(output.normal, output.tangent);
		
		output.tc = input.texCoord;
	}
}

fragmentShader {
	input {
		vec2 tc;
		vec3 normal;
		vec3 tangent;
		vec3 bitangent;
		
		cullMaode = BACK;
		frontFace = COUNTER_CLOCKWISE;
		
		depthTest = true;
		depthWrite = true;
		depthClamp = false;
	}
	
	void main() {
		vec3 camPos = camera.transforms.position;
		float seed = fragmentCoord.x * 0.2 + fragmentCoord.y * 0.7 + fragmentCoord.z * 2.3 + camPos.x + camPos.y * 3.1 + camPos.z * 0.4;
		
		vec4 albedoSample = texture(material.albedo, input.tc);
		if (albedoSample.a < clamp(rand(seed), 0.01, 0.99)) {
			discard;
		} else {
			output.albedo = albedoSample.rgb;
			output.roughness = texture(material.roughness, input.tc).r;
			output.metalness = texture(material.metalness, input.tc).r;
			output.subsurface = texture(material.subsurface, input.tc).rgb;
			output.emission = texture(material.emission, input.tc).rgb;
			vec3 normalMapSample = texture(material.normal, input.tc).rgb * 2.0 - 1.0;
			output.normal = dither_8(normalize(normalMapSample.x * input.tangent + normalMapSample.y * input.bitangent + normalMapSample.z * input.normal) * 0.5 + 0.5, seed);
		}
	}
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
