/**
 * 
 */
package de.kjEngine.scene.animation;

import org.json.JSONArray;
import org.json.JSONObject;

import de.kjEngine.io.RL;
import de.kjEngine.io.ResourceManager;
import de.kjEngine.io.serilization.Serializable;
import de.kjEngine.io.serilization.Serializer;
import de.kjEngine.math.Transform;

/**
 * @author konst
 *
 */
public class TransformAnimation implements Serializable {

	public Transform[] transforms;
	public float[] frameLengths;

	public TransformAnimation() {
	}

	/**
	 * @param delay     in nano seconds
	 * @param materials
	 */
	public TransformAnimation(Transform[] transforms, float[] frameLengths) {
		this.transforms = transforms;
		this.frameLengths = frameLengths;
	}

	@Override
	public void deserialize(JSONObject obj) {
		if (obj.has("file")) {
			Serializer.deserialize(new JSONObject(ResourceManager.loadTextResource(RL.create(obj.getString("file")), true)), this);
		} else if (obj.has("frames")) {
			JSONArray frames = obj.getJSONArray("frames");
			transforms = new Transform[frames.length()];
			frameLengths = new float[frames.length()];
			for (int i = 0; i < frames.length(); i++) {
				JSONObject frame = frames.getJSONObject(i);
				
				Transform t = new Transform();
				t.deserialize(frame.getJSONObject("transform"));
				transforms[i] = t;
				
				frameLengths[i] = frame.getFloat("length");
			}
		}
	}

	@Override
	public JSONObject serialize() {
		return null;
	}
}
