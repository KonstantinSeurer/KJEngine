/**
 * 
 */
package de.kjEngine.graphics;

import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

/**
 * @author konst
 *
 */
public interface AnySamplesPassedQuery extends Query {
	
	public static class BooleanPointer {
		public IntBuffer pointer = BufferUtils.createIntBuffer(1);
		
		public boolean get() {
			return pointer.get(0) != 0;
		}
	}

	public void haveAnySamplesPassed(CommandBuffer cb, BooleanPointer pResult);
}
