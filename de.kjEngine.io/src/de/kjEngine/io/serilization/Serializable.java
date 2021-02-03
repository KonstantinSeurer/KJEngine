/**
 * 
 */
package de.kjEngine.io.serilization;

import org.json.JSONObject;

/**
 * @author konst
 *
 */
public interface Serializable {

	public void deserialize(JSONObject obj);
	
	public JSONObject serialize();
}
