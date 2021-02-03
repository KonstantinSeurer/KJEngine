/**
 * 
 */
package de.kjEngine.component;

import java.util.ArrayList;
import java.util.List;

import de.kjEngine.renderer.Renderable;
import de.kjEngine.renderer.RenderingContext;
import de.kjEngine.renderer.Renderable.RenderImplementation.ID;

/**
 * @author konst
 *
 */
public class Container<RootComponentType extends Component<?, ?, ContainerType>, ContainerType extends Container<?, ?>> extends Renderable<Container.RenderImplementation<ContainerType>> {

	public static interface RenderImplementation<ContainerType> extends Renderable.RenderImplementation {

		public void updateDescriptors(ContainerType container);

		public void render(ContainerType container);
	}
	
	public static interface Implementation<ContainerType> {
		
		public void updateEarly(ContainerType container, float delta);
		
		public void updateLate(ContainerType container, float delta);
	}
	
	private static int counter;

	public final RootComponentType root;
	public final List<Implementation<ContainerType>> implementations = new ArrayList<>();
	public String name = getClass().getName().substring(getClass().getName().lastIndexOf('.') + 1) + counter++;

	@SuppressWarnings("unchecked")
	public Container(RootComponentType root) {
		this.root = root;
		root.setContainer((ContainerType) this);
	}
	
	@SuppressWarnings("unchecked")
	public void update(float delta) {
		for (int i = 0; i < implementations.size(); i++) {
			implementations.get(i).updateEarly((ContainerType) this, delta);
		}
		
		root.updateComponent(delta);
		
		for (int i = 0; i < implementations.size(); i++) {
			implementations.get(i).updateLate((ContainerType) this, delta);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void updateDescriptors() {
		root.updateDescriptors();
		
		for (ID id : RenderingContext.getRequiredRenderImplementations()) {
			if (renderImplementationMap.containsKey(id)) {
				renderImplementationMap.get(id).updateDescriptors((ContainerType) this);
			} else {
				root.updateDescriptors(id);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void render() {
		root.render();
		
		for (ID id : RenderingContext.getRequiredRenderImplementations()) {
			if (renderImplementationMap.containsKey(id)) {
				renderImplementationMap.get(id).render((ContainerType) this);
			} else {
				root.render(id);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected RenderImplementation<ContainerType>[] createImplementationArray(int length) {
		return new RenderImplementation[length];
	}
}
