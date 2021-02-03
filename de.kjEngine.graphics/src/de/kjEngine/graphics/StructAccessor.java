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
public interface StructAccessor {

	public void set(String name, float v);
	public void set(String name, Vec2 v);
	public void set(String name, Vec3 v);
	public void set(String name, Vec4 v);
	public void set(String name, Mat2 v);
	public void set(String name, Mat3 v);
	public void set(String name, Mat4 v);
	
	public void seti(String name, int v);
	
	public StructAccessor getStruct(String name);
	public ArrayAccessor getArray(String name);
}
