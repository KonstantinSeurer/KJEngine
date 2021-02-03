package de.kjEngine.component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.kjEngine.component.Component.RenderImplementation;
import de.kjEngine.renderer.Renderable;
import de.kjEngine.renderer.Renderable.RenderImplementation.ID;
import de.kjEngine.util.container.Array;
import de.kjEngine.util.container.WriteArray;

public class Component<ParentType extends Component<?, ?, ContainerType>, ComponentType extends Component<ParentType, ?, ContainerType>, ContainerType extends Container<?, ?>>
		extends Renderable<RenderImplementation<? super ComponentType>> {

	public static final int ACTIVE = 1;
	public static final int EARLY = 2;
	public static final int LATE = 4;

	public static interface RenderImplementation<ComponentType> extends Renderable.RenderImplementation {

		public void init(ComponentType c);

		public void updateDescriptors(ComponentType c);

		public void render(ComponentType c);
	}

	private static int counter;

	protected ParentType parent;
	protected final Array<Component<?, ?, ContainerType>> children = new Array<>();
	protected ContainerType container;
	public String name = getClass().getName().substring(getClass().getName().lastIndexOf('.') + 1) + counter++;
	private int flags;

	public Component(int flags) {
		this.flags = flags;
		setActive(true);
	}

	public final ContainerType getContainer() {
		return container;
	}

	public final void setActive(boolean active) {
		if (active) {
			flags |= ACTIVE;
		} else {
			flags &= 0xffffffff ^ ACTIVE;
		}
	}

	public final boolean isActive() {
		return (flags & ACTIVE) != 0;
	}

	public final boolean isInactive() {
		return (flags & ACTIVE) == 0;
	}

	public final boolean isEarly() {
		return (flags & EARLY) != 0;
	}

	public final boolean isLate() {
		return (flags & LATE) != 0;
	}

	public final boolean isDynamic() {
		return isEarly() | isLate();
	}

	public void setContainer(ContainerType container) {
		this.container = container;
		for (int i = 0; i < children.length(); i++) {
			children.get(i).setContainer(container);
		}
	}

	@SuppressWarnings("unchecked")
	public ComponentType add(Component<? super ComponentType, ?, ContainerType> c) {
		if (c.parent != null) {
			if (c.parent == this) {
				return (ComponentType) this;
			}
			c.parent.remove(c);
		}
		c.parent = (ComponentType) this;
		c.setContainer(container);
		children.add(c);
		c.init();
		return (ComponentType) this;
	}

	@SuppressWarnings("unchecked")
	public ComponentType remove(Component<?, ?, ContainerType> c) {
		c.parent = null;
		children.remove(c);
		return (ComponentType) this;
	}

	@SuppressWarnings("unchecked")
	public ComponentType removeAll() {
		for (int i = 0; i < children.length(); i++) {
			children.get(i).parent = null;
		}
		children.clear(true);
		return (ComponentType) this;
	}

	@SuppressWarnings("unchecked")
	public <T extends Component<?, ?, ContainerType>> T get(Class<T> type) {
		for (int i = 0; i < children.length(); i++) {
			Component<?, ?, ContainerType> c = children.get(i);
			if (type.isInstance(c)) {
				return (T) c;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public <T extends Component<?, ?, ContainerType>> List<T> getAll(Class<T> type) {
		List<T> result = new ArrayList<>();
		for (int i = 0; i < children.length(); i++) {
			Component<?, ?, ContainerType> c = children.get(i);
			if (type.isInstance(c)) {
				result.add((T) c);
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public <T extends Component<?, ?, ContainerType>> ComponentType getAll(Class<T> type, List<? super T> target) {
		for (int i = 0; i < children.length(); i++) {
			Component<?, ?, ContainerType> c = children.get(i);
			if (type.isInstance(c)) {
				target.add((T) c);
			}
		}
		return (ComponentType) this;
	}

	@SuppressWarnings("unchecked")
	public <T extends Component<?, ?, ContainerType>> ComponentType getAll(Class<T> type, WriteArray<? super T> target) {
		for (int i = 0; i < children.length(); i++) {
			Component<?, ?, ContainerType> c = children.get(i);
			if (type.isInstance(c)) {
				target.add((T) c);
			}
		}
		return (ComponentType) this;
	}

	@SuppressWarnings("unchecked")
	public <T extends Component<?, ?, ContainerType>> ComponentType getAllUnsafe(Class<T> type, WriteArray<? super T> target) {
		if (children.length() == 1) {
			target.add((T) children.get(0));
		} else {
			for (int i = 0; i < children.length(); i++) {
				Component<?, ?, ContainerType> c = children.get(i);
				if (type.isInstance(c)) {
					target.add((T) c);
				}
			}
		}
		return (ComponentType) this;
	}

	public boolean has(Class<?> type) {
		for (int i = 0; i < children.length(); i++) {
			if (type.isInstance(children.get(i))) {
				return true;
			}
		}
		return false;
	}

	public void render() {
		for (int i = 0; i < children.length(); i++) {
			Component<?, ?, ContainerType> child = children.get(i);
			if (child.isActive()) {
				child.render();
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void render(ID id) {
		if (renderImplementationMap != null && renderImplementationMap.containsKey(id)) {
			renderImplementationMap.get(id).render((ComponentType) this);
		}

		for (int i = 0; i < children.length(); i++) {
			Component<?, ?, ContainerType> child = children.get(i);
			if (child.isActive()) {
				child.render(id);
			}
		}
	}

	public final void updateComponent(float delta) {
		for (int i = 0; i < children.length(); i++) {
			Component<?, ?, ContainerType> child = children.get(i);
			if (child.isEarly() && child.isActive()) {
				child.updateComponent(delta);
			}
		}
		update(delta);
		for (int i = 0; i < children.length(); i++) {
			Component<?, ?, ContainerType> child = children.get(i);
			if (child.isLate() && child.isActive()) {
				child.updateComponent(delta);
			}
		}
	}

	protected void update(float delta) {
	}

	@SuppressWarnings("unchecked")
	public void init() {
		for (int i = 0; i < children.length(); i++) {
			children.get(i).init();
		}

		if (renderImplementations != null) {
			for (int i = 0; i < renderImplementations.length; i++) {
				renderImplementations[i].init((ComponentType) this);
			}
		}
	}

	@Override
	public void dispose() {
		for (int i = 0; i < children.length(); i++) {
			children.get(i).dispose();
		}

		if (renderImplementations != null) {
			for (int i = 0; i < renderImplementations.length; i++) {
				renderImplementations[i].dispose();
			}
		}
	}

	public void updateDescriptors() {
		for (int i = 0; i < children.length(); i++) {
			Component<?, ?, ContainerType> child = children.get(i);
			if (child.isActive()) {
				child.updateDescriptors();
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void updateDescriptors(ID id) {
		if (renderImplementationMap != null && renderImplementationMap.containsKey(id)) {
			renderImplementationMap.get(id).updateDescriptors((ComponentType) this);
		}

		for (int i = 0; i < children.length(); i++) {
			Component<?, ?, ContainerType> child = children.get(i);
			if (child.isActive()) {
				child.updateDescriptors(id);
			}
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected RenderImplementation[] createImplementationArray(int length) {
		return new RenderImplementation[length];
	}

	public final ParentType getParent() {
		return parent;
	}

	@SuppressWarnings("unchecked")
	public Set<Class<? extends Component<?, ?, ContainerType>>> getComponentTypes() {
		Set<Class<? extends Component<?, ?, ContainerType>>> result = new HashSet<>();
		for (int i = 0; i < children.length(); i++) {
			result.add((Class<? extends Component<?, ?, ContainerType>>) children.get(i).getClass());
		}
		return result;
	}

	public Component<?, ?, ContainerType> get(String name) {
		for (int i = 0; i < children.length(); i++) {
			Component<?, ?, ContainerType> c = children.get(i);
			if (c.name.equals(name)) {
				return c;
			}
		}
		return null;
	}
}
