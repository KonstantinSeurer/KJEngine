
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
	}
	
	void main() {
		vec3 pos = vec3(0.0);
		
		int baseIndex = indices.indices.indices[int(uniforms.instance_offset) + instanceIndex];
		
		{
			mat4 mMat = transforms.data.transforms[baseIndex + int(input.jointIds.x)];
			pos += (mMat * vec4(input.pos, 1.0)).xyz * input.jointWeights.x;
		}
			
		{
			mat4 mMat = transforms.data.transforms[baseIndex + int(input.jointIds.y)];
			pos += (mMat * vec4(input.pos, 1.0)).xyz * input.jointWeights.y;
		}
				
		{
			mat4 mMat = transforms.data.transforms[baseIndex + int(input.jointIds.z)];
			pos += (mMat * vec4(input.pos, 1.0)).xyz * input.jointWeights.z;
		}
					
		{
			mat4 mMat = transforms.data.transforms[baseIndex + int(input.jointIds.w)];
			pos += (mMat * vec4(input.pos, 1.0)).xyz * input.jointWeights.w;
		}
		vertexPosition = camera.transforms.viewProjection * vec4(pos, 1.0);
		output.tc = input.texCoord;
	}
}

fragmentShader {
	input {
		vec2 tc;
	
		cullMode = NONE;
		frontFace = COUNTER_CLOCKWISE;
		
		depthTest = true;
		depthWrite = true;
		depthClamp = false;
	}
	
	output {
		vec4 dummy;
	
		depth = true;
		blend = false;
	}
	
	void main() {
		if (texture(material.albedo, input.tc).a < 0.5) {
			discard;
		}
	}
}
