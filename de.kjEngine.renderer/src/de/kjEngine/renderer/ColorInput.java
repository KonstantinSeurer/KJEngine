/**
 * 
 */
package de.kjEngine.renderer;

import java.util.Set;

import org.json.JSONObject;

import de.kjEngine.graphics.CommandBuffer;
import de.kjEngine.graphics.Graphics;
import de.kjEngine.graphics.Texture2D;
import de.kjEngine.graphics.Texture2DDataProvider;
import de.kjEngine.io.serilization.Serializable;
import de.kjEngine.math.Vec4;
import de.kjEngine.renderer.Renderable.RenderImplementation.ID;

/**
 * @author konst
 *
 */
public class ColorInput extends Stage implements Serializable {

	public static final String OUTPUT_TEXTURE_COLOR_NAME = "color";

	private Texture2D color = Graphics.createTexture2D(0f, 0f, 0f, 1f);

	/**
	 * @param inputProvider
	 */
	public ColorInput(InputProvider inputProvider) {
		super(inputProvider);
	}

	@Override
	public void dispose() {
	}

	@Override
	protected void renderImplementation(RenderList renderList, CommandBuffer cb) {
	}

	@Override
	protected void linkImplementation() {
		output.put(OUTPUT_TEXTURE_COLOR_NAME, color);
	}

	public void setColor(float r, float g, float b, float a) {
		color.setData(new Texture2DDataProvider() {

			@Override
			public void get(int x, int y, Vec4 target) {
				target.set(r, g, b, a);
			}
		});
	}

	@Override
	public void deserialize(JSONObject obj) {
		float r = 0f, g = 0f, b = 0f, a = 1f;
		if (obj.has("r")) {
			r = obj.getFloat("r");
		}
		if (obj.has("g")) {
			g = obj.getFloat("g");
		}
		if (obj.has("b")) {
			b = obj.getFloat("b");
		}
		if (obj.has("a")) {
			a = obj.getFloat("a");
		}
		setColor(r, g, b, a);
	}

	@Override
	public JSONObject serialize() {
		return null;
	}

	@Override
	protected void updateDescriptorsImplementation() {
	}

	@Override
	public void getRequiredRenderImplementations(Set<ID> target) {
	}

	@Override
	protected void resizeImplementation(int width, int height) {
	}
}
