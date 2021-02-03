/**
 * 
 */
package de.kjEngine.graphics;

import java.nio.ByteBuffer;

/**
 * @author konst
 *
 */
public interface BufferAccessor extends StructAccessor {

	public void setData(ByteBuffer data);
	public int getSize();
}
