/**
 * 
 */
package de.kjEngine.scene.physics.solver;

import de.kjEngine.math.Vec3;
import de.kjEngine.scene.physics.PhysicsObject;
import de.kjEngine.scene.physics.collission.Collission;
import de.kjEngine.util.container.Array;

/**
 * @author konst
 *
 */
public class PositionSolver implements Solver {

	public PositionSolver() {
	}
	
	private Vec3 d = Vec3.create();

	@Override
	public void solve(Array<Collission> collissions) {
		for (int i = 0; i < collissions.length(); i++) {
			Collission c = collissions.get(i);
			d.set(c.deepB).sub(c.deepA);
			PhysicsObject a = c.a.parent;
			PhysicsObject b = c.b.parent;
			if (a.dynamic) {
				if (b.dynamic) {
					a.transform.position.add(d, 0.5f);
					b.transform.position.sub(d, 0.5f);
				} else {
					a.transform.position.add(d);
				}
			} else if (b.dynamic) {
				b.transform.position.sub(d);
			}
		}
	}
}
