
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
		float inner = getTesselationLevel(length(sceneCamera.transforms.position - displaceAndTransformPoint(vec2(input.offset[0].x + 0.5, input.offset[0].y + 0.5))));
	
		outerTesselationLevel[0] = getTesselationLevel(length(sceneCamera.transforms.position - displaceAndTransformPoint(vec2(input.offset[0].x, input.offset[0].y + 0.5))));
		outerTesselationLevel[1] = getTesselationLevel(length(sceneCamera.transforms.position - displaceAndTransformPoint(vec2(input.offset[0].x + 0.5, input.offset[0].y))));
		outerTesselationLevel[2] = getTesselationLevel(length(sceneCamera.transforms.position - displaceAndTransformPoint(vec2(input.offset[0].x + 1.0, input.offset[0].y + 0.5))));
		outerTesselationLevel[3] = getTesselationLevel(length(sceneCamera.transforms.position - displaceAndTransformPoint(vec2(input.offset[0].x + 0.5, input.offset[0].y + 1.0))));
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
	}
	
	void main() {
		vec2 coord = (tesselationCoord.xy + input.offset[0]) / vec2(terrain.settings.tileCountX, terrain.settings.tileCountZ);
		vec3 pos = (terrain.settings.transform * vec4(displacePoint(coord), 1.0)).xyz;
		
		output.coord = coord * terrain.settings.textureCoordScale;
		
		pos.y += texture(material.displacement, output.coord).r;
		vertexPosition = lightCamera.transforms.viewProjection * vec4(pos, 1.0);
	}
}

fragmentShader {
	input {
		vec2 coord;
		
		drawMode = FILL;
		
		cullMode = NONE;
		frontFace = COUNTER_CLOCKWISE;
		
		depthWrite = true;
		depthClamp = false;
		depthTest = true;
	}
	
	output {
		vec4 dummy;
		
		depth = true;
	}
	
	void main() {
	}
}

vec3 displacePoint(vec2 coord) {
	return vec3(coord.x, texture(terrain.heightMap, coord).x, coord.y);
}

vec3 displaceAndTransformPoint(vec2 p) {
	p /= vec2(terrain.settings.tileCountX, terrain.settings.tileCountZ);
	return (terrain.settings.transform * vec4(displacePoint(p), 1.0)).xyz;
}
