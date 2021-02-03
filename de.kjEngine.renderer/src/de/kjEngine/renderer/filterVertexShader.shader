input {
	topology = TRIANGLE_LIST;
}

output {
	vec2 coord;
}

void main() {
	switch (vertexIndex) {
	case 0: case 5:
		output.coord = vec2(0.0, 1.0);
		break;
	case 1:
		output.coord = vec2(0.0, 0.0);
		break;
	case 2: case 3:
		output.coord = vec2(1.0, 0.0);
		break;
	case 4:
		output.coord = vec2(1.0, 1.0);
		break;
	}
	vertexPosition = vec4(output.coord * 2.0 - 1.0, 0.0, 1.0);
}