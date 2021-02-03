package de.kjEngine.scene.material;

import org.json.JSONObject;

import de.kjEngine.graphics.Color;
import de.kjEngine.graphics.DescriptorSet;
import de.kjEngine.graphics.Graphics;
import de.kjEngine.graphics.SamplingMode;
import de.kjEngine.graphics.Texture2D;
import de.kjEngine.graphics.Texture2DData;
import de.kjEngine.graphics.Texture2DDataProvider;
import de.kjEngine.graphics.TextureFormat;
import de.kjEngine.graphics.WrappingMode;
import de.kjEngine.graphics.shader.DescriptorSetSource;
import de.kjEngine.graphics.shader.TextureSource;
import de.kjEngine.io.RL;
import de.kjEngine.math.Vec3;
import de.kjEngine.math.Vec4;
import de.kjEngine.scene.io.MTLFile;

public class PbrMaterial extends Material {

	public static final DescriptorSetSource DESCRIPTOR_SET_SOURCE = new DescriptorSetSource();
	static {
		DESCRIPTOR_SET_SOURCE.setName("material");
		DESCRIPTOR_SET_SOURCE.getDescriptors().add(new TextureSource("albedo", 2));
		DESCRIPTOR_SET_SOURCE.getDescriptors().add(new TextureSource("roughness", 2));
		DESCRIPTOR_SET_SOURCE.getDescriptors().add(new TextureSource("metalness", 2));
		DESCRIPTOR_SET_SOURCE.getDescriptors().add(new TextureSource("subsurface", 2));
		DESCRIPTOR_SET_SOURCE.getDescriptors().add(new TextureSource("emission", 2));
		DESCRIPTOR_SET_SOURCE.getDescriptors().add(new TextureSource("normal", 2));
		DESCRIPTOR_SET_SOURCE.getDescriptors().add(new TextureSource("displacement", 2));
		DESCRIPTOR_SET_SOURCE.getDescriptors().add(new TextureSource("ao", 2));
	}

	private static PbrMaterial nullMaterial;

	public static PbrMaterial getNullMaterial() {
		if (nullMaterial == null) { // lol
			nullMaterial = new PbrMaterial(true);
		}
		return nullMaterial;
	}

	private static final Texture2D DEFAULT_NORMAL_MAP = Graphics.createTexture2D(0.5f, 0.5f, 1f, 1f);

	private Texture2D albedo;
	private Texture2D roughness;
	private Texture2D metalness;
	private Texture2D subsurface;
	private Texture2D emission;
	private Texture2D normal;
	private Texture2D displacement;
	private Texture2D ao;

	private DescriptorSet descriptorSet;

	private boolean opaque;

	public PbrMaterial() {
		this(true);
	}

	public PbrMaterial(boolean opaque) {
		this.opaque = opaque;
		descriptorSet = Graphics.createDescriptorSet(DESCRIPTOR_SET_SOURCE);
		setAlbedo(Color.GRAY_07.getTexture());
		setRoughness(1f);
		setMetalness(0f);
		setSubsurface(Color.BLACK);
		setEmission(Color.BLACK);
		setNormal(DEFAULT_NORMAL_MAP);
		setDisplacement(Color.BLACK.getTexture());
		setAo(Color.WHITE);
	}

	public PbrMaterial(boolean opaque, Color albedo) {
		this(opaque, albedo.getTexture());
	}

	public PbrMaterial(Color albedo) {
		this(albedo.getTexture());
	}

	public PbrMaterial(boolean opaque, Texture2D albedo) {
		this(opaque);
		setAlbedo(albedo);
	}

	public PbrMaterial(Texture2D albedo) {
		this();
		setAlbedo(albedo);
	}

	public Texture2D getAlbedo() {
		return albedo;
	}

	public PbrMaterial setAlbedo(Color albedo) {
		return setAlbedo(albedo.getTexture());
	}

	public PbrMaterial setAlbedo(Texture2D albedo) {
		this.albedo = albedo;
		descriptorSet.set("albedo", albedo);
		update();
		return this;
	}

	public Texture2D getAo() {
		return ao;
	}

	public PbrMaterial setAo(Color ao) {
		return setAo(ao.getTexture());
	}

	public PbrMaterial setAo(Texture2D ao) {
		this.ao = ao;
		descriptorSet.set("ao", ao);
		update();
		return this;
	}

	public Texture2D getRoughness() {
		return roughness;
	}

	public PbrMaterial setRoughness(Texture2D roughness) {
		this.roughness = roughness;
		descriptorSet.set("roughness", roughness);
		update();
		return this;
	}

	public PbrMaterial setRoughness(float roughness) {
		setRoughness(Graphics.createTexture2D(roughness, 0f, 0f, 1f));
		return this;
	}

	public Texture2D getMetalness() {
		return metalness;
	}

	public PbrMaterial setMetalness(Texture2D metalness) {
		this.metalness = metalness;
		descriptorSet.set("metalness", metalness);
		update();
		return this;
	}

	public PbrMaterial setMetalness(float metalness) {
		setMetalness(Graphics.createTexture2D(metalness, 0f, 0f, 1f));
		return this;
	}

	public Texture2D getSubsurface() {
		return subsurface;
	}

	public PbrMaterial setSubsurface(Texture2D subsurface) {
		this.subsurface = subsurface;
		descriptorSet.set("subsurface", subsurface);
		update();
		return this;
	}

	public PbrMaterial setSubsurface(Color subsurface) {
		return setSubsurface(subsurface.getTexture());
	}

	public PbrMaterial setSubsurface(Vec3 subsurface) {
		return setSubsurface(Graphics.createTexture2D(subsurface.x, subsurface.y, subsurface.z, 1f));
	}

	public Texture2D getEmission() {
		return emission;
	}

	public PbrMaterial setEmission(Texture2D emission) {
		this.emission = emission;
		descriptorSet.set("emission", emission);
		update();
		return this;
	}

	public PbrMaterial setEmission(Color emission) {
		return setEmission(emission.getTexture());
	}

	public PbrMaterial setEmission(Vec3 emission) {
		return setEmission(Graphics.createTexture2D(new Texture2DData(1, 1, 1, new Texture2DDataProvider() {

			@Override
			public void get(int x, int y, Vec4 target) {
				target.set(emission);
			}
		}, TextureFormat.RGB32F, SamplingMode.NEAREST, WrappingMode.CLAMP)));
	}

	public Texture2D getNormal() {
		return normal;
	}

	public PbrMaterial setNormal(Texture2D normalMap) {
		this.normal = normalMap;
		descriptorSet.set("normal", normalMap);
		update();
		return this;
	}

	public Texture2D getDisplacement() {
		return displacement;
	}

	public PbrMaterial setDisplacement(Texture2D dispMap) {
		this.displacement = dispMap;
		descriptorSet.set("displacement", dispMap);
		update();
		return this;
	}

	private void update() {
		descriptorSet.update();
		updateGPUImplementations();
	}

	private void updateGPUImplementations() {
		if (renderImplementations != null) {
			for (int i = 0; i < renderImplementations.length; i++) {
				renderImplementations[i].update(this);
			}
		}
	}

	public DescriptorSet getDescriptorSet() {
		return descriptorSet;
	}

	/**
	 * @return the opaque
	 */
	public boolean isOpaque() {
		return opaque;
	}

	/**
	 * @param opaque the opaque to set
	 */
	public void setOpaque(boolean opaque) {
		this.opaque = opaque;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((albedo == null) ? 0 : albedo.hashCode());
		result = prime * result + ((displacement == null) ? 0 : displacement.hashCode());
		result = prime * result + ((metalness == null) ? 0 : metalness.hashCode());
		result = prime * result + ((normal == null) ? 0 : normal.hashCode());
		result = prime * result + ((roughness == null) ? 0 : roughness.hashCode());
		result = prime * result + ((subsurface == null) ? 0 : subsurface.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof PbrMaterial))
			return false;
		PbrMaterial other = (PbrMaterial) obj;
		if (albedo == null) {
			if (other.albedo != null)
				return false;
		} else if (!albedo.equals(other.albedo))
			return false;
		if (displacement == null) {
			if (other.displacement != null)
				return false;
		} else if (!displacement.equals(other.displacement))
			return false;
		if (metalness == null) {
			if (other.metalness != null)
				return false;
		} else if (!metalness.equals(other.metalness))
			return false;
		if (normal == null) {
			if (other.normal != null)
				return false;
		} else if (!normal.equals(other.normal))
			return false;
		if (roughness == null) {
			if (other.roughness != null)
				return false;
		} else if (!roughness.equals(other.roughness))
			return false;
		if (subsurface == null) {
			if (other.subsurface != null)
				return false;
		} else if (!subsurface.equals(other.subsurface))
			return false;
		return true;
	}

	@Override
	public void deserialize(JSONObject obj) {
		if (obj.has("file") && obj.has("material")) {
			MTLFile mtl = MTLFile.load(new RL("jar", "", obj.getString("file")));
			MTLFile.MaterialEntry entry = mtl.getMaterials().get(obj.getString("material"));
			setAlbedo(entry.material.albedo);
			setRoughness(entry.material.roughness);
			setMetalness(entry.material.metalness);
			setSubsurface(entry.material.subsurface);
			setNormal(entry.material.normal);
			setDisplacement(entry.material.displacement);
		}

		if (obj.has("albedo")) {
			JSONObject albedo = obj.getJSONObject("albedo");
			if (albedo != null) {
				if (albedo.has("file")) {
					setAlbedo(Graphics.loadTexture(RL.create(albedo.getString("file")), SamplingMode.LINEAR_LINEAR, WrappingMode.REPEAT, true));
				} else {
					Vec4 color = Vec4.create(1f, 1f, 1f, 1f);
					if (albedo.has("r")) {
						color.x = albedo.getFloat("r");
					}
					if (albedo.has("g")) {
						color.y = albedo.getFloat("g");
					}
					if (albedo.has("b")) {
						color.z = albedo.getFloat("b");
					}
					setAlbedo(Graphics.createTexture2D(color));
				}
			}
		}

		if (obj.has("roughness")) {
			JSONObject roughness = obj.getJSONObject("roughness");
			if (roughness != null) {
				if (roughness.has("file")) {
					setRoughness(Graphics.loadTexture(RL.create(roughness.getString("file")), SamplingMode.LINEAR_LINEAR, WrappingMode.REPEAT, true));
				} else if (roughness.has("value")) {
					setRoughness(roughness.getFloat("value"));
				}
			}
		}

		if (obj.has("metalness")) {
			JSONObject metalness = obj.getJSONObject("metalness");
			if (metalness != null) {
				if (metalness.has("file")) {
					setMetalness(Graphics.loadTexture(RL.create(metalness.getString("file")), SamplingMode.LINEAR_LINEAR, WrappingMode.REPEAT, true));
				} else if (metalness.has("value")) {
					setMetalness(metalness.getFloat("value"));
				}
			}
		}
	}

	@Override
	public JSONObject serialize() {
		return null;
	}

	@Override
	public void dispose() {
		descriptorSet.dispose();
	}

	@Override
	public PbrMaterial deepCopy() {
		PbrMaterial m = new PbrMaterial(opaque);
		m.setAlbedo(albedo.deepCopy());
		m.setRoughness(roughness.deepCopy());
		m.setMetalness(metalness.deepCopy());
		m.setSubsurface(subsurface.deepCopy());
		m.setEmission(emission.deepCopy());
		m.setNormal(normal.deepCopy());
		m.setDisplacement(displacement.deepCopy());
		return m;
	}

	@Override
	public PbrMaterial shallowCopy() {
		PbrMaterial m = new PbrMaterial(opaque);
		m.setAlbedo(albedo);
		m.setRoughness(roughness);
		m.setMetalness(metalness);
		m.setSubsurface(subsurface);
		m.setEmission(emission);
		m.setNormal(normal);
		m.setDisplacement(displacement);
		return m;
	}
}
