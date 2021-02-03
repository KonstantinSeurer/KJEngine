/**
 * 
 */
package de.kjEngine.scene.physics.solver;

import de.kjEngine.scene.physics.collission.Collission;
import de.kjEngine.util.container.Array;

/**
 * @author konst
 *
 */
public interface Solver {

	public void solve(Array<Collission> collissions);
}
