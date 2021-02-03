/**
 * 
 */
package de.kjEngine.scene.model.procedual;

import de.kjEngine.math.Real;
import de.kjEngine.math.Vec2;
import de.kjEngine.math.Vec3;
import de.kjEngine.scene.model.Mesh;

/**
 * @author konst
 *
 */
public class SphereBuilder {

	public static Mesh create(int resolution, float radius) {
		MeshBuilder mb = new MeshBuilder();

		for (int y = 0; y < resolution; y++) {
			for (int x = 0; x < resolution; x++) {
				float rx = Real.map(x, 0, resolution - 1, -Real.PI, Real.PI);
				float ry = Real.map(y, 0, resolution - 1, -Real.PI, Real.PI) * 0.5f;

				float cos_rx = Real.cos(rx);
				float cos_ry = Real.cos(ry);
				float sin_rx = Real.sin(rx);
				float sin_ry = Real.sin(ry);

				Vec3 p = Vec3.create(cos_rx * cos_ry, sin_ry, sin_rx * cos_ry).mul(radius);
				Vec3 n = Vec3.create(p).normalise();

				mb.appendVertex(p, Vec2.create((float) x / (float) resolution, (float) y / (float) resolution), n);
			}
		}

		for (int y = 0; y < resolution - 1; y++) {
			for (int x = 0; x < resolution; x++) {
				int i0 = x + y * resolution;
				int i1 = x + (y + 1) * resolution;
				int i2 = ((x + 1) % resolution) + (y + 1) * resolution;
				int i3 = ((x + 1) % resolution) + y * resolution;
				mb.appendTriangle(i0, i1, i2);
				mb.appendTriangle(i2, i3, i0);
			}
		}

		return mb.toMesh();
	}
}
