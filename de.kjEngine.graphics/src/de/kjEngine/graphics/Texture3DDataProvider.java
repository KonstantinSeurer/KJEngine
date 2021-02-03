/**
 * 
 */
package de.kjEngine.graphics;

import de.kjEngine.math.Vec4;

/**
 * @author konst
 *
 */
public interface Texture3DDataProvider {

	public void get(int x, int y, int z, Vec4 target);
}
