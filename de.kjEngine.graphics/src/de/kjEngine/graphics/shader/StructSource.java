/**
 * 
 */
package de.kjEngine.graphics.shader;

import java.util.ArrayList;
import java.util.List;

/**
 * @author konst
 *
 */
public class StructSource implements DataType {
	
	public static StructSource parse(ObjectSource source) throws ShaderCompilationException {
		if (!source.getName().startsWith("struct")) {
			throw new UnexpectedValueException(0, source.getName());
		}
		
		String[] pts = source.getName().split(" ");

		if (pts.length != 2) {
			throw new IllegalArgumentException();
		}

		String name = pts[1].trim();
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
		
		return new StructSource(name, members);
	}

	private String name;
	private List<VariableSource> members;
	
	/**
	 * @param name
	 * @param members
	 */
	public StructSource(String name, List<VariableSource> members) {
		this.name = name;
		this.members = members;
	}
	
	public StructSource(String name) {
		this(name, new ArrayList<>());
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the members
	 */
	public List<VariableSource> getMembers() {
		return members;
	}
	
	@Override
	public Type getType() {
		return Type.STRUCT;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	public StructSource addMembder(VariableSource member) {
		members.add(member);
		return this;
	}
}
