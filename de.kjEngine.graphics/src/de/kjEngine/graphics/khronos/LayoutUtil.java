/**
 * 
 */
package de.kjEngine.graphics.khronos;

import java.nio.ByteBuffer;
import java.util.List;

import de.kjEngine.graphics.shader.StructSource;
import de.kjEngine.graphics.shader.VariableSource;
import de.kjEngine.math.Mat4;
import de.kjEngine.math.Vec2;
import de.kjEngine.math.Vec3;
import de.kjEngine.math.Vec4;

/**
 * @author konst
 *
 */
public class LayoutUtil {

	private LayoutUtil() {
	}

	public static int align(int off, int alignment) {
		if (off % alignment == 0) {
			return off;
		}
		return (off / alignment + 1) * alignment;
	}

	public static int getStd140Alignment(String type, List<StructSource> definedStructs) {
		if (isPrimitive(type)) {
			switch (type) {
			case "float":
				return 4;
			case "int":
				return 4;
			case "vec2":
				return 8;
			case "mat2":
				throw new UnsupportedOperationException("mat2");
			case "mat3":
				throw new UnsupportedOperationException("mat3");
			}
			return 16;
		} else if (isArray(type)) {
			return getStd140Size(type, definedStructs);
		} else {
			for (StructSource struct : definedStructs) {
				if (struct.getName().equals(type)) {
					int maxAlignment = 0;
					for (VariableSource member : struct.getMembers()) {
						maxAlignment = Math.max(maxAlignment, getStd140Alignment(member.getType(), definedStructs));
					}
					return align(maxAlignment, 16);
				}
			}
		}
		return 16;
	}

	public static int getStd140Size(String type, List<StructSource> definedStructs) {
		if (isPrimitive(type)) {
			switch (type) {
			case "float":
				return 4;
			case "int":
				return 4;
			case "vec2":
				return 8;
			case "vec3":
				return 16;
			case "vec4":
				return 16;
			case "mat2":
				throw new UnsupportedOperationException("mat2");
			case "mat3":
				throw new UnsupportedOperationException("mat3");
			case "mat4":
				return 64;
			}
		} else if (isArray(type)) {
			int openingBracket = type.indexOf('[');
			int closingBracket = type.indexOf(']');
			String elementType = type.substring(0, openingBracket);
			int count = Integer.parseInt(type.substring(openingBracket, closingBracket));
			return align(getStd140Size(elementType, definedStructs), 16) * count;
		} else {
			for (StructSource struct : definedStructs) {
				if (struct.getName().equals(type)) {
					int offset = 0;
					for (VariableSource member : struct.getMembers()) {
						offset = align(offset, getStd140Alignment(member.getType(), definedStructs));
						offset += getStd140Size(member.getType(), definedStructs);
					}
					return align(offset, getStd140Alignment(type, definedStructs));
				}
			}
		}
		return 0;
	}

	public static boolean isPrimitive(String type) {
		switch (type) {
		case "float":
		case "int":
		case "vec2":
		case "vec3":
		case "vec4":
		case "mat4":
			return true;
		case "mat2":
			throw new UnsupportedOperationException("mat2");
		case "mat3":
			throw new UnsupportedOperationException("mat3");
		}
		return false;
	}

	public static boolean isArray(String type) {
		return type.contains("[");
	}

	public static void store(Mat4 v, ByteBuffer target, int offset) {
		target.putFloat(offset, v.xx);
		target.putFloat(offset + 4, v.xy);
		target.putFloat(offset + 8, v.xz);
		target.putFloat(offset + 12, v.xw);

		target.putFloat(offset + 16, v.yx);
		target.putFloat(offset + 20, v.yy);
		target.putFloat(offset + 24, v.yz);
		target.putFloat(offset + 28, v.yw);

		target.putFloat(offset + 32, v.zx);
		target.putFloat(offset + 36, v.zy);
		target.putFloat(offset + 40, v.zz);
		target.putFloat(offset + 44, v.zw);

		target.putFloat(offset + 48, v.wx);
		target.putFloat(offset + 52, v.wy);
		target.putFloat(offset + 56, v.wz);
		target.putFloat(offset + 60, v.ww);
	}

	public static void store(Vec4 v, ByteBuffer target, int offset) {
		target.putFloat(offset, v.x);
		target.putFloat(offset + 4, v.y);
		target.putFloat(offset + 8, v.z);
		target.putFloat(offset + 12, v.w);
	}

	public static void store(Vec3 v, ByteBuffer target, int offset) {
		target.putFloat(offset, v.x);
		target.putFloat(offset + 4, v.y);
		target.putFloat(offset + 8, v.z);
	}

	public static void store(Vec2 v, ByteBuffer target, int offset) {
		target.putFloat(offset, v.x);
		target.putFloat(offset + 4, v.y);
	}
}
