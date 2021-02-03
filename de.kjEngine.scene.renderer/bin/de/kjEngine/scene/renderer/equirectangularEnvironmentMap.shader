
set data {
	texture2D map;
}

include math;

fragmentShader {
	input {
		vec2 coord;
	}
	
	output {
		RGB16F vec3 color;
	}
	
	void main() {
		vec3 V = normalize(mat3(camera.transforms.invView) * (camera.transforms.invProjection * vec4(input.coord * 2.0 - 1.0, 1.0, 1.0)).xyz);
		output.color = texture(data.map, directionToSphereCoords(V)).rgb;
	}
}
