/**
 * 
 */
package de.kjEngine.scene.ocean;

import de.kjEngine.graphics.Texture2D;
import de.kjEngine.scene.SceneComponent;

/**
 * @author konst
 *
 */
public abstract class OceanHeightMap extends SceneComponent<SceneComponent<?, ?>, OceanHeightMap> {
	
	public OceanHeightMap() {
		super(LATE);
	}
	
	public abstract Texture2D getDx();
	public abstract Texture2D getDy();
	public abstract Texture2D getDz();
}
