
set data {
	uniformBuffer settings {
		vec3 upperColor;
		vec3 lowerColor;
	}
}

fragmentShader {
	input {
		vec2 coord;
	}
	
	output {
		RGBA16F vec4 color;
	}
	
	void main() {
		output.color = vec4(mix(data.settings.lowerColor, data.settings.upperColor, pow(max(input.coord.y * 2.0 - 1.0, 0.0), 0.5)), 1.0);
	}
}

const float PI = 3.14159;
