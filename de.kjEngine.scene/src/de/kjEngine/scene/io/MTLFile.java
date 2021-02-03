/**
 * 
 */
package de.kjEngine.scene.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import de.kjEngine.graphics.Color;
import de.kjEngine.graphics.Graphics;
import de.kjEngine.graphics.SamplingMode;
import de.kjEngine.graphics.WrappingMode;
import de.kjEngine.io.RL;
import de.kjEngine.io.ResourceNotFoundException;
import de.kjEngine.io.UnknownProtocolException;
import de.kjEngine.math.Vec3;
import de.kjEngine.scene.material.PbrMaterial;

/**
 * @author konst
 *
 */
public class MTLFile {

	public static MTLFile load(RL rl) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(rl.openInputStream()));
		} catch (UnknownProtocolException e1) {
			e1.printStackTrace();
		} catch (ResourceNotFoundException e1) {
			e1.printStackTrace();
		}

		MTLFile result = new MTLFile();

		try {
			String line = reader.readLine();

			while (line != null) {
				line = line.trim();
				if (!acceptLine(line)) {
					line = reader.readLine();
					continue;
				}
				if (line.startsWith("newmtl")) {
					String mtl_name = line.split(" ")[1];
					float Ns = 1f;
					Vec3 Ka = Vec3.create(), Kd = Vec3.create(), Ks = Vec3.create();
					String map_Ka = null, map_Kd = null, map_Ns = null, map_Disp = null, map_bump = null;
					while ((line = reader.readLine()) != null) {
						if (!acceptLine(line)) {
							continue;
						}
						if (line.startsWith("Ka")) {
							String[] pts = line.split(" ");
							Ka.x = Float.parseFloat(pts[1]);
							Ka.y = Float.parseFloat(pts[2]);
							Ka.z = Float.parseFloat(pts[3]);
						} else if (line.startsWith("Kd")) {
							String[] pts = line.split(" ");
							Kd.x = Float.parseFloat(pts[1]);
							Kd.y = Float.parseFloat(pts[2]);
							Kd.z = Float.parseFloat(pts[3]);
						} else if (line.startsWith("Ks")) {
							String[] pts = line.split(" ");
							Ks.x = Float.parseFloat(pts[1]);
							Ks.y = Float.parseFloat(pts[2]);
							Ks.z = Float.parseFloat(pts[3]);
						} else if (line.startsWith("Ns")) {
							String[] pts = line.split(" ");
							Ns = Float.parseFloat(pts[1]);
						} else if (line.startsWith("map_Ka")) {
							map_Ka = line.split(" ")[1];
						} else if (line.startsWith("map_Kd")) {
							map_Kd = line.split(" ")[1];
						} else if (line.startsWith("map_Ns")) {
							map_Ns = line.split(" ")[1];
						} else if (line.startsWith("map_Disp")) {
							map_Disp = line.split(" ")[1];
						} else if (line.startsWith("map_bump")) {
							map_bump = line.split(" ")[1];
						} else if (line.startsWith("newmtl")) {
							break;
						}
					}
//					System.out.println(mtl_name);
//					System.out.println("ns " + Ns);
//					System.out.println("ka " + Ka);
//					System.out.println("kd " + Kd);
//					System.out.println("ks " + Ks);
//					System.out.println("map_Ka " + map_Ka);
//					System.out.println("map_Kd " + map_Kd);
//					System.out.println("map_Ks " + map_Ks);
//					System.out.println("map_Disp " + map_Disp);

					PbrMaterial mat = new PbrMaterial(true, Color.PURPLE);
					if (map_Kd != null) {
						mat.setAlbedo(Graphics.loadTexture(rl.getParent().getChild(map_Kd), SamplingMode.LINEAR_LINEAR, WrappingMode.REPEAT, true));
					} else {
						mat.setAlbedo(new Color(Kd.x, Kd.y, Kd.z, 1f).getTexture());
					}
					if (map_bump != null) {
						mat.setNormal(Graphics.loadTexture(rl.getParent().getChild(map_bump), SamplingMode.LINEAR_LINEAR, WrappingMode.REPEAT, true));
					}
					if (map_Disp != null) {
						mat.setDisplacement(Graphics.loadTexture(rl.getParent().getChild(map_Disp), SamplingMode.LINEAR_LINEAR, WrappingMode.REPEAT, true));
					}
					if (map_Ka != null) {
						mat.setMetalness(Graphics.loadTexture(rl.getParent().getChild(map_Ka), SamplingMode.LINEAR_LINEAR, WrappingMode.REPEAT, true));
					}
					if (map_Ns != null) {
						mat.setRoughness(Graphics.loadTexture(rl.getParent().getChild(map_Ns), SamplingMode.LINEAR_LINEAR, WrappingMode.REPEAT, true));
					} else {
						mat.setRoughness(Ns);
					}
					MaterialEntry me = new MaterialEntry();
					me.name = mtl_name;
					me.diffuse = map_Kd;
					me.normalmap = map_bump;
					me.metalness = map_Ka;
					me.roughness = map_Ns;
					me.material = mat;
					result.materials.put(mtl_name, me);
				} else {
					line = reader.readLine();
				}
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	private static boolean acceptLine(String line) {
		return !line.isEmpty() && !line.startsWith("#");
	}

	public static class MaterialEntry {
		public String name;
		public String diffuse, metalness, roughness, normalmap;
		public PbrMaterial material;
	}

	private Map<String, MaterialEntry> materials = new HashMap<>();

	public MTLFile() {
	}

	public Map<String, MaterialEntry> getMaterials() {
		return materials;
	}

	public static void write(MTLFile data, RL rl) {
		try {
			BufferedWriter w = new BufferedWriter(new OutputStreamWriter(rl.openOutputStream()));
			for (MTLFile.MaterialEntry e : data.getMaterials().values()) {
				w.write("newmtl " + e.name + "\n");
				w.write("map_Kd " + e.diffuse + "\n");
				w.write("map_bump " + e.normalmap + "\n");
				w.write("map_Ka " + e.metalness + "\n");
				w.write("map_Ns " + e.roughness + "\n");
				w.write("\n");
			}
			w.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
