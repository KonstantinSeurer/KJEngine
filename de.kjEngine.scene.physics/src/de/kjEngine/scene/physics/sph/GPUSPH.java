/**
 * 
 */
package de.kjEngine.scene.physics.sph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.kjEngine.graphics.CommandBuffer;
import de.kjEngine.graphics.ComputePipeline;
import de.kjEngine.graphics.DescriptorSet;
import de.kjEngine.graphics.Graphics;
import de.kjEngine.graphics.ShaderBuffer;
import de.kjEngine.graphics.StructAccessor;
import de.kjEngine.graphics.Descriptor.Type;
import de.kjEngine.graphics.shader.BufferSource;
import de.kjEngine.graphics.shader.ConstantSource;
import de.kjEngine.graphics.shader.DescriptorSetSource;
import de.kjEngine.graphics.shader.PipelineSource;
import de.kjEngine.graphics.shader.ShaderCompilationException;
import de.kjEngine.graphics.shader.StructSource;
import de.kjEngine.graphics.shader.VariableSource;
import de.kjEngine.graphics.shader.BufferSource.Layout;
import de.kjEngine.io.RL;
import de.kjEngine.math.Real;
import de.kjEngine.util.Disposable;

/**
 * @author konst
 *
 */
public class GPUSPH implements Disposable {
	
	public static final int MAX_GRID_SIZE = 100;
	public static final int GRID_DEPTH = 30;
	public static final int MAX_PARTICLE_COUNT = 100000;
	
	public static final BufferSource SETTINGS_UBO_SOURCE = new BufferSource("settings", Type.UNIFORM_BUFFER, Layout.STANDARD, new ArrayList<>());
	static {
		SETTINGS_UBO_SOURCE.addMember("float", "boundarySize");
		SETTINGS_UBO_SOURCE.addMember("float", "invBoundarySize");
		SETTINGS_UBO_SOURCE.addMember("float", "particleRadius");
		SETTINGS_UBO_SOURCE.addMember("float", "restingDensity");
		SETTINGS_UBO_SOURCE.addMember("float", "particleMass");
		SETTINGS_UBO_SOURCE.addMember("float", "viscosity");
		SETTINGS_UBO_SOURCE.addMember("float", "gassConstant");
		SETTINGS_UBO_SOURCE.addMember("float", "maxVelocity");
		SETTINGS_UBO_SOURCE.addMember("float", "wallElasticity");
		SETTINGS_UBO_SOURCE.addMember("float", "gravity");
	}
	
	public static final StructSource PARTICLE_SOURCE = new StructSource("Particle", new ArrayList<>());
	static {
		PARTICLE_SOURCE.getMembers().add(new VariableSource("vec3", "pos"));
		PARTICLE_SOURCE.getMembers().add(new VariableSource("vec3", "vel"));
		PARTICLE_SOURCE.getMembers().add(new VariableSource("float", "density"));
	}
	
	public static final BufferSource PARTICLE_SSBO_SOURCE = new BufferSource("particles", Type.STORAGE_BUFFER, Layout.PACKED, new ArrayList<>());
	static {
		PARTICLE_SSBO_SOURCE.addMember("Particle[" + MAX_PARTICLE_COUNT + "]", "list");
		PARTICLE_SSBO_SOURCE.addMember("int[" + MAX_GRID_SIZE * MAX_GRID_SIZE * MAX_GRID_SIZE * GRID_DEPTH + "]", "grid");
		PARTICLE_SSBO_SOURCE.addMember("int[" + MAX_GRID_SIZE * MAX_GRID_SIZE * MAX_GRID_SIZE + "]", "gridParticleCount");
		PARTICLE_SSBO_SOURCE.addMember("int", "count");
		PARTICLE_SSBO_SOURCE.addMember("int", "gridSize");
	}
	
	public static final DescriptorSetSource DESCRIPTOR_SET_SOURCE = new DescriptorSetSource("data", new ArrayList<>());
	static {
		DESCRIPTOR_SET_SOURCE.getDescriptors().add(SETTINGS_UBO_SOURCE);
		DESCRIPTOR_SET_SOURCE.getDescriptors().add(PARTICLE_SSBO_SOURCE);
	}
	
	public static final PipelineSource LIBRARY_SOURCE = new PipelineSource();
	static {
		LIBRARY_SOURCE.getStructs().add(PARTICLE_SOURCE);
		LIBRARY_SOURCE.getConstants().add(new ConstantSource("int", "gridDepth", String.valueOf(GRID_DEPTH)));
	}
	
	private DescriptorSet descriptorSet;
	private ShaderBuffer settingsUbo;
	private ShaderBuffer particleSsbo;
	
	private ComputePipeline gridResetPipeline;
	private ComputePipeline gridBuildPipeline;
	private ComputePipeline densityPipeline;
	private ComputePipeline integrationPipeline;
	private CommandBuffer cb;
	
	private int particleCount;
	private int gridSize;
	private SPHSettings settings;
	
	private List<SPHParticle> addParticleQueue = new ArrayList<>();

	public GPUSPH(SPHSettings settings) {
		this.settings = settings;
		
		gridSize = (int) (settings.boundarySize / (settings.particleRadius * 2f));
		
		settingsUbo = Graphics.createUniformBuffer(SETTINGS_UBO_SOURCE, new ArrayList<>(), ShaderBuffer.FLAG_NONE);
		settingsUbo.getAccessor().set("boundarySize", settings.boundarySize);
		settingsUbo.getAccessor().set("invBoundarySize", 1f / settings.boundarySize);
		settingsUbo.getAccessor().set("particleRadius", settings.particleRadius);
		settingsUbo.getAccessor().set("restingDensity", settings.restingDensity);
		settingsUbo.getAccessor().set("particleMass", settings.particleMass);
		settingsUbo.getAccessor().set("viscosity", settings.viscosity);
		settingsUbo.getAccessor().set("gassConstant", settings.gassConstant);
		settingsUbo.getAccessor().set("maxVelocity", settings.maxVelocity);
		settingsUbo.getAccessor().set("wallElasticity", settings.wallElasticity);
		settingsUbo.getAccessor().set("gravity", settings.gravity);
		settingsUbo.update();
		
		particleSsbo = Graphics.createStorageBuffer(PARTICLE_SSBO_SOURCE, Arrays.asList(PARTICLE_SOURCE), ShaderBuffer.FLAG_NONE);
		particleSsbo.getAccessor().seti("gridSize", gridSize);
		particleSsbo.update();
		
		descriptorSet = Graphics.createDescriptorSet(DESCRIPTOR_SET_SOURCE);
		descriptorSet.set("settings", settingsUbo);
		descriptorSet.set("particles", particleSsbo);
		descriptorSet.update();
		
		cb = Graphics.createCommandBuffer(CommandBuffer.FLAG_DYNAMIC);
		
		try {
			PipelineSource source = PipelineSource.parse(RL.create("jar://engine/de/kjEngine/core/scene/physics3d/sph/gridReset.shader"));
			source.getDescriptorSets().add(DESCRIPTOR_SET_SOURCE);
			source.add(LIBRARY_SOURCE);
			gridResetPipeline = Graphics.createComputePipeline(source);
		} catch (ShaderCompilationException e) {
			e.printStackTrace();
		}
		
		try {
			PipelineSource source = PipelineSource.parse(RL.create("jar://engine/de/kjEngine/core/scene/physics3d/sph/gridBuild.shader"));
			source.getDescriptorSets().add(DESCRIPTOR_SET_SOURCE);
			source.add(LIBRARY_SOURCE);
			gridBuildPipeline = Graphics.createComputePipeline(source);
		} catch (ShaderCompilationException e) {
			e.printStackTrace();
		}
		
		try {
			PipelineSource source = PipelineSource.parse(RL.create("jar://engine/de/kjEngine/core/scene/physics3d/sph/density.shader"));
			source.getDescriptorSets().add(DESCRIPTOR_SET_SOURCE);
			source.add(LIBRARY_SOURCE);
			densityPipeline = Graphics.createComputePipeline(source);
		} catch (ShaderCompilationException e) {
			e.printStackTrace();
		}
		
		try {
			PipelineSource source = PipelineSource.parse(RL.create("jar://engine/de/kjEngine/core/scene/physics3d/sph/integration.shader"));
			source.getDescriptorSets().add(DESCRIPTOR_SET_SOURCE);
			source.add(LIBRARY_SOURCE);
			integrationPipeline = Graphics.createComputePipeline(source);
		} catch (ShaderCompilationException e) {
			e.printStackTrace();
		}
	}
	
	public void update(float timeStep, int subSteps) {
		float dt = timeStep / subSteps;
		
		cb.clear();
		
		int workGroupSize = 8 * 8 * 8;
		int invocations = particleCount / workGroupSize + 1;
		int invocationWidth = (int) Real.pow(invocations, 1f / 3f) + 1;
		
		cb.bindPipeline(gridResetPipeline);
		cb.bindDescriptorSet(descriptorSet, "data");
		cb.compute(gridSize / 8 + 1, gridSize / 8 + 1, gridSize / 8 + 1);
		
		cb.memoryBarrier();
		
		cb.bindPipeline(gridBuildPipeline);
		cb.bindDescriptorSet(descriptorSet, "data");
		cb.compute(invocationWidth, invocationWidth, invocationWidth);
		
		cb.memoryBarrier();
		
		cb.bindPipeline(densityPipeline);
		cb.bindDescriptorSet(descriptorSet, "data");
		cb.compute(invocationWidth, invocationWidth, invocationWidth);
		cb.memoryBarrier();
		
		cb.bindPipeline(integrationPipeline);
		cb.bindDescriptorSet(descriptorSet, "data");
		integrationPipeline.getUniformAccessor().set("dt", dt);
		cb.compute(invocationWidth, invocationWidth, invocationWidth);
		
		for (int s = 0; s < subSteps; s++) {
			cb.submit();
		}
	}
	
	public void updateDescriptors() {
		for (int i = 0; i < addParticleQueue.size(); i++) {
			SPHParticle particle = addParticleQueue.get(i);
			StructAccessor particleAccessor = particleSsbo.getAccessor().getArray("list").getStruct(particleCount++);
			particleAccessor.set("pos", particle.position);
			particleAccessor.set("vel", particle.velocity);
		}
		
		particleSsbo.getAccessor().seti("count", particleCount);
		
		particleSsbo.update();
		
		addParticleQueue.clear();
	}

	/**
	 * @return the descriptorSet
	 */
	public DescriptorSet getDescriptorSet() {
		return descriptorSet;
	}

	/**
	 * @return the particleCount
	 */
	public int getParticleCount() {
		return particleCount;
	}
	
	/**
	 * @return the gridSize
	 */
	public int getGridSize() {
		return gridSize;
	}

	/**
	 * @return the settings
	 */
	public SPHSettings getSettings() {
		return settings;
	}

	public void addParticle(SPHParticle particle) {
		addParticleQueue.add(particle);
	}

	@Override
	public void dispose() {
		descriptorSet.dispose();
		settingsUbo.dispose();
		particleSsbo.dispose();
		gridResetPipeline.dispose();
		gridBuildPipeline.dispose();
		densityPipeline.dispose();
		integrationPipeline.dispose();
		cb.dispose();
	}
}
