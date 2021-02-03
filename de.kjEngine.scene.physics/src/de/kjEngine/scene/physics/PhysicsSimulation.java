/**
 * 
 */
package de.kjEngine.scene.physics;

import java.util.ArrayList;
import java.util.List;

import de.kjEngine.component.Container.Implementation;
import de.kjEngine.math.Vec3;
import de.kjEngine.scene.Entity;
import de.kjEngine.scene.Scene;
import de.kjEngine.scene.physics.collission.Collider;
import de.kjEngine.scene.physics.collission.Collission;
import de.kjEngine.scene.physics.collission.CollissionSolver;
import de.kjEngine.scene.physics.solver.Solver;
import de.kjEngine.util.Timer;
import de.kjEngine.util.container.Array;

/**
 * @author konst
 *
 */
public class PhysicsSimulation implements Implementation<Scene> {

	private final Array<PhysicsObject> objects = new Array<>();
	private final Array<Collission> collissions = new Array<>();
	public final List<Solver> solvers = new ArrayList<>();
	public int subSteps = 1;

	public PhysicsSimulation() {
	}

	@Override
	public void updateEarly(Scene scene, float delta) {
		objects.clear(false);
		getObjects(scene.staticRoot);
		getObjects(scene.dynamicRoot);

		collissions.clear(false);

		Collission currentCollission = new Collission();
		
		Vec3 aMin = Vec3.create();
		Vec3 bMin = Vec3.create();
		Vec3 aMax = Vec3.create();
		Vec3 bMax = Vec3.create();
		
		Timer.start();

		for (int i = 0; i < objects.length(); i++) {
			PhysicsObject a = objects.get(i);

			a.accelerate(Vec3.create(0f, -delta * 9.81f, 0f));
			
			a.collider.getBounds(aMin, aMax);

			for (int j = i + 1; j < objects.length(); j++) {
				PhysicsObject b = objects.get(j);
				
				b.collider.getBounds(bMin, bMax);
				
				if (aMax.x < bMin.x || aMax.y < bMin.y ||aMax.z < bMin.z || bMax.x < aMin.x || bMax.y < aMin.y ||bMax.z < aMin.z) {
					continue;
				}

				@SuppressWarnings("unchecked")
				CollissionSolver<Collider, Collider> solver = (CollissionSolver<Collider, Collider>) Collider.Registry.getSolver(a.collider.type, b.collider.type);
				if (solver.getCollission(a.collider, b.collider, currentCollission)) {
					collissions.add(currentCollission);
					currentCollission = new Collission();
				}
			}
		}

		for (int i = 0; i < solvers.size(); i++) {
			solvers.get(i).solve(collissions);
		}
		
		Timer.printPassed();
	}

	private final Array<PhysicsComponent> componentBuffer = new Array<>();

	private void getObjects(Entity entity) {
		componentBuffer.clear(false);
		entity.getAll(PhysicsComponent.class, componentBuffer);
		for (int i = 0; i < componentBuffer.length(); i++) {
			objects.add(componentBuffer.get(i).object);
		}

		List<Entity> entities = entity.getAll(Entity.class);
		for (int i = 0; i < entities.size(); i++) {
			getObjects(entities.get(i));
		}
	}

	@Override
	public void updateLate(Scene container, float delta) {
	}
}
