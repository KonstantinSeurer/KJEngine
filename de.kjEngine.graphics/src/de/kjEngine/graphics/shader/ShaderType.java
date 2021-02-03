/**
 * 
 */
package de.kjEngine.graphics.shader;

import java.util.HashMap;
import java.util.Map;

/**
 * @author konst
 *
 */
public enum ShaderType {
	VERTEX, TESSELATION_CONTROL, TESSELATION_EVALUATION, GEOMETRY, FRAGMENT, COMPUTE, RAY_GENERATION, HIT, MISS, NEAREST_HIT;
	
	private static Map<String, ShaderType> typeNameLookup = new HashMap<>();
	static {
		typeNameLookup.put("vertexShader", VERTEX);
		typeNameLookup.put("tesselationControlShader", TESSELATION_CONTROL);
		typeNameLookup.put("tesselationEvaluationShader", TESSELATION_EVALUATION);
		typeNameLookup.put("geometryShader", GEOMETRY);
		typeNameLookup.put("fragmentShader", FRAGMENT);
		typeNameLookup.put("computeShader", COMPUTE);
		typeNameLookup.put("rayGenerationShader", RAY_GENERATION);
		typeNameLookup.put("hitShader", HIT);
		typeNameLookup.put("missShader", MISS);
		typeNameLookup.put("nearestHitShader", NEAREST_HIT);
	}
	
	public static boolean isShaderType(String type) {
		return typeNameLookup.containsKey(type);
	}
	
	public static ShaderType getShaderType(String type) {
		return typeNameLookup.get(type);
	}
}
