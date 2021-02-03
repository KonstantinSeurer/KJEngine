/**
 * 
 */
package de.kjEngine.graphics;

import de.kjEngine.math.Mat2;
import de.kjEngine.math.Mat3;
import de.kjEngine.math.Mat4;
import de.kjEngine.math.Vec2;
import de.kjEngine.math.Vec3;
import de.kjEngine.math.Vec4;

/**
 * @author konst
 *
 */
public interface ArrayAccessor {

	public void set(int index, float v);
	public void set(int index, Vec2 v);
	public void set(int index, Vec3 v);
	public void set(int index, Vec4 v);
	public void set(int index, Mat2 v);
	public void set(int index, Mat3 v);
	public void set(int index, Mat4 v);
	
	public void seti(int index, int v);
	
	public StructAccessor getStruct(int i);
	public int getLength();
}
