/**
 * 
 */
package de.kjEngine.graphics;

import java.nio.IntBuffer;
import java.nio.LongBuffer;

import de.kjEngine.util.Disposable;

/**
 * @author konst
 *
 */
public interface Query extends Disposable {

	public static enum Type {
		NUM_SAMPLES_PASSED, ANY_SAMPLES_PASSED
	}

	public void getResult32(CommandBuffer cb, IntBuffer pResult);

	public void getResult64(CommandBuffer cb, LongBuffer pResult);

	public Type getType();
}
