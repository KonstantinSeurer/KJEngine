
set data {
	texture2D texture;
	
	uniformBuffer data {
		float exposure;
	}
}

fragmentShader {
	input {
		vec2 coord;
	}
	
	output {
		vec3 color;
	}

	void main() {
		output.color = vec3(1.0) - exp(-texture(data.texture, input.coord).rgb * data.data.exposure);
	}
}
