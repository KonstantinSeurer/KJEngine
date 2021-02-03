/**
 * 
 */
package de.kjEngine.graphics.khronos;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.kjEngine.graphics.ArrayAccessor;
import de.kjEngine.graphics.BufferAccessor;
import de.kjEngine.graphics.StructAccessor;
import de.kjEngine.graphics.shader.StructSource;
import de.kjEngine.graphics.shader.VariableSource;
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
public class Std140ShaderBufferAccessor implements BufferAccessor {

	private Map<String, Std140StructAccessor> structs = new HashMap<>();
	private Map<String, Std140ArrayAccessor> arrays = new HashMap<>();
	private Map<String, Integer> primitives = new HashMap<>();
	private int size;
	private ByteBuffer data;

	public Std140ShaderBufferAccessor(StructSource source, List<StructSource> definedStructs) {
		int offset = 0;
		for (VariableSource member : source.getMembers()) {
			String type = member.getType().trim();
			String name = member.getName().trim();
			if (LayoutUtil.isPrimitive(type)) {
				offset = LayoutUtil.align(offset, LayoutUtil.getStd140Alignment(type, definedStructs));
				primitives.put(name, offset);
				offset += LayoutUtil.getStd140Size(type, definedStructs);
			} else if (type.contains("[")) {
				int openingBracket = type.indexOf('[');
				int closingBracket = type.indexOf(']');
				String elementType = type.substring(0, openingBracket);
				int count = Integer.parseInt(type.substring(openingBracket + 1, closingBracket));
				Std140ArrayAccessor arrayAccessor = new Std140ArrayAccessor(offset, elementType, count, definedStructs);
				arrays.put(name, arrayAccessor);
				offset = arrayAccessor.getAlignedOffset() + arrayAccessor.getSize();
			} else {
				Std140StructAccessor structAccessor = new Std140StructAccessor(offset, type, definedStructs);
				structs.put(name, structAccessor);
				offset = LayoutUtil.align(offset, LayoutUtil.getStd140Alignment(type, definedStructs)) + LayoutUtil.getStd140Size(type, definedStructs);
			}
		}

		size = offset;
	}

	@Override
	public StructAccessor getStruct(String name) {
		return structs.get(name);
	}

	@Override
	public ArrayAccessor getArray(String name) {
		return arrays.get(name);
	}

	@Override
	public int getSize() {
		return size;
	}

	@Override
	public void set(String name, float v) {
		data.putFloat(primitives.get(name), v);
	}

	@Override
	public void set(String name, Vec2 v) {
		LayoutUtil.store(v, data, primitives.get(name));
	}

	@Override
	public void set(String name, Vec3 v) {
		LayoutUtil.store(v, data, primitives.get(name));
	}

	@Override
	public void set(String name, Vec4 v) {
		LayoutUtil.store(v, data, primitives.get(name));
	}

	@Override
	public void set(String name, Mat2 v) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void set(String name, Mat3 v) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void set(String name, Mat4 v) {
		LayoutUtil.store(v, data, primitives.get(name));
	}

	@Override
	public void setData(ByteBuffer data) {
		this.data = data;
		for (Std140StructAccessor struct : structs.values()) {
			struct.setData(data);
		}
		for (Std140ArrayAccessor array : arrays.values()) {
			array.setData(data);
		}
	}

	@Override
	public void seti(String name, int v) {
		data.putInt(primitives.get(name), v);
	}
}
