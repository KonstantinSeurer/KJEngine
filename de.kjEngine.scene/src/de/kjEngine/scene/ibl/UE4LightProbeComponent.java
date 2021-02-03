/**
 * 
 */
package de.kjEngine.scene.ibl;

import de.kjEngine.graphics.DescriptorSet;
import de.kjEngine.graphics.Graphics;
import de.kjEngine.graphics.SamplingMode;
import de.kjEngine.graphics.Texture2D;
import de.kjEngine.graphics.Texture2DData;
import de.kjEngine.graphics.TextureFormat;
import de.kjEngine.graphics.WrappingMode;
import de.kjEngine.graphics.shader.DescriptorSetSource;
import de.kjEngine.graphics.shader.ImageSource;
import de.kjEngine.graphics.shader.TextureSource;
import de.kjEngine.scene.Entity;
import de.kjEngine.scene.SceneComponent;

/**
 * @author konst
 *
 */
public class UE4LightProbeComponent extends SceneComponent<Entity, UE4LightProbeComponent> {

	public static final DescriptorSetSource DESCRIPTOR_SET_SOURCE = new DescriptorSetSource("lightProbe");
	static {
		DESCRIPTOR_SET_SOURCE.addDescriptor(new TextureSource("diffuse", 2));
		DESCRIPTOR_SET_SOURCE.addDescriptor(new TextureSource("specular", 2));
	}
	
	public static final DescriptorSetSource CONVERSION_DESCRIPTOR_SET_SOURCE = new DescriptorSetSource("destination");
	static {
		CONVERSION_DESCRIPTOR_SET_SOURCE.addDescriptor(new ImageSource("texture", 2, TextureFormat.RGB16F, true, true));
	}
	
	public static final DescriptorSetSource DIFFUSE_GENERATOR_DESCRIPTOR_SET_SOURCE = new DescriptorSetSource("data");
	static {
		DIFFUSE_GENERATOR_DESCRIPTOR_SET_SOURCE.addDescriptor(new TextureSource("environment", 2));
		DIFFUSE_GENERATOR_DESCRIPTOR_SET_SOURCE.addDescriptor(new ImageSource("diffuse", 2, TextureFormat.RGB16F, true, true));
	}
	
	public static final DescriptorSetSource SPECULAR_GENERATOR_DESCRIPTOR_SET_SOURCE = new DescriptorSetSource("data");
	static {
		SPECULAR_GENERATOR_DESCRIPTOR_SET_SOURCE.addDescriptor(new TextureSource("environment", 2));
		SPECULAR_GENERATOR_DESCRIPTOR_SET_SOURCE.addDescriptor(new ImageSource("specular0", 2, TextureFormat.RGB16F, true, true));
		SPECULAR_GENERATOR_DESCRIPTOR_SET_SOURCE.addDescriptor(new ImageSource("specular1", 2, TextureFormat.RGB16F, true, true));
		SPECULAR_GENERATOR_DESCRIPTOR_SET_SOURCE.addDescriptor(new ImageSource("specular2", 2, TextureFormat.RGB16F, true, true));
		SPECULAR_GENERATOR_DESCRIPTOR_SET_SOURCE.addDescriptor(new ImageSource("specular3", 2, TextureFormat.RGB16F, true, true));
	}

	private Texture2D environment;
	private Texture2D diffuse, specular;
	private DescriptorSet descriptorSet;
	private DescriptorSet conversionDescriptorSet;
	private DescriptorSet diffuseGeneratorDescriptorSet, specularGeneratorDescriptorSet;
	private float minRenderDistance, maxRenderDistance;
	public boolean generated;

	public UE4LightProbeComponent(int diffuseResolution, int specularResolution, float minRenderDistance, float maxRenderDistance) {
		super(LATE);
		
		this.minRenderDistance = minRenderDistance;
		this.maxRenderDistance = maxRenderDistance;
		
		environment = Graphics.createTexture2D(new Texture2DData(specularResolution, specularResolution, 1, null, TextureFormat.RGB16F, SamplingMode.LINEAR_LINEAR, WrappingMode.REPEAT));

		diffuse = Graphics.createTexture2D(new Texture2DData(diffuseResolution * 2, diffuseResolution, 1, null, TextureFormat.RGB16F, SamplingMode.LINEAR, WrappingMode.REPEAT));
		specular = Graphics.createTexture2D(new Texture2DData(specularResolution * 2, specularResolution, 4, null, TextureFormat.RGB16F, SamplingMode.LINEAR_LINEAR, WrappingMode.REPEAT));

		descriptorSet = Graphics.createDescriptorSet(DESCRIPTOR_SET_SOURCE);
		descriptorSet.set("diffuse", diffuse);
		descriptorSet.set("specular", specular);
		descriptorSet.update();
		
		conversionDescriptorSet = Graphics.createDescriptorSet(CONVERSION_DESCRIPTOR_SET_SOURCE);
		conversionDescriptorSet.set("texture", environment.getImage(0));
		conversionDescriptorSet.update();
		
		diffuseGeneratorDescriptorSet = Graphics.createDescriptorSet(DIFFUSE_GENERATOR_DESCRIPTOR_SET_SOURCE);
		diffuseGeneratorDescriptorSet.set("environment", environment);
		diffuseGeneratorDescriptorSet.set("diffuse", diffuse.getImage(0));
		diffuseGeneratorDescriptorSet.update();
		
		specularGeneratorDescriptorSet = Graphics.createDescriptorSet(SPECULAR_GENERATOR_DESCRIPTOR_SET_SOURCE);
		specularGeneratorDescriptorSet.set("environment", environment);
		specularGeneratorDescriptorSet.set("specular0", specular.getImage(0));
		specularGeneratorDescriptorSet.set("specular1", specular.getImage(1));
		specularGeneratorDescriptorSet.set("specular2", specular.getImage(2));
		specularGeneratorDescriptorSet.set("specular3", specular.getImage(3));
		specularGeneratorDescriptorSet.update();
	}

	/**
	 * @return the descriptorSet
	 */
	public DescriptorSet getDescriptorSet() {
		return descriptorSet;
	}

	@Override
	public void init() {
		super.init();
		if (!parent.isDynamic()) {
			parent.getContainer().getRenderImplementation(SceneUE4LightProbeList.class).staticLightProbes.add(this);
		}
	}

	@Override
	public void render() {
		super.render();
		parent.getContainer().getRenderImplementation(SceneUE4LightProbeList.class).dynamicLightProbes.add(this);
	}

	@Override
	public void dispose() {
		super.dispose();
		descriptorSet.dispose();
	}

	/**
	 * @return the environment
	 */
	public Texture2D getEnvironment() {
		return environment;
	}

	/**
	 * @return the minRenderDistance
	 */
	public float getMinRenderDistance() {
		return minRenderDistance;
	}

	/**
	 * @param minRenderDistance the minRenderDistance to set
	 */
	public void setMinRenderDistance(float minRenderDistance) {
		this.minRenderDistance = minRenderDistance;
	}

	/**
	 * @return the maxRenderDistance
	 */
	public float getMaxRenderDistance() {
		return maxRenderDistance;
	}

	/**
	 * @param maxRenderDistance the maxRenderDistance to set
	 */
	public void setMaxRenderDistance(float maxRenderDistance) {
		this.maxRenderDistance = maxRenderDistance;
	}

	/**
	 * @return the conversionDescriptorSet
	 */
	public DescriptorSet getConversionDescriptorSet() {
		return conversionDescriptorSet;
	}

	/**
	 * @return the diffuse
	 */
	public Texture2D getDiffuse() {
		return diffuse;
	}

	/**
	 * @return the specular
	 */
	public Texture2D getSpecular() {
		return specular;
	}

	/**
	 * @return the diffuseGeneratorDescriptorSet
	 */
	public DescriptorSet getDiffuseGeneratorDescriptorSet() {
		return diffuseGeneratorDescriptorSet;
	}

	/**
	 * @return the specularGeneratorDescriptorSet
	 */
	public DescriptorSet getSpecularGeneratorDescriptorSet() {
		return specularGeneratorDescriptorSet;
	}

	@Override
	protected void update(float delta) {
	}
}
