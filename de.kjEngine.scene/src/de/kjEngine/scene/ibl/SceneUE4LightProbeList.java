/**
 * 
 */
package de.kjEngine.scene.ibl;

import java.util.ArrayList;
import java.util.List;

import de.kjEngine.component.Container.RenderImplementation;
import de.kjEngine.scene.Scene;

/**
 * @author konst
 *
 */
public class SceneUE4LightProbeList implements RenderImplementation<Scene> {
	
	public final List<UE4LightProbeComponent> staticLightProbes = new ArrayList<>();
	public final List<UE4LightProbeComponent> dynamicLightProbes = new ArrayList<>();
	public final List<UE4LightProbeComponent> lightProbes = new ArrayList<>();

	public SceneUE4LightProbeList() {
	}

	@Override
	public void dispose() {
	}

	@Override
	public void updateDescriptors(Scene scene) {
		lightProbes.clear();
		dynamicLightProbes.clear();
	}

	@Override
	public void render(Scene scene) {
		lightProbes.addAll(staticLightProbes);
		lightProbes.addAll(dynamicLightProbes);
	}
}
