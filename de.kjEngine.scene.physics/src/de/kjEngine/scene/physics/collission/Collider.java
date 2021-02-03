/**
 * 
 */
package de.kjEngine.scene.physics.collission;

import java.util.ArrayList;
import java.util.List;

import de.kjEngine.math.Real;
import de.kjEngine.math.Vec3;
import de.kjEngine.scene.physics.PhysicsObject;

/**
 * @author konst
 *
 */
public abstract class Collider {

	public static class Registry {

		private static List<List<CollissionSolver<?, ?>>> solvers = new ArrayList<>();
		private static int currentColliderType;

		public static int getNewColliderType() {
			int result = currentColliderType;
			currentColliderType++;
			return result;
		}

		public static <A extends Collider, B extends Collider> void registerSolver(CollissionSolver<A, B> solver, int aType, int bType) {
			_registerSolver(solver, aType, bType);
			if (aType != bType) {
				_registerSolver(new CollissionSolver<B, A>() {

					@Override
					public boolean getCollission(B b, A a, Collission target) {
						boolean result = solver.getCollission(a, b, target);
						target.negate();
						return result;
					}
				}, bType, aType);
			}
		}

		private static void _registerSolver(CollissionSolver<?, ?> solver, int aType, int bType) {
			if (solvers.size() <= aType) {
				for (int i = solvers.size(); i <= aType; i++) {
					solvers.add(new ArrayList<>());
				}
			}
			List<CollissionSolver<?, ?>> solvers = Registry.solvers.get(aType);
			if (solvers.size() <= bType) {
				for (int i = solvers.size(); i <= bType; i++) {
					solvers.add(null);
				}
			}
			solvers.set(bType, solver);
		}

		public static CollissionSolver<?, ?> getSolver(int aType, int bType) {
			return solvers.get(aType).get(bType);
		}
	}

	static {
		Registry.registerSolver(new CollissionSolver<SphereCollider, SphereCollider>() {
			
			Vec3 diff = Vec3.create();

			@Override
			public boolean getCollission(SphereCollider a, SphereCollider b, Collission target) {
				float x = b.parent.transform.globalTransform.wx - a.parent.transform.globalTransform.wx;
				float y = b.parent.transform.globalTransform.wy - a.parent.transform.globalTransform.wy;
				float z = b.parent.transform.globalTransform.wz - a.parent.transform.globalTransform.wz;
				diff.set(x, y, z);
				float d2 = diff.lengthSquared();
				float r = a.radius + b.radius;
				if (d2 > r * r) {
					return false;
				}
				float d = Real.sqrt(d2);
				diff.div(d);
				target.a = a;
				target.b = b;
				target.deepA.set(a.parent.transform.getGlobalPosition()).add(diff, a.radius);
				target.deepB.set(b.parent.transform.getGlobalPosition()).sub(diff, b.radius);
				return true;
			}
		}, SphereCollider.TYPE, SphereCollider.TYPE);
		
		Registry.registerSolver(new CollissionSolver<PlaneCollider, SphereCollider>() {

			Vec3 n = Vec3.create();

			@Override
			public boolean getCollission(PlaneCollider a, SphereCollider b, Collission target) {
				float x = b.parent.transform.globalTransform.wx - a.parent.transform.globalTransform.wx;
				float y = b.parent.transform.globalTransform.wy - a.parent.transform.globalTransform.wy;
				float z = b.parent.transform.globalTransform.wz - a.parent.transform.globalTransform.wz;
				a.parent.transform.globalTransform.getY(n);
				n.normalise();
				float d = n.x * x + n.y * y + n.z * z;
				if (d > b.radius) {
					return false;
				}
				target.a = a;
				target.b = b;
				target.deepB.set(a.parent.transform.getGlobalPosition()).add(n, -b.radius);
				target.deepA.set(target.deepB).add(n, -(d - b.radius));
				return true;
			}
		}, PlaneCollider.TYPE, SphereCollider.TYPE);

		Registry.registerSolver(new CollissionSolver<PlaneCollider, PlaneCollider>() {

			@Override
			public boolean getCollission(PlaneCollider a, PlaneCollider b, Collission target) {
				return false;
			}
		}, PlaneCollider.TYPE, PlaneCollider.TYPE);
	}

	public final int type;
	public PhysicsObject parent;

	public Collider(int type) {
		this.type = type;
	}

	public abstract void getBounds(Vec3 min, Vec3 max);
}
