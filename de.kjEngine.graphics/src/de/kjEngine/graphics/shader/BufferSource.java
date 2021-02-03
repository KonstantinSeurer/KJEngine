/**
 * 
 */
package de.kjEngine.graphics.shader;

import java.util.ArrayList;
import java.util.List;

import de.kjEngine.graphics.Descriptor;
import de.kjEngine.graphics.Descriptor.Type;
import de.kjEngine.util.Copy;

/**
 * @author konst
 *
 */
public class BufferSource extends DescriptorSource implements Copy<BufferSource> {

	public static BufferSource parse(ObjectSource source) throws ShaderCompilationException {
		String[] pts = source.getName().split(" ");

		if (pts.length < 2 || pts.length > 3) {
			throw new IllegalArgumentException();
		}

		String typeString = pts[pts.length - 2].trim();
		Descriptor.Type type;
		if (typeString.startsWith("uniformBuffer")) {
			type = Descriptor.Type.UNIFORM_BUFFER;
		} else if (typeString.startsWith("storageBuffer")) {
			type = Descriptor.Type.STORAGE_BUFFER;
		} else {
			throw new IllegalArgumentException();
		}

		String name = pts[pts.length - 1].trim();

		Layout layout = Layout.STANDARD;
		for (int i = 0; i < pts.length - 2; i++) {
			if (pts[i].equals("packed")) {
				layout = Layout.PACKED;
			} else if (!pts[i].equals("standard")) {
				throw new UnexpectedValueException(0, pts[i]);
			}
		}

		List<VariableSource> members = new ArrayList<>();

		int startIndex = 0;
		String content = source.getContent();
		for (int i = 0; i < content.length(); i++) {
			if (content.charAt(i) == ';') {
				String declaration = content.substring(startIndex, i).trim();
				startIndex = i + 1;
				members.add(VariableSource.parse(declaration));
			}
		}
		
		int arrayLength = 1;
		if (typeString.contains("[")) {
			arrayLength = Integer.parseInt(typeString.substring(typeString.indexOf('[') + 1, typeString.indexOf(']')));
		}

		return new BufferSource(name, arrayLength, type, layout, members);
	}

	public static enum Layout {
		STANDARD, PACKED
	}

	private List<VariableSource> members;
	private Descriptor.Type type;
	private Layout layout;

	public BufferSource(String name, int arrayLength, Descriptor.Type type, Layout layout, List<VariableSource> members) {
		super(name, arrayLength);
		if (type != Descriptor.Type.UNIFORM_BUFFER && type != Descriptor.Type.STORAGE_BUFFER) {
			throw new IllegalArgumentException(type.toString());
		}
		this.members = members;
		this.type = type;
		this.layout = layout;
	}
	
	public BufferSource(String name, Descriptor.Type type, Layout layout, List<VariableSource> members) {
		this(name, 1, type, layout, members);
	}
	
	public BufferSource(String name, int arrayLength, Descriptor.Type type, Layout layout) {
		this(name, arrayLength, type, layout, new ArrayList<>());
	}
	
	public BufferSource(String name, Descriptor.Type type, Layout layout) {
		this(name, 1, type, layout);
	}

	/**
	 * @return the members
	 */
	public List<VariableSource> getMembers() {
		return members;
	}

	@Override
	public Type getType() {
		return type;
	}

	/**
	 * @return the layout
	 */
	public Layout getLayout() {
		return layout;
	}
	
	public StructSource asStruct() {
		return new StructSource(getName(), members);
	}

	/**
	 * @param layout the layout to set
	 */
	public void setLayout(Layout layout) {
		this.layout = layout;
	}
	
	public void addMember(String type, String name) {
		members.add(new VariableSource(type, name));
	}

	@Override
	public BufferSource deepCopy() {
		return null;
	}

	@Override
	public BufferSource shallowCopy() {
		return new BufferSource(getName(), getArrayLength(), type, layout, members);
	}
}
