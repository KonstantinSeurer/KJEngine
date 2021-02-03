/**
 * 
 */
package de.kjEngine.graphics;

import java.util.List;

import de.kjEngine.graphics.shader.BufferSource;
import de.kjEngine.graphics.shader.StructSource;

/**
 * @author konst
 *
 */
public abstract class ShaderBuffer implements Descriptor {

	public static final int FLAG_NONE = 0;
	public static final int FLAG_DEVICE_LOCAL = 1;

	protected BufferSource source;
	protected List<StructSource> definedStructs;
	protected int flags;

	public ShaderBuffer(BufferSource source, List<StructSource> definedStructs, int flags) {
		this.source = source;
		this.definedStructs = definedStructs;
		this.flags = flags;
	}

	public BufferSource getSource() {
		return source;
	}

	/**
	 * @return the definedStructs
	 */
	public List<StructSource> getDefinedStructs() {
		return definedStructs;
	}

	public int getFlags() {
		return flags;
	}

	public abstract BufferAccessor getAccessor();
	
	public abstract void update();
}
