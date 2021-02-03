/**
 * 
 */
package de.kjEngine.scene.physics.solver;

import de.kjEngine.scene.physics.PhysicsObject;
import de.kjEngine.scene.physics.RigidBody;
import de.kjEngine.scene.physics.collission.Collission;
import de.kjEngine.util.container.Array;

/**
 * @author konst
 *
 */
public class ImpulseSolver implements Solver {

	public ImpulseSolver() {
	}

	@Override
	public void solve(Array<Collission> collissions) {
		for (int i = 0; i < collissions.length(); i++) {
			Collission c = collissions.get(i);
			PhysicsObject a = c.a.parent;
			PhysicsObject b = c.b.parent;
			if (a instanceof RigidBody) {
				((RigidBody) a).velocity.set(0f, 0f, 0f);
			}
			if (b instanceof RigidBody) {
				((RigidBody) b).velocity.set(0f, 0f, 0f);
			}
		}
	}
}
