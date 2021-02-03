
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
		float inner = getTesselationLevel(length(sceneCamera.transforms.position - transformPoint(vec2(input.offset[0].x + 0.5, input.offset[0].y + 0.5))));
	
		outerTesselationLevel[0] = getTesselationLevel(length(sceneCamera.transforms.position - transformPoint(vec2(input.offset[0].x, input.offset[0].y + 0.5))));
		outerTesselationLevel[1] = getTesselationLevel(length(sceneCamera.transforms.position - transformPoint(vec2(input.offset[0].x + 0.5, input.offset[0].y))));
		outerTesselationLevel[2] = getTesselationLevel(length(sceneCamera.transforms.position - transformPoint(vec2(input.offset[0].x + 1.0, input.offset[0].y + 0.5))));
		outerTesselationLevel[3] = getTesselationLevel(length(sceneCamera.transforms.position - transformPoint(vec2(input.offset[0].x + 0.5, input.offset[0].y + 1.0))));
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
		vertexPosition = lightCamera.transforms.viewProjection * vec4(output.pos, 1.0);
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
		vec3 dummy;
		
		depth = true;
	}
	
	void main() {
	}
}

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
