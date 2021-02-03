/**
 * 
 */
package de.kjEngine.renderer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import de.kjEngine.graphics.CommandBuffer;
import de.kjEngine.graphics.Texture2D;
import de.kjEngine.io.RL;
import de.kjEngine.io.ResourceManager;
import de.kjEngine.io.serilization.Serializer;
import de.kjEngine.renderer.Stage.InputProvider;
import de.kjEngine.util.Disposable;

/**
 * @author konst
 *
 */
public class Pipeline implements Disposable {

	public static interface Provider {

		public Stage create(InputProvider input, JSONObject obj);
	}

	public static interface PrepassProvider {

		public PrepassStage create(JSONObject obj);
	}

	public static Pipeline create(RL rl, int width, int height) {
		return create(new JSONObject(ResourceManager.loadTextResource(rl, true)), width, height);
	}

	public static Pipeline create(JSONObject obj, int width, int height) {
		final Stage[] stages;
		PrepassStage[] prepassStages = new PrepassStage[0];
		if (obj.has("stages")) {
			JSONArray stageArray = obj.getJSONArray("stages");
			stages = new Stage[stageArray.length()];
			for (int i = 0; i < stages.length; i++) {
				JSONObject stage = stageArray.getJSONObject(i);
				if (!stage.has("type")) {
					continue;
				}
				String type = stage.getString("type");
				if (!providers.containsKey(type)) {
					continue;
				}
				final JSONObject inputs;
				if (stage.has("inputs")) {
					inputs = stage.getJSONObject("inputs");
				} else {
					inputs = new JSONObject();
				}
				stages[i] = providers.get(type).create(new InputProvider() {

					@Override
					public void reset() {
						for (String inputName : inputs.keySet()) {
							JSONObject input = inputs.getJSONObject(inputName);
							stages[input.getInt("stage")].reset();
						}
					}

					@Override
					public void render(RenderList renderList, CommandBuffer cb) {
						for (String inputName : inputs.keySet()) {
							JSONObject input = inputs.getJSONObject(inputName);
							stages[input.getInt("stage")].render(renderList, cb);
						}
					}

					@Override
					public void link() {
						for (String inputName : inputs.keySet()) {
							JSONObject input = inputs.getJSONObject(inputName);
							stages[input.getInt("stage")].link();
						}
					}

					@Override
					public Texture2D get(String name) {
//						if (!inputs.has("name")) {
//							return Color.BLACK.getTexture();
//						}
						if (!inputs.has(name)) {
							return null;
						}
						JSONObject input = inputs.getJSONObject(name);
						return stages[input.getInt("stage")].getOutput().get(input.getString("output"));
					}

					@Override
					public void updateDescriptors() {
						for (String inputName : inputs.keySet()) {
							JSONObject input = inputs.getJSONObject(inputName);
							stages[input.getInt("stage")].updateDescriptors();
						}
					}

					@Override
					public void prepareResize() {
						for (String inputName : inputs.keySet()) {
							JSONObject input = inputs.getJSONObject(inputName);
							stages[input.getInt("stage")].prepareResize();
						}
					}

					@Override
					public void resize(int width, int height) {
						for (String inputName : inputs.keySet()) {
							JSONObject input = inputs.getJSONObject(inputName);
							stages[input.getInt("stage")].resize(width, height);
						}
					}
				}, stage);
				Serializer.deserialize(stage, stages[i]);
			}
			if (stages.length > 0) {
				Stage output = stages[stages.length - 1];

				output.prepareResize();
				output.resize(width, height);
				output.link();
			}
		} else {
			stages = new Stage[0];
		}
		if (obj.has("prepassStages")) {
			JSONArray stageArray = obj.getJSONArray("prepassStages");
			prepassStages = new PrepassStage[stageArray.length()];
			for (int i = 0; i < prepassStages.length; i++) {
				JSONObject stage = stageArray.getJSONObject(i);
				if (!stage.has("type")) {
					continue;
				}
				String type = stage.getString("type");
				if (!prepassProviders.containsKey(type)) {
					continue;
				}
				prepassStages[i] = prepassProviders.get(type).create(stage);
				Serializer.deserialize(stage, prepassStages[i]);
			}
		}
		return new Pipeline(prepassStages, stages);
	}

	private static Map<String, Provider> providers = new HashMap<>();
	private static Map<String, PrepassProvider> prepassProviders = new HashMap<>();

	public static void registerStageProvider(String name, Provider p) {
		providers.put(name, p);
	}

	public static void registerPrepassStageProvider(String name, PrepassProvider p) {
		prepassProviders.put(name, p);
	}

	static {
		registerStageProvider("pipeline", new Provider() {

			@Override
			public Stage create(InputProvider input, JSONObject obj) {
				Pipeline pipeline = null;
				if (obj.has("file")) {
					pipeline = Pipeline.create(new JSONObject(ResourceManager.loadTextResource(RL.create(obj.getString("file")), true)), 2, 2);
				}
				pipeline = Pipeline.create(obj, 2, 2);
				return new PipelineStage(pipeline);
			}
		});
		registerStageProvider("addFilter", new Provider() {

			@Override
			public Stage create(InputProvider input, JSONObject obj) {
				return new AddFilter(input);
			}
		});
		registerStageProvider("blurFilter", new Provider() {

			@Override
			public Stage create(InputProvider input, JSONObject obj) {
				return new BlurFilter(input);
			}
		});
		registerStageProvider("exposureFilter", new Provider() {

			@Override
			public Stage create(InputProvider input, JSONObject obj) {
				return new ExposureFilter(input);
			}
		});
		registerStageProvider("acesTonemappingFilter", new Provider() {

			@Override
			public Stage create(InputProvider input, JSONObject obj) {
				return new ACESTonemappingFilter(input);
			}
		});
		registerStageProvider("mulFilter", new Provider() {

			@Override
			public Stage create(InputProvider input, JSONObject obj) {
				return new MulFilter(input);
			}
		});
		registerStageProvider("vecScalarMulFilter", new Provider() {

			@Override
			public Stage create(InputProvider input, JSONObject obj) {
				return new VecScalarMulFilter(input);
			}
		});
		registerStageProvider("singleAxisBlurFilter", new Provider() {

			@Override
			public Stage create(InputProvider input, JSONObject obj) {
				return new SingleAxisBlurFilter(input);
			}
		});
		registerStageProvider("colorInput", new Provider() {

			@Override
			public Stage create(InputProvider input, JSONObject obj) {
				return new ColorInput(input);
			}
		});
		registerStageProvider("textureInput", new Provider() {

			@Override
			public Stage create(InputProvider input, JSONObject obj) {
				return new TextureInput(input);
			}
		});
		registerStageProvider("bloomFilter", new Provider() {

			@Override
			public Stage create(InputProvider input, JSONObject obj) {
				return new BloomFilter(input);
			}
		});
	}

	private PrepassStage[] prepassStages;
	private Stage[] stages;

	public Pipeline(PrepassStage[] prepassStages, Stage[] stages) {
		this.prepassStages = prepassStages;
		this.stages = stages;
	}

	public PrepassStage getPrepassStage(int index) {
		return prepassStages[index];
	}

	@SuppressWarnings("unchecked")
	public <T extends PrepassStage> T getPrepassStage(Class<T> c) {
		for (int i = 0; i < prepassStages.length; i++) {
			if (prepassStages[i].getClass() == c) {
				return (T) prepassStages[i];
			}
		}
		return null;
	}

	public Stage getStage(int index) {
		return stages[index];
	}

	@SuppressWarnings("unchecked")
	public <T extends Stage> T getStage(Class<T> c) {
		for (int i = 0; i < stages.length; i++) {
			if (stages[i].getClass() == c) {
				return (T) stages[i];
			}
		}
		return null;
	}

	public Stage getFinalStage() {
		if (stages.length > 0) {
			return stages[stages.length - 1];
		}
		return null;
	}

	public void getRequiredRenderImplementations(Set<Renderable.RenderImplementation.ID> target) {
		for (int i = 0; i < prepassStages.length; i++) {
			prepassStages[i].getRequiredRenderImplementations(target);
		}
		for (int i = 0; i < stages.length; i++) {
			stages[i].getRequiredRenderImplementations(target);
		}
	}

	@Override
	public void dispose() {
		for (Stage stage : stages) {
			stage.dispose();
		}
	}

	public void reset() {
		if (getFinalStage() != null) {
			getFinalStage().reset();
		}
	}

	public void updateDescriptors() {
		for (int i = 0; i < prepassStages.length; i++) {
			prepassStages[i].updateDescriptors();
		}
		if (getFinalStage() != null) {
			getFinalStage().updateDescriptors();
		}
	}

	public void render(RenderList renderList, CommandBuffer cb) {
		for (int i = 0; i < prepassStages.length; i++) {
			prepassStages[i].render(renderList, cb);
		}
		if (getFinalStage() != null) {
			getFinalStage().render(renderList, cb);
		}
	}
}
