/**
 * 
 */
package de.kjEngine.renderer;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author konst
 *
 */
public class RenderList extends ArrayList<Renderable<?>> {
	private static final long serialVersionUID = 7729593212849725043L;

	public RenderList() {
	}

	public RenderList(int initialCapacity) {
		super(initialCapacity);
	}

	public RenderList(Collection<? extends Renderable<?>> c) {
		super(c);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Renderable<?>> T get(Class<T> type) {
		int size = size();
		for (int i = 0; i < size; i++) {
			if (type.isInstance(get(i))) {
				return (T) get(i);
			}
		}
		return null;
	}
}