/**
 * 
 */
package de.kjEngine.io;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import de.kjEngine.io.serilization.Serializable;

/**
 * @author konst
 *
 */
public class ResourceLookup implements Serializable {
	
	public static class Entry implements Serializable {
		public RL defaultResource;
		public Map<String, RL> specificResources = new HashMap<>();
		
		public RL getFittingResource() {
			return defaultResource;
		}

		@Override
		public void deserialize(JSONObject obj) {
		}

		@Override
		public JSONObject serialize() {
			return null;
		}
	}
	
	private Map<String, Entry> entries = new HashMap<>();

	public ResourceLookup() {
	}
	
	public RL getResource(String id) {
		return entries.get(id).getFittingResource();
	}

	@Override
	public void deserialize(JSONObject obj) {
		for (String id : obj.keySet()) {
			Entry e = new Entry();
			e.deserialize(obj.getJSONObject(id));
			entries.put(id, e);
		}
	}

	@Override
	public JSONObject serialize() {
		JSONObject o = new JSONObject();
		for (String id : entries.keySet()) {
			o.append(id, entries.get(id).serialize());
		}
		return o;
	}
}
