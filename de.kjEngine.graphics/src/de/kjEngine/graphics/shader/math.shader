
// constants

const float PI = 3.14159265358;
const float TWO_PI = 6.283185307179;
const float HALF_PI = 1.570796326794896;

// sphere coordinates

vec2 directionToSphereCoords(vec3 v) {
	return vec2(atan(v.x, v.z) * 0.5, asin(v.y)) / PI + 0.5;
}

vec3 sphereCoordsToDirection(vec2 v) {
	v = (v - 0.5) * vec2(TWO_PI, PI);
	float cosY = cos(v.y);
	return vec3(cosY * sin(v.x), sin(v.y), cosY * cos(v.x));
}