/**
 * 
 */
package de.kjEngine.graphics;

import de.kjEngine.math.Vec4;

/**
 * @author konst
 *
 */
public interface Texture2DDataProvider {

	public void get(int x, int y, Vec4 target);
}
