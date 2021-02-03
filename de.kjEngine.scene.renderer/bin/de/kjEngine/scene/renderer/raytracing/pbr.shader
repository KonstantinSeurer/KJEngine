
bool intersectAABB(vec3 P, vec3 invD, vec3 aabbMin, vec3 aabbMax, output float t) {
	const bvec3 lt = lessThan(invD, vec3(0.0));
	const vec3 m1 = (aabbMin - P) * invD;
	const vec3 m2 = (aabbMax - P) * invD;
	const vec3 tmin = mix(m1, m2, lt);
	const vec3 tmax = mix(m2, m1, lt);
	const float mint = max(max(tmin.x, tmin.y), tmin.z);
	const float maxt = min(min(tmax.x, tmax.y), tmax.z);
	t = mint;
	return maxt >= 0.0 && mint <= maxt * 1.00000024;
}

bool intersectAABB(vec3 P, vec3 invD, vec3 aabbMin, vec3 aabbMax) {
	const bvec3 lt = lessThan(invD, vec3(0.0));
	const vec3 m1 = (aabbMin - P) * invD;
	const vec3 m2 = (aabbMax - P) * invD;
	const vec3 tmin = mix(m1, m2, lt);
	const vec3 tmax = mix(m2, m1, lt);
	const float mint = max(max(tmin.x, tmin.y), tmin.z);
	const float maxt = min(min(tmax.x, tmax.y), tmax.z);
	return maxt >= 0.0 && mint <= maxt * 1.00000024;
}

struct TriangleHitInfo {
	float t;
	float u;
	float v;
};

bool intersectTriangle(vec3 origin, vec3 dir, vec3 v0, vec3 v1, vec3 v2, output TriangleHitInfo thinfo) {
	vec3 e1 = v1 - v0, e2 = v2 - v0;
	vec3 pvec = cross(dir, e2);
	float det = dot(e1, pvec);
	if (det < 0.0) {
		return false;
	}
	vec3 tvec = origin - v0;
	float u = dot(tvec, pvec);
	if (u < 0.0 || u > det) {
		return false;
	}
	vec3 qvec = cross(tvec, e1);
	float v = dot(dir, qvec);
	if (v < 0.0 || u + v > det) {
		return false;
	}
	float invDet = 1.0 / det;
	float t = dot(e2, qvec) * invDet;
	thinfo.u = u * invDet;
	thinfo.v = v * invDet;
	thinfo.t = t;
	return t >= 0.0;
}

int closestChild(int index, vec3 origin, BottomLevelNode n, output uint leftRight) {
	const BottomLevelNode left = bvh.models[index].nodes[n.left];
	const BottomLevelNode right = bvh.models[index].nodes[n.right];
	const vec3 dl = origin - (left.min.xyz + left.max.xyz) * 0.5;
	const vec3 dr = origin - (right.min.xyz + right.max.xyz) * 0.5;
	if (dot(dl, dl) < dot(dr, dr)) {
		leftRight = 0;
		return n.left;
	} else {
		leftRight = 1;
		return n.right;
	}
}

bool processNextFarChild(int index, input output uint nearFarStack, input output uint leftRightStack, input output uint depth, input BottomLevelNode n, input output uint nextIdx) {
	BottomLevelNode parent = bvh.models[index].nodes[n.parent];
	while ((nearFarStack & 1u) == 1u) {
		nearFarStack >>= 1;
		leftRightStack >>= 1;
		depth--;
		if (depth == 0u)
			return false;
		parent = bvh.models[index].nodes[parent.parent];
	}
	nextIdx = (leftRightStack & 1u) == 0u ? parent.right : parent.left;
	nearFarStack |= 1;
	return true;
}

void intersectTriangles(int index, BottomLevelNode n, vec3 P, vec3 D, output TriangleHitInfo info) {
	info.t = 1.0 / 0.0;
	for (int i = 0; i < 10; i++) {
		if (i < n.triangleCount) {
			const int baseIndex = n.firstIndex + i * 3;
			const vec3 p0 = bvh.models[index].position_tc_x[bvh.models[index].indices[baseIndex]].xyz;
			const vec3 p1 = bvh.models[index].position_tc_x[bvh.models[index].indices[baseIndex + 1]].xyz;
			const vec3 p2 = bvh.models[index].position_tc_x[bvh.models[index].indices[baseIndex + 2]].xyz;
			TriangleHitInfo newInfo;
			if (intersectTriangle(P, D, p0, p1, p2, newInfo)) {
				info.t = min(info.t, newInfo.t);
				info.t = 0.0;
			}
		}
	}
}

bool traceBottomLevelNearest(int index, vec3 P, vec3 D, output float t) {
	const vec3 invD = 1.0 / D;
	uint nextIdx = 0u;
	uint iterationCount = 0u;
	t = 1.0 / 0.0;
	
	uint nearFarStack = 0u, leftRightStack = 0u, depth = 0u;
	
	while (iterationCount < 200) {
		const BottomLevelNode n = bvh.models[index].nodes[nextIdx];
		
		float currentT;
		if (!intersectAABB(P, invD, n.min, n.max, currentT) || currentT > t) {
			if (depth == 0u) {
				break;
			}
			if (!processNextFarChild(index, nearFarStack, leftRightStack, depth, n, nextIdx)) {
				break;
			}
		} else {
			if (n.triangleCount > 0) {
				TriangleHitInfo info;
				intersectTriangles(index, n, P, D, info);
				t = min(t, info.t);
				
				if (!processNextFarChild(index, nearFarStack, leftRightStack, depth, n, nextIdx)) {
					break;
				}
			} else {
				uint leftRight;
				nextIdx = closestChild(index, P, n, leftRight);

				nearFarStack <<= 1;
				leftRightStack = leftRightStack << 1 | leftRight;
				depth++;
			}
		}
	
		iterationCount++;
	}
	
	return t != 1.0 / 0.0;
}

int closestChild(vec3 origin, Node n, output uint leftRight) {
	const Node left = bvh.bvh.nodes[n.left];
	const Node right = bvh.bvh.nodes[n.right];
	const vec3 dl = origin - (left.min.xyz + left.max.xyz) * 0.5;
	const vec3 dr = origin - (right.min.xyz + right.max.xyz) * 0.5;
	if (dot(dl, dl) < dot(dr, dr)) {
		leftRight = 0;
		return n.left;
	} else {
		leftRight = 1;
		return n.right;
	}
}

bool processNextFarChild(input output uint nearFarStack, input output uint leftRightStack, input output uint depth, input Node n, input output uint nextIdx) {
	Node parent = bvh.bvh.nodes[n.parent];
	while ((nearFarStack & 1u) == 1u) {
		nearFarStack >>= 1;
		leftRightStack >>= 1;
		depth--;
		if (depth == 0u)
			return false;
		parent = bvh.bvh.nodes[parent.parent];
	}
	nextIdx = (leftRightStack & 1u) == 0u ? parent.right : parent.left;
	nearFarStack |= 1;
	return true;
}

bool traceNearest(vec3 P, vec3 D, output float t) {
	const vec3 invD = 1.0 / D;
	uint nextIdx = 0u;
	uint iterationCount = 0u;
	t = 1.0 / 0.0;
	
	uint nearFarStack = 0u, leftRightStack = 0u, depth = 0u;
	
	while (iterationCount < 200) {
		const Node n = bvh.bvh.nodes[nextIdx];
		
		float currentT;
		if (!intersectAABB(P, invD, n.min.xyz, n.max.xyz, currentT) || currentT > t) {
			if (depth == 0u) {
				break;
			}
			if (!processNextFarChild(nearFarStack, leftRightStack, depth, n, nextIdx)) {
				break;
			}
		} else {
			if (n.model != -1) {
				const vec3 localP = (n.invTransform * vec4(P, 1.0)).xyz;
				const vec3 localD = mat3(n.invTransform) * D;
				if (traceBottomLevelNearest(n.model, localP, localD, currentT)) {
					t = min(t, currentT);
				}
				
				if (!processNextFarChild(nearFarStack, leftRightStack, depth, n, nextIdx)) {
					break;
				}
			} else {
				uint leftRight;
				nextIdx = closestChild(P, n, leftRight);

				nearFarStack <<= 1;
				leftRightStack = leftRightStack << 1 | leftRight;
				depth++;
			}
		}
	
		iterationCount++;
	}
	
	return t != 1.0 / 0.0;
}

bool traceAny(vec3 P, vec3 D) {
	const vec3 invD = 1.0 / D;
	uint nextIdx = 0u;
	uint iterationCount = 0u;
	
	uint nearFarStack = 0u, leftRightStack = 0u, depth = 0u;
	
	while (iterationCount < 200) {
		const Node n = bvh.bvh.nodes[nextIdx];
		
		if (!intersectAABB(P, invD, n.min.xyz, n.max.xyz)) {
			if (depth == 0u) {
				return false;
			}
			if (!processNextFarChild(nearFarStack, leftRightStack, depth, n, nextIdx)) {
				return false;
			}
		} else {
			if (n.model != -1) {
				return true;
			} else {
				nextIdx = n.left;

				nearFarStack <<= 1;
				leftRightStack <<= 1;
				depth++;
			}
		}
	
		iterationCount++;
	}
	
	return false;
}

bool traceAny(vec3 P, vec3 D, float maxT) {
	const vec3 invD = 1.0 / D;
	uint nextIdx = 0u;
	uint iterationCount = 0u;
	
	uint nearFarStack = 0u, leftRightStack = 0u, depth = 0u;
	float t = 1.0 / 0.0;
	
	while (iterationCount < 200) {
		const Node n = bvh.bvh.nodes[nextIdx];
		
		float currentT;
		if (!intersectAABB(P, invD, n.min.xyz, n.max.xyz, currentT) || currentT > t) {
			if (depth == 0u) {
				return false;
			}
			if (!processNextFarChild(nearFarStack, leftRightStack, depth, n, nextIdx)) {
				return false;
			}
		} else {
			if (n.model != -1) {
				t = min(t, currentT);
				
				if (t < maxT) {
					return true;
				}
				
				if (!processNextFarChild(nearFarStack, leftRightStack, depth, n, nextIdx)) {
					return false;
				}
			} else {
				uint leftRight;
				nextIdx = closestChild(P, n, leftRight);

				nearFarStack <<= 1;
				leftRightStack = leftRightStack << 1 | leftRight;
				depth++;
			}
		}
	
		iterationCount++;
	}
	
	return false;
}

fragmentShader {
	input {
		vec2 coord;
	}
	
	output {
		RGB16F vec3 result;
	}
	
	void main() {
		vec3 V = normalize(mat3(camera.transforms.invView) * (camera.transforms.invProjection * vec4(input.coord * 2.0 - 1.0, 1.0, 1.0)).xyz);
		float t = 0.0;
		if (traceNearest(camera.transforms.position, V, t)) {
			output.result = vec3(exp(-t * 0.2), 0.1, 0.2);
		} else {
			output.result = vec3(0.0, 0.0, 1.0);
		}
	}
}
