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
public class RockBuilder {

	public static interface RadiusFunction {

		public float getRadius(Vec3 p);
	}

	public static Mesh create(int resolution, RadiusFunction f) {
		MeshBuilder mb = new MeshBuilder();

		float co = Real.TWO_PI / resolution;

		for (int y = 0; y < resolution; y++) {
			for (int x = 0; x < resolution; x++) {
				float rx = Real.map(x, 0, resolution - 1, -Real.PI, Real.PI);
				float ry = Real.map(y, 0, resolution - 1, -Real.PI, Real.PI) * 0.5f;

				float cos_rx = Real.cos(rx);
				float cos_ry = Real.cos(ry);
				float sin_rx = Real.sin(rx);
				float sin_ry = Real.sin(ry);

				float xo = co;

				float yo = co;
				if (y == resolution - 1) {
					yo *= -1f;
				}

				Vec3 p = Vec3.create(cos_rx * cos_ry, sin_ry, sin_rx * cos_ry);
				p.mul(f.getRadius(p));

				Vec3 a = Vec3.create(Real.cos(rx + xo) * cos_ry, sin_ry, Real.sin(rx + xo) * cos_ry);
				a.mul(f.getRadius(a));

				Vec3 b = Vec3.create(cos_rx * Real.cos(ry + yo), Real.sin(ry + yo), sin_rx * Real.cos(ry + yo));
				b.mul(f.getRadius(b));

				Vec3 n = Vec3.cross(b.sub(p), a.sub(p), null).normalise();

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
