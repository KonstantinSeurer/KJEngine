/**
 * 
 */
package de.kjEngine.graphics.khronos;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import de.kjEngine.graphics.ArrayAccessor;
import de.kjEngine.graphics.StructAccessor;
import de.kjEngine.graphics.shader.StructSource;
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
public class Std140ArrayAccessor implements ArrayAccessor {
	
	private int length;
	private int alignedOffset;
	private int size;
	private int elementSize;
	private ByteBuffer data;
	private List<Std140StructAccessor> structs = new ArrayList<>();

	public Std140ArrayAccessor(int baseOffset, String type, int length, List<StructSource> definedStructs) {
		this.length = length;
		elementSize = LayoutUtil.align(LayoutUtil.getStd140Size(type, definedStructs), 16);
		alignedOffset = LayoutUtil.align(baseOffset, elementSize);
		size = elementSize * length;
		
		if (!LayoutUtil.isPrimitive(type)) {
			for (int i = 0; i < length; i++) {
				structs.add(new Std140StructAccessor(baseOffset + elementSize * i, type, definedStructs));
			}
		}
	}

	@Override
	public StructAccessor getStruct(int i) {
		return structs.get(i);
	}

	@Override
	public int getLength() {
		return length;
	}

	/**
	 * @return the alignedOffset
	 */
	public int getAlignedOffset() {
		return alignedOffset;
	}

	/**
	 * @return the size
	 */
	public int getSize() {
		return size;
	}

	@Override
	public void set(int index, float v) {
		data.putFloat(alignedOffset + elementSize * index, v);
	}

	@Override
	public void set(int index, Vec2 v) {
		LayoutUtil.store(v, data, alignedOffset + elementSize * index);
	}

	@Override
	public void set(int index, Vec3 v) {
		LayoutUtil.store(v, data, alignedOffset + elementSize * index);
	}

	@Override
	public void set(int index, Vec4 v) {
		LayoutUtil.store(v, data, alignedOffset + elementSize * index);
	}

	@Override
	public void set(int index, Mat2 v) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void set(int index, Mat3 v) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void set(int index, Mat4 v) {
		LayoutUtil.store(v, data, alignedOffset + elementSize * index);
	}

	public void setData(ByteBuffer data) {
		this.data = data;
		for (Std140StructAccessor struct : structs) {
			struct.setData(data);
		}
	}

	@Override
	public void seti(int index, int v) {
		data.putInt(alignedOffset + elementSize * index, v);
	}
}
