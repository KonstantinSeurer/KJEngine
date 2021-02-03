#source vertex

#include jar://engine/de/kjEngine/core/scene/renderer/pipeline/shaders/filterVertexShader.txt

#source fragment

// https://github.com/BennyQBD/3DEngineCpp/blob/master/res/shaders/filter-fxaa.glsl

layout (location = 0) in vec2 coord;

layout (location = 0) out vec4 color;

layout (binding = 0, set = 0) uniform sampler2D tex;

layout (std140, binding = 1, set = 0) uniform DATA {
	vec2 size;
} data;

float luma(vec3 rgb) {
	return rgb.y * (0.587/0.299) + rgb.x; 
}

const float fxaaSpanMax = 8.0;
const float fxaaReduceMin = 0.01;
const float fxaaReduceMul = 1.125;

void main(void) {
	vec2 texCoordOffset = vec2(1.0) / data.size;
	vec3 luma = vec3(0.299, 0.587, 0.114);	
	float lumaTL = dot(luma, texture(tex, coord + (vec2(-1.0, -1.0) * texCoordOffset)).xyz);
	float lumaTR = dot(luma, texture(tex, coord + (vec2(1.0, -1.0) * texCoordOffset)).xyz);
	float lumaBL = dot(luma, texture(tex, coord + (vec2(-1.0, 1.0) * texCoordOffset)).xyz);
	float lumaBR = dot(luma, texture(tex, coord + (vec2(1.0, 1.0) * texCoordOffset)).xyz);
	float lumaM  = dot(luma, texture(tex, coord).xyz);

	vec2 dir;
	dir.x = -((lumaTL + lumaTR) - (lumaBL + lumaBR));
	dir.y = ((lumaTL + lumaBL) - (lumaTR + lumaBR));

	float dirReduce = max((lumaTL + lumaTR + lumaBL + lumaBR) * (fxaaReduceMul * 0.25), fxaaReduceMin);
	float inverseDirAdjustment = 1.0/(min(abs(dir.x), abs(dir.y)) + dirReduce);

	dir = min(vec2( fxaaSpanMax,  fxaaSpanMax), 
		  max(vec2(-fxaaSpanMax, -fxaaSpanMax), dir * inverseDirAdjustment));
	dir.x = dir.x * step(1.0, abs(dir.x));
	dir.y = dir.y * step(1.0, abs(dir.y));
	
	//float dirStep = max(step(1.0, abs(dir.x)), step(1.0, abs(dir.y)));
	//dir.x = dir.x * dirStep;
	//dir.y = dir.y * dirStep;

	dir = dir * texCoordOffset;

	vec3 result1 = (1.0/2.0) * (
		texture(tex, coord + (dir * vec2(1.0/3.0 - 0.5))).xyz +
		texture(tex, coord + (dir * vec2(2.0/3.0 - 0.5))).xyz);

	vec3 result2 = result1 * (1.0/2.0) + (1.0/4.0) * (
		texture(tex, coord + (dir * vec2(0.0/3.0 - 0.5))).xyz +
		texture(tex, coord + (dir * vec2(3.0/3.0 - 0.5))).xyz);

	float lumaMin = min(lumaM, min(min(lumaTL, lumaTR), min(lumaBL, lumaBR)));
	float lumaMax = max(lumaM, max(max(lumaTL, lumaTR), max(lumaBL, lumaBR)));

	float lumaResult2 = dot(luma, result2);

	if(lumaResult2 < lumaMin || lumaResult2 > lumaMax) {
		color = vec4(result1, 1.0);
	} else {
		color = vec4(result2, 1.0);
	}
}
