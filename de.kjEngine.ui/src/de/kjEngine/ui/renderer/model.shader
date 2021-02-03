
uniforms {
	mat4 transform;
	vec2 invWindowSize;
}

vertexShader {
	input {
		vec2 pos;
		vec2 tc;
		
		topology = TRIANGLE_LIST;
	}
	
	output {
		vec2 tc;
	}
	
	void main() {
		output.tc = input.tc;
		
		vertexPosition = vec4(uniforms.invWindowSize.x, -uniforms.invWindowSize.y, 1.0, 1.0) * (uniforms.transform * vec4(input.pos, 0.0, 1.0)) 
					   + vec4(-1.0, 1.0, 0.0, 0.0);
	}
}

fragmentShader {
	input {
		vec2 tc;
		
		cullMode = NONE;
		depthTest = false;
		drawMode = FILL;
	}
	
	output {
		vec4 result;
		
		depth = false;
		
		blend = true;
		sourceFactor = SOURCE_ALPHA;
		destinationFactor = ONE_MINUS_SOURCE_ALPHA;
	}
	
	void main() {
		output.result = texture(material.texture, input.tc);
	}
}
