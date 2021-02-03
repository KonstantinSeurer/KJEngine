/**
 * 
 */
package de.kjEngine.renderer;

import java.util.Set;

import org.json.JSONObject;

import de.kjEngine.graphics.CommandBuffer;
import de.kjEngine.graphics.Graphics;
import de.kjEngine.graphics.SamplingMode;
import de.kjEngine.graphics.Texture2D;
import de.kjEngine.graphics.WrappingMode;
import de.kjEngine.io.RL;
import de.kjEngine.io.serilization.Serializable;
import de.kjEngine.renderer.Renderable.RenderImplementation.ID;

/**
 * @author konst
 *
 */
public class TextureInput extends Stage implements Serializable {

	private Texture2D texture;

	/**
	 * @param inputProvider
	 */
	public TextureInput(InputProvider inputProvider) {
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
		output.put("texture", texture);
	}

	/**
	 * @param texture the texture to set
	 */
	public void setTexture(Texture2D texture) {
		this.texture = texture;
	}

	@Override
	public void deserialize(JSONObject obj) {
		if (obj.has("rl")) {
			setTexture(Graphics.loadTexture(RL.create(obj.getString("rl")), SamplingMode.LINEAR, WrappingMode.CLAMP, true));
		}
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
