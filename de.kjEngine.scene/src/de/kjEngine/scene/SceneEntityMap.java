/**
 * 
 */
package de.kjEngine.scene;

import de.kjEngine.component.Container.RenderImplementation;
import de.kjEngine.math.Average;
import de.kjEngine.scene.model.Model;
import de.kjEngine.scene.model.ModelComponent;
import de.kjEngine.scene.model.ModelEntityMap;
import de.kjEngine.util.container.Array;

/**
 * @author konst
 *
 */
public class SceneEntityMap implements RenderImplementation<Scene> {

	public Array<Model> models = new Array<>();
	
	public Array<Array<Entity>> entities = new Array<>();

	public Array<Entity> staticEntityQueue = new Array<>();
	public Array<Entity> dynamicEntityQueue = new Array<>();

	private Array<Entity> staticEntities = new Array<>();

	private long renderPassId;

	private Array<ModelComponent> modelBuffer = new Array<>();
	
	private Array<Array<Entity>> batchCache = new Array<>();

	public SceneEntityMap() {
	}

	@Override
	public void dispose() {
	}
	
	Average avg = new Average();

	private void recordEntities(Scene scene, Array<Entity> es) {
		for (int i = 0; i < es.length(); i++) {
			Entity e = es.get(i);
			if (e == null | e.isInactive()) {
				continue;
			}

			modelBuffer.clear(false);
			e.getAllUnsafe(ModelComponent.class, modelBuffer);

			for (int j = 0; j < modelBuffer.length(); j++) {
				Model model = modelBuffer.get(j).getModel();
				ModelEntityMap impl = model.entityMapImplementation;
				Array<Entity> batch;
				if (impl.renderPassId != renderPassId) {
					impl.renderPassId = renderPassId;
					impl.index = models.length();
					models.add(model);
					if (batchCache.isEmpty()) {
						batch = new Array<>();
					} else {
						batch = batchCache.remove(batchCache.length() - 1);
					}
					entities.add(batch);
				} else {
					batch = entities.get(impl.index);
				}
				batch.add(e);
			}
		}
	}

	@Override
	public void updateDescriptors(Scene scene) {
	}

	@Override
	public void render(Scene scene) {
		for (int i = 0; i < entities.length(); i++) {
			batchCache.add(entities.get(i));
			entities.get(i).clear(false);
		}
		
		renderPassId++;

		models.clear(false);
		entities.clear(false);

		staticEntities.add(staticEntityQueue);

		recordEntities(scene, staticEntities);
		recordEntities(scene, dynamicEntityQueue);

		staticEntityQueue.clear(false);
		dynamicEntityQueue.clear(false);
	}
}
