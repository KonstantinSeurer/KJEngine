
set data {
	texture2D a;
	texture2D b;
	
	uniformBuffer data {
		vec3 color;
	}
}

fragmentShader {
	input {
		vec2 coord;
	}
	
	output {
		RGB16F vec3 color;
	}

	void main() {
		output.color = texture(data.a, input.coord).rgb * texture(data.b, input.coord).rgb * data.data.color;
	}
}
