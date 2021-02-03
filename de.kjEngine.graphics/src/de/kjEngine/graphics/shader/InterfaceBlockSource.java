/**
 * 
 */
package de.kjEngine.graphics.shader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author konst
 *
 */
public class InterfaceBlockSource {

	public static InterfaceBlockSource parse(ObjectSource source) throws ShaderCompilationException {
		List<VaryingVariableSource> variables = new ArrayList<>();
		Map<String, String> settings = new HashMap<>();

		int startIndex = 0;
		String varyingContent = source.getContent();
		for (int i = 0; i < varyingContent.length(); i++) {
			if (varyingContent.charAt(i) == ';') {
				String declaration = varyingContent.substring(startIndex, i).trim();
				startIndex = i + 1;
				if (declaration.contains("=")) {
					String[] pts = declaration.split("=");
					if (pts.length != 2) {
						throw new IllegalArgumentException();
					}
					settings.put(pts[0].trim(), pts[1].trim());
				} else {
					variables.add(VaryingVariableSource.parse(declaration));
				}
			}
		}
		return new InterfaceBlockSource(variables, settings);
	}

	private List<VaryingVariableSource> variables;
	private Map<String, String> settings;
	
	public InterfaceBlockSource() {
		this(new ArrayList<>(), new HashMap<>());
	}

	public InterfaceBlockSource(List<VaryingVariableSource> variables, Map<String, String> settings) {
		this.variables = variables;
		this.settings = settings;
	}

	/**
	 * @return the variables
	 */
	public List<VaryingVariableSource> getVariables() {
		return variables;
	}

	/**
	 * @return the settings
	 */
	public Map<String, String> getSettings() {
		return settings;
	}

	public int getInt(String name) {
		return Integer.parseInt(settings.get(name));
	}

	public boolean getBoolean(String name) {
		return Boolean.parseBoolean(settings.get(name));
	}

	public float getFloat(String name) {
		return Float.parseFloat(settings.get(name));
	}

	public BlendFactor getBlendFactor(String name) {
		return BlendFactor.valueOf(settings.get(name));
	}

	public DrawMode getDrawMode(String name) {
		return DrawMode.valueOf(settings.get(name));
	}

	public CullMode getCullMode(String name) {
		return CullMode.valueOf(settings.get(name));
	}

	public WindingOrder getWindingOrder(String name) {
		return WindingOrder.valueOf(settings.get(name));
	}

	public PrimitiveType getPrimitiveType(String name) {
		return PrimitiveType.valueOf(settings.get(name));
	}

	public void setBoolean(String name, boolean value) {
		settings.put(name, String.valueOf(value));
	}
	
	public void setInt(String name, int value) {
		settings.put(name, String.valueOf(value));
	}
	
	public void setFloat(String name, float value) {
		settings.put(name, String.valueOf(value));
	}

	public TesselationSpacing getTesselationSpacing(String name) {
		return TesselationSpacing.valueOf(settings.get(name));
	}
}
