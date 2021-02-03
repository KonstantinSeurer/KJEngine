
fragmentShader {
	input {
		vec2 coord;
	}
	
	output {
		RGB16F vec3 color;
	}
	
	void main() {
		vec3 V = normalize(mat3(camera.camera.inv_vMat) * (camera.camera.inv_pMat * vec4(input.coord * 2.0 - 1.0, 1.0, 1.0)).xyz);
		Hit hit;
		trace(camera.camera.pos, V, hit);
		if (hit.t < 0.0) {
			output.color = mix(vec3(0.0, 0.0, 0.2), vec3(0.0), asin(V.y) / 3.14159 * 2.0);
		} else {
			output.color = vec3(0.0);
			{
				PointLight light = PointLight(vec3(0.0, 3.0, 0.0), vec3(5.0));
				vec3 toLight = light.pos - hit.P;
				vec3 L = normalize(toLight);
				float NdotL = max(dot(hit.N, L), 0.0);
				vec3 radiance = light.color * NdotL / (1.0 + dot(toLight, toLight));
				Hit shadowHit;
				trace(hit.P + L * 0.01, L, shadowHit);
				if (shadowHit.t < 0.0) {
					output.color += radiance * hit.material.albedo / 3.14159;
				}
			}
		}
	}
}

struct Material {
	vec3 albedo;
	float roughness;
}

struct Hit {
	float t;
	vec3 P;
	vec3 N;
	
	Material material;
}

struct PointLight {
	vec3 pos;
	vec3 color;
}

float iSphere(vec3 ro, vec3 rd, vec3 sph, float rad) {
	vec3 oc = ro - sph;
	float b = dot(oc, rd);
	float c = dot(oc, oc) - rad * rad;
	float t = b * b - c;
	if(t > 0.0) {
		t = -b - sqrt(t);
	}
	return t;
}

void trace(vec3 P, vec3 D, output Hit hit) {
	hit.t = 99999999.9;
	
	float tGround = -P.y / D.y;
	if (tGround > 0.0 && tGround < hit.t) {
		hit.t = tGround;
		hit.N = vec3(0.0, 1.0, 0.0);
		hit.P = P + D * hit.t;
		
		hit.material.albedo = vec3(1.0);
		hit.material.roughness = 1.0;
	}
	
	const vec3 pSphere = vec3(0.0, 1.0, 0.0);
	float tSphere = iSphere(P, D, pSphere, 0.5);
	if(tSphere > 0.0 && tSphere < hit.t) {
		hit.t = tSphere;
		hit.P = P + D * hit.t;
		hit.N = (hit.P - pSphere) * 2.0;
		
		hit.material.albedo = vec3(1.0, 0.7, 0.3);
		hit.material.roughness = 0.01;
	}
	
	if (hit.t > 9999999.9) {
		hit.t = -1.0;
	}
}
