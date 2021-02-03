
set camera {
	uniformBuffer transforms {
		mat4 view;
		mat4 projection;
		mat4 viewProjection;
		mat4 invView;
		mat4 invProjection;
		mat4 invViewProjection;
		vec3 position;
	}
}

vec3 clipSpaceToWorldSpace(vec3 coord) {
	vec4 result = camera.transforms.invViewProjection * vec4(coord, 1.0);
	return result.xyz / result.w;
}

vec3 textureSpaceToWorldSpace(vec3 coord) {
	vec4 result = camera.transforms.invViewProjection * vec4(coord.xy * 2.0 - 1.0, coord.z, 1.0);
	return result.xyz / result.w;
}

vec3 worldSpaceToClipSpace(vec3 pos) {
	vec4 result = camera.transforms.viewProjection * vec4(pos, 1.0);
	return result.xyz / result.w;
}

vec3 worldSpaceToTextureSpace(vec3 pos) {
	vec4 result = camera.transforms.viewProjection * vec4(pos, 1.0);
	vec3 v = result.xyz / result.w;
	return vec3(v.xy * 0.5 + 0.5, v.z);
}
