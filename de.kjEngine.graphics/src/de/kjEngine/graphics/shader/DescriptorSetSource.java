/**
 * 
 */
package de.kjEngine.graphics.shader;

import java.util.ArrayList;
import java.util.List;

import de.kjEngine.util.Copy;

/**
 * @author konst
 *
 */
public class DescriptorSetSource implements Copy<DescriptorSetSource> {

	public static DescriptorSetSource parse(ObjectSource source) throws ShaderCompilationException {
		if (!source.getName().startsWith("set")) {
			throw new IllegalArgumentException();
		}
		String name = "";
		String[] pts = source.getName().split(" ");
		if (pts.length > 1) {
			name = pts[1].trim();
		}

		List<DescriptorSource> descriptors = new ArrayList<>();

		int index = 0;
		while (index < source.getContent().length()) {
			ShaderCompilationUtil.ParseResult<ObjectSource> result = ShaderCompilationUtil.parseObject(source.getContent(), index);
			index = result.endIndex + 1;
			if (result.successful) {
				if (result.data.getName().contains("uniformBuffer") || result.data.getName().contains("storageBuffer")) {
					descriptors.add(BufferSource.parse(result.data));
				}
			} else {
				String s = result.data.getName().trim();
				if (s.startsWith("texture")) {
					descriptors.add(TextureSource.parse(s));
				} else if (s.contains("image")) {
					descriptors.add(ImageSource.parse(s));
				}
			}
		}

		return new DescriptorSetSource(name, descriptors);
	}

	private List<DescriptorSource> descriptors;
	private String name;

	public DescriptorSetSource(String name, List<DescriptorSource> descriptors) {
		this.name = name;
		this.descriptors = descriptors;
	}
	
	public DescriptorSetSource(String name) {
		this(name, new ArrayList<>());
	}

	public DescriptorSetSource() {
		this("");
	}

	/**
	 * @return the descriptors
	 */
	public List<DescriptorSource> getDescriptors() {
		return descriptors;
	}
	
	public DescriptorSource getDescriptor(String name) {
		for (int i = 0; i < descriptors.size(); i++) {
			if (descriptors.get(i).getName().equals(name)) {
				return descriptors.get(i);
			}
		}
		return null;
	}
	
	public BufferSource getBuffer(String name) {
		return (BufferSource) getDescriptor(name);
	}
	
	public TextureSource getTexture(String name) {
		return (TextureSource) getDescriptor(name);
	}
	
	public ImageSource getImage(String name) {
		return (ImageSource) getDescriptor(name);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public DescriptorSetSource setName(String name) {
		this.name = name;
		return this;
	}
	
	public DescriptorSetSource addDescriptor(DescriptorSource descriptor) {
		descriptors.add(descriptor);
		return this;
	}

	@Override
	public DescriptorSetSource deepCopy() {
		return null;
	}

	@Override
	public DescriptorSetSource shallowCopy() {
		return new DescriptorSetSource(name, descriptors);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((descriptors == null) ? 0 : descriptors.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DescriptorSetSource other = (DescriptorSetSource) obj;
		if (descriptors == null) {
			if (other.descriptors != null)
				return false;
		} else if (!descriptors.equals(other.descriptors))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
