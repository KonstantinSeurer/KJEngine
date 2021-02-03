/**
 * 
 */
package de.kjEngine.renderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.kjEngine.renderer.Renderable.RenderImplementation.ID;
import de.kjEngine.util.Disposable;

/**
 * @author konst
 *
 */
public abstract class Renderable<RenderImplementationType extends Renderable.RenderImplementation> implements Disposable {
	
	public static interface RenderImplementation extends Disposable {

		public static class ID {

			public final Class<?> renderableClass;
			public final String name;
			private final int hash;

			/**
			 * @param sceneGraphItemClass
			 * @param name
			 */
			public ID(Class<?> sceneGraphItemClass, String name) {
				this.renderableClass = sceneGraphItemClass;
				this.name = name;
				hash = sceneGraphItemClass.getName().hashCode() + name.hashCode();
			}

			@Override
			public int hashCode() {
				return hash;
			}

			@Override
			public boolean equals(Object other) {
				if (other == null) {
					return false;
				}
				if (!(other instanceof ID)) {
					return false;
				}
				ID id = (ID) other;
				if (hash != id.hash) {
					return false;
				}
				return renderableClass == id.renderableClass && name.equals(id.name);
			}

			@Override
			public String toString() {
				return "ID [sceneGraphItemClass=" + renderableClass + ", name=" + name + ", hash=" + hash + "]";
			}
		}

		public static interface Provider {

			public RenderImplementation create();
		}

		public static class Registry {

			private static Map<ID, Provider> providers = new HashMap<>();
			private static Map<ID, Set<ID>> dependencies = new HashMap<>();

			public static void registerProvider(ID id, Provider provider) {
				providers.put(id, provider);
			}

			public static Provider getProvider(ID id) {
				return providers.get(id);
			}

			public static void registerDependency(ID feature, Set<ID> dependency) {
				dependencies.put(feature, dependency);
				// RenderingContext.updateGPUImplementationList();
			}

			public static Set<ID> getDependency(ID feature) {
				return dependencies.get(feature);
			}

			/**
			 * @return the dependencies
			 */
			public static Map<ID, Set<ID>> getDependencies() {
				return dependencies;
			}

			public static boolean isSuperClass(Class<?> c, Class<?> superClass) {
				Class<?> temp = c;
				do {
					if (temp == superClass) {
						return true;
					}
					temp = temp.getSuperclass();
				} while (temp != Object.class && temp != null);
				return false;
			}
		}
	}

	public final Map<ID, RenderImplementationType> renderImplementationMap;
	public final RenderImplementationType[] renderImplementations;

	@SuppressWarnings("unchecked")
	public Renderable() {
		Class<?> c = getClass();
		List<RenderImplementation.ID> ids = RenderingContext.getRequiredRenderImplementations();
		if (!ids.isEmpty()) {
			renderImplementationMap = new HashMap<>();
			List<RenderImplementationType> renderImplementationList = new ArrayList<>();
			for (RenderImplementation.ID id : ids) {
				if (RenderImplementation.Registry.isSuperClass(c, id.renderableClass)) {
					RenderImplementation.Provider provider = RenderImplementation.Registry.getProvider(id);
					if (provider != null) {
						RenderImplementationType impl = (RenderImplementationType) provider.create();
						renderImplementationMap.put(id, impl);
						renderImplementationList.add(impl);
					}
				}
			}
			renderImplementations = renderImplementationList.toArray(createImplementationArray(renderImplementationList.size()));
		} else {
			renderImplementationMap = null;
			renderImplementations = null;
		}
	}
	
	protected Renderable(Map<ID, RenderImplementationType> renderImplementationMap, RenderImplementationType[] renderImplementations) {
		this.renderImplementationMap = renderImplementationMap;
		this.renderImplementations = renderImplementations;
	}
	
	protected abstract RenderImplementationType[] createImplementationArray(int length);

	@SuppressWarnings("unchecked")
	public <T extends RenderImplementationType> T getRenderImplementation(Class<T> type) {
		if (renderImplementations == null) {
			return null;
		}
		for (int i = 0; i < renderImplementations.length; i++) {
			if (type.isInstance(renderImplementations[i])) {
				return (T) renderImplementations[i];
			}
		}
		return null;
	}

	@Override
	public void dispose() {
		for (RenderImplementation implementation : renderImplementations) {
			implementation.dispose();
		}
	}
}
