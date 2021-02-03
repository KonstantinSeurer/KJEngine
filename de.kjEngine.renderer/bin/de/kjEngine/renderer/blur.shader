
include math;

set data {
	texture2D texture;
	
	uniformBuffer data {
		vec2 axis;
		float sampleCount;
	}
}

const float stDevSquared = 0.1;
const float f1 = (1.0 / sqrt(TWO_PI * stDevSquared));
const float f2 = 1.0 / (2.0 * stDevSquared);

fragmentShader {
	input {
		vec2 coord;
	}
	
	output {
		vec3 color;
	}

	void main() {
		float half_sample_count = data.data.sampleCount * 0.5;
		float sampleSpacing = 1.0 / half_sample_count;
		float sum = 0.0;
		output.color = vec3(0.0);
		
		for (float i = -half_sample_count; i <= half_sample_count; i += 1.0) {
			float offset = i * sampleSpacing;
			
			float gauss = f1 * exp(-offset * offset * f2);
			
			sum += gauss;
			
			output.color += texture(data.texture, clamp(input.coord + data.data.axis * offset, vec2(0.001), vec2(0.999))).rgb * gauss;
		}
		output.color /= sum;
	}
}
