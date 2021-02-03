/**
 * 
 */
package de.kjEngine.scene.model;

import de.kjEngine.scene.model.Model.RenderImplementation;

/**
 * @author konst
 *
 */
public class ModelEntityMap implements RenderImplementation {
	
	public long renderPassId;
	public int index;

	public ModelEntityMap() {
	}

	@Override
	public void dispose() {
	}
	
	@Override
	public void init(Mesh mesh, float[] jointIds, float[] jointWeights) {
	}

	@Override
	public RenderImplementation deepCopy() {
		return new ModelEntityMap();
	}

	@Override
	public RenderImplementation shallowCopy() {
		return new ModelEntityMap();
	}
}
