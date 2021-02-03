/**
 * 
 */
package de.kjEngine.scene.animation;

import org.json.JSONArray;
import org.json.JSONObject;

import de.kjEngine.io.RL;
import de.kjEngine.io.ResourceManager;
import de.kjEngine.io.serilization.Serializable;
import de.kjEngine.io.serilization.Serialize;
import de.kjEngine.io.serilization.Serializer;
import de.kjEngine.scene.material.Material;
import de.kjEngine.scene.material.PbrMaterial;

/**
 * @author konst
 *
 */
public class MaterialAnimation implements Serializable {

	@Serialize
	public long delay;

	public Material[] materials;

	public MaterialAnimation() {
	}

	/**
	 * @param delay     in nano seconds
	 * @param materials
	 */
	public MaterialAnimation(long delay, Material[] materials) {
		this.delay = delay;
		this.materials = materials;
	}

	@Override
	public void deserialize(JSONObject obj) {
		if (obj.has("file")) {
			Serializer.deserialize(new JSONObject(ResourceManager.loadTextResource(RL.create(obj.getString("file")), true)), this);
		} else if (obj.has("frames")) {
			JSONArray frames = obj.getJSONArray("frames");
			materials = new Material[frames.length()];
			for (int i = 0; i < frames.length(); i++) {
				PbrMaterial frame = new PbrMaterial(true);
				frame.deserialize(frames.getJSONObject(i));
				materials[i] = frame;
			}
		}
	}

	@Override
	public JSONObject serialize() {
		return null;
	}
}
