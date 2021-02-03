/**
 * 
 */
package de.kjEngine.graphics;

import de.kjEngine.graphics.shader.DescriptorSetSource;
import de.kjEngine.util.Disposable;

/**
 * @author konst
 *
 */
public abstract class DescriptorSet implements Disposable {

	public final DescriptorSetSource source;

	protected DescriptorSet(DescriptorSetSource source) {
		this.source = source;
	}

	public final void set(String name, Descriptor d) {
		set(name, 0, d);
	}
	
	public abstract void set(String name, int index, Descriptor d);
	
	public abstract Descriptor get(String name);
	
	public abstract void update();
}
