/**
 * 
 */
package de.kjEngine.graphics;

import java.nio.IntBuffer;

/**
 * @author konst
 *
 */
public interface NumSamplesPassedQuery extends Query {

	public void getSampleCount(CommandBuffer cb, IntBuffer pResult);
}
