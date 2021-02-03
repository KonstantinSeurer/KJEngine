/**
 * 
 */
package de.kjEngine.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

import de.kjEngine.component.Component;
import de.kjEngine.core.Engine;
import de.kjEngine.core.Engine.Settings.Window.RefreshMode;
import de.kjEngine.core.StateHandler;
import de.kjEngine.graphics.Color;
import de.kjEngine.graphics.Graphics;
import de.kjEngine.io.JarProtocolImplementation;
import de.kjEngine.io.RL;
import de.kjEngine.math.Real;
import de.kjEngine.math.Vec2;
import de.kjEngine.math.Vec3;
import de.kjEngine.renderer.Pipeline;
import de.kjEngine.renderer.Pipeline.PrepassProvider;
import de.kjEngine.renderer.PrepassStage;
import de.kjEngine.renderer.RenderList;
import de.kjEngine.renderer.Renderable.RenderImplementation.ID;
import de.kjEngine.renderer.RenderingContext;
import de.kjEngine.scene.Entity;
import de.kjEngine.scene.Scene;
import de.kjEngine.scene.SceneComponent;
import de.kjEngine.scene.SceneManager;
import de.kjEngine.scene.camera.PerspectiveCameraComponent;
import de.kjEngine.scene.camera.PerspectiveFrustum;
import de.kjEngine.scene.light.DirectionalLightComponent;
import de.kjEngine.scene.light.DirectionalLightShadowMapCascadeComponent;
import de.kjEngine.ui.Slider;
import de.kjEngine.ui.Slider.Orientation;
import de.kjEngine.ui.SplitPanel;
import de.kjEngine.ui.Tree;
import de.kjEngine.ui.Tree.Node;
import de.kjEngine.ui.UI;
import de.kjEngine.ui.UIComponent;
import de.kjEngine.ui.UIFactory;
import de.kjEngine.ui.UIScene;
import de.kjEngine.ui.UISceneManager;
import de.kjEngine.ui.Window;
import de.kjEngine.ui.event.MouseButtonEvent;
import de.kjEngine.ui.event.MouseMoveEvent;
import de.kjEngine.ui.event.MouseWheelEvent;
import de.kjEngine.ui.model.Model;
import de.kjEngine.ui.model.ModelComponent;
import de.kjEngine.ui.model.StandartMaterial;
import de.kjEngine.ui.transform.Offset;
import de.kjEngine.ui.transform.ParentMinusPixelSize;
import de.kjEngine.ui.transform.ParentOffset;
import de.kjEngine.ui.transform.ParentSize;
import de.kjEngine.ui.transform.PixelOffset;
import de.kjEngine.ui.transform.Size;
import de.kjEngine.ui.transform.TopRightPixelOffset;

/**
 * @author konst
 *
 */
public class Main {

	public static class EntityData extends SceneComponent<Entity, EntityData> {

		public boolean dynamic;

		public EntityData() {
			super(0);
		}
	}

	public static Pipeline pipeline;
	public static UIScene ui;
	public static UIFactory f;

	public static Slider sceneGraphSlider;
	public static float sceneGraphSliderSensitivity;
	public static Tree sceneGraph;
	public static float lastSceneGraphHeight;

	public static UI view;
	public static StandartMaterial viewMaterial;
	public static Scene scene;
	public static Vec2 cameraRotation = Vec2.create();
	public static Vec3 cameraPosition = Vec3.create();
	public static Entity cameraEntity;
	public static float cameraZoom = 3f;

	public static Node currentNode;
	@SuppressWarnings("rawtypes")
	public static Component currentComponent;

	public static UI propertiesPanel;
	public static Slider propertiesPanelSlider;
	public static Properties currentPropertiesUI;
	public static Map<Class<?>, Properties> propertiesUICache = new HashMap<>();

	public static Slider assetPanelSlider;
	public static UI assetPanel;
	public static AssetCollection currentAssets;
	public static float lastAssetPanelWidth;
	public static List<Asset> visibleAssets = new ArrayList<>();

	public static void main(String[] args) {
		JarProtocolImplementation.registerClassLoader("editor", Main.class);
		
		Pipeline.registerPrepassStageProvider("sceneRenderer", new PrepassProvider() {

			@Override
			public PrepassStage create(JSONObject obj) {
				Pipeline pipeline = null;
				if (obj.has("pipeline")) {
					pipeline = Pipeline.create(RL.create(obj.getString("pipeline")), 128, 128);
				}
				return new SceneRenderer(pipeline);
			}
		});

		Engine.Settings settings = new Engine.Settings();
		settings.window.refreshMode = RefreshMode.UNLIMITED;
		settings.stateHandlers.add(new StateHandler() {

			Vec2 lastViewSize = Vec2.create();

			@Override
			public void dispose() {
			}

			@Override
			public void render(RenderList renderList) {
			}

			@Override
			public void update(float delta) {
				updateView();
				updateAssetPanel();
			}

			private void updateAssetPanel() {
				float width = assetPanel.getPixelWidth();
				if (width == lastAssetPanelWidth) {
					return;
				}
				lastAssetPanelWidth = width;
				layoutAssetPanel();
			}

			private void updateView() {
				float viewWidth = view.getPixelWidth();
				float viewHeight = Math.abs(view.getPixelHeight());
				if (lastViewSize.x != viewWidth || lastViewSize.y != viewHeight) {
					lastViewSize.set(viewWidth, viewHeight);
					Pipeline sceneRenderer = pipeline.getPrepassStage(SceneRenderer.class).pipeline;

					Graphics.getContext().finish();
					sceneRenderer.getFinalStage().prepareResize();
					sceneRenderer.getFinalStage().resize((int) viewWidth, (int) viewHeight);
					Graphics.getContext().finish();
					sceneRenderer.getFinalStage().link();
					viewMaterial.setTexture(pipeline.getPrepassStage(SceneRenderer.class).pipeline.getFinalStage().getOutput().get("result"));
					Graphics.getContext().finish();

					((PerspectiveCameraComponent) scene.camera).getFrustum().setAspect(viewWidth / viewHeight);
				}
			}

			@Override
			public void updateDescriptors() {
			}

			@SuppressWarnings("unchecked")
			@Override
			public void init() {
				ui = new UIScene();
				f = UIFactory.createDefault();

				UI menuPanel = f.image("", "", "1pt", "1pt", new StandartMaterial(Color.GRAY_02.getTexture()));
				int menuPanelLayerOffset = 4;
				UI currentOffset = f.ui("", "trpx", "1win", "20px");
				UI menuPanelLayerOffsetRoot = currentOffset;
				for (int i = 0; i < menuPanelLayerOffset; i++) {
					UI lastOffset = currentOffset;
					currentOffset = f.ui("", "", "1pt", "1pt");
					lastOffset.add(currentOffset);
				}
				currentOffset.add(menuPanel);
				ui.add(menuPanelLayerOffsetRoot);

				menuPanel.add(f.menu("1px", "ct", "50px", "0.8pt", "file", (option) -> {
					switch (option) {
					case "exit":
						System.exit(0);
						break;
					}
				}, "new", "open", "save", "import", "export", "exit"));

				menuPanel.add(f.menu("52px", "ct", "150px", "0.8pt", "add", (option) -> {
					switch (option) {
					case "Entity":
						if (currentComponent != null && currentNode != null) {
							Entity e = createEntity(true);
							currentComponent.add(e);
							currentNode.add(e.name);
						}
						break;
					case "Model":
						if (currentComponent != null && currentNode != null) {
							de.kjEngine.scene.model.ModelComponent model = new de.kjEngine.scene.model.ModelComponent(de.kjEngine.scene.model.Model.getDefaultCube());
							currentComponent.add(model);
							currentNode.add(model.name);
						}
						break;
					}
				}, "Entity", "Transform", "DirectionalLight", "PointLight", "SpotLight", "SphereLight", "PerspectiveCamera", "OrthographicCamera", "Model", "other..."));

				menuPanel.add(f.button("203px", "ct", "50px", "0.8pt", "remove", (button) -> {
					if (currentNode != null && currentComponent.getParent() != null) {
						currentNode.parentNode.remove(currentNode);
						currentNode = null;

						currentComponent.getParent().remove(currentComponent);
						currentComponent = null;
					}
				}));

				SplitPanel splitPanel1 = f.splitPanel("", "", "1win", "20ptmpx", Orientation.HORIZONTAL, "5px");
				ui.add(splitPanel1);
				splitPanel1.setValue(0.2f);

				sceneGraph = f.tree("", "ui", "15ptmpx", "1pt", "20px", (node) -> {
					if (!node.children.isEmpty()) {
						updateSceneGraphSlider();
					}
					setCurrent(node);
				});
				sceneGraphSlider = f.slider("trpx", "", "15px", "1pt", Orientation.VERTICAL, "1pt", "0.5pt", (slider) -> {
					sceneGraph.y.value = (1f - slider.getValue()) * sceneGraphSliderSensitivity;
				});
				splitPanel1.left.add(sceneGraph);
				splitPanel1.left.add(sceneGraphSlider);
				splitPanel1.left.add(new UIComponent<UI, UIComponent<UI, ?>>(0) {

					@Override
					public void mouseWheelMoved(MouseWheelEvent e) {
						if (e.isHandled()) {
							return;
						}
						Vec2 local = parent.toLocal(Vec2.create(e.x, e.y));
						if (local.x < 0f || local.y < 0f || local.x > 1f || local.y > 1f) {
							return;
						}
						float sensitivity = 0.1f;
						sceneGraphSlider.setValue(Real.clamp(sceneGraphSlider.getValue() + sensitivity * e.movement / sceneGraphSliderSensitivity, 0f, 1f));
					}
				});
				sceneGraphSlider.setValue(1f);

				SplitPanel splitPanel2 = f.splitPanel("", "", "1pt", "1pt", Orientation.HORIZONTAL, "5px");
				splitPanel1.right.add(splitPanel2);
				splitPanel2.setValue(0.7f);

				SplitPanel splitPanel3 = f.splitPanel("", "", "1pt", "1pt", Orientation.VERTICAL, "5px");
				splitPanel2.left.add(splitPanel3);
				splitPanel3.setValue(0.3f);
				propertiesPanel = new UI(new PixelOffset(), new PixelOffset(), new ParentMinusPixelSize(15f), new ParentSize(1f));
				splitPanel2.right.add(propertiesPanel);
				propertiesPanelSlider = f.slider("trpx", "", "15px", "1pt", Orientation.VERTICAL, "1pt", "0.5pt", (slider) -> {

				});
				splitPanel2.right.add(propertiesPanelSlider);

				view = new UI(new PixelOffset(), new ParentOffset(1f), new ParentSize(1f), new ParentSize(-1f)) {

					boolean handleEvents;

					@Override
					public void mousePressed(MouseButtonEvent e) {
						super.mousePressed(e);
						Vec2 localMousePos = toLocal(Vec2.create(e.x, e.y));
						handleEvents = localMousePos.x > 0f && localMousePos.y > 0f && localMousePos.x < 1f && localMousePos.y < 1f && e.button == Window.MOUSE_BUTTON_CENTER && !e.isHandled();
					}

					@Override
					public void mouseReleased(MouseButtonEvent e) {
						super.mouseReleased(e);
						handleEvents = false;
					}

					@Override
					public void mouseMoved(MouseMoveEvent e) {
						super.mouseMoved(e);
						if (handleEvents) {
							float dx = (e.x - e.prevX);
							float dy = (e.y - e.prevY);
							if (Window.isKeyPressed(Window.KEY_LSHIFT) || Window.isKeyPressed(Window.KEY_RSHIFT)) {
								PerspectiveFrustum frustum = cameraEntity.get(PerspectiveCameraComponent.class).getFrustum();
								float speed = cameraZoom * Real.tan(frustum.getFov() * 0.5f) * 2f;
								cameraPosition.add(cameraEntity.transform.globalTransform.getX(), -dx * speed / view.getPixelWidth() * frustum.getAspect());
								cameraPosition.add(cameraEntity.transform.globalTransform.getY(), dy * speed / view.getPixelHeight());
							} else {
								float rotationSpeed = 0.01f;
								cameraRotation.x -= dy * rotationSpeed;
								cameraRotation.y += dx * rotationSpeed;
							}
						}
					}

					@Override
					public void mouseWheelMoved(MouseWheelEvent e) {
						super.mouseWheelMoved(e);
						Vec2 localMousePos = toLocal(Vec2.create(e.x, e.y));
						if (localMousePos.x < 0f || localMousePos.y < 0f || localMousePos.x > 1f || localMousePos.y > 1f) {
							return;
						}
						cameraZoom = Math.max(cameraZoom - e.movement, 0f);
					}
				};
				viewMaterial = new StandartMaterial(pipeline.getPrepassStage(SceneRenderer.class).pipeline.getFinalStage().getOutput().get("result"));
				view.add(new ModelComponent(new Model(Model.getRectangle(), viewMaterial)));
				splitPanel3.right.add(view);

				assetPanelSlider = f.slider("trpx", "", "15px", "1pt", Orientation.VERTICAL, "1pt", "0.5pt", (slider) -> {

				});
				splitPanel3.left.add(assetPanelSlider);

				assetPanel = f.ui("", "", "15ptmpx", "1pt");
				splitPanel3.left.add(assetPanel);

				currentAssets = new AssetCollection(f, "root");
				currentAssets.setActive(false);
				assetPanel.add(currentAssets);
				createDefaultAssets();
				setCurrentAssets(currentAssets);

				createEntityPropertiesUI();
				createModelComponentPropertiesUI();

				UISceneManager.addScene("ui", ui);
				UISceneManager.setScene("ui");

				scene = new Scene();

				addEditorCamera(scene);
				addDefaultScene(scene);

				SceneManager.addScene(scene.name, scene);
				SceneManager.setScene(scene.name);

				addSceneToSceneGraph(scene);

				updateSceneGraphSlider();
			}

			@Override
			public void initRenderer(Set<ID> target) {
				pipeline = Pipeline.create(RL.create("file:///pipeline.ppl"), Window.getWidth(), Window.getHeight());
				RenderingContext.addPipeline("pipeline", pipeline);
				RenderingContext.setPipeline("pipeline");

				pipeline.getRequiredRenderImplementations(target);
			}
		});
		Engine.start(settings, Main.class.getModule());
	}

	public static void layoutAssetPanel() {
		float x = 0f;
		float y = 0f;
		float panelWidth = assetPanel.getPixelWidth();
		for (Asset asset : visibleAssets) {
			asset.x.value = x;
			if (asset.getPixelX() + asset.getPixelWidth() > panelWidth) {
				x = 0f;
				y += 1f;
				asset.x.value = 0f;
			}
			asset.y.value = y;
			x += 1f;
		}
	}

	public static void setCurrentAssets(AssetCollection assets) {
		for (Asset asset : visibleAssets) {
			asset.setActive(false);;
		}
		currentAssets = assets;
		visibleAssets.clear();
		visibleAssets.addAll(assets.assets);
		if (assets.parentCollection != null) {
			visibleAssets.add(assets.parentCollection);
		}
		for (Asset asset : visibleAssets) {
			asset.setActive(true);
		}
		layoutAssetPanel();
	}

	private static void createDefaultAssets() {
		AssetCollection defaultAssets = new AssetCollection(f, "default");
		currentAssets.add(defaultAssets);

		for (int i = 0; i < 20; i++) {
			currentAssets.add(new AssetCollection(f, "test[" + i + "]"));
		}
	}

	protected static void createEntityPropertiesUI() {
		Offset defaultX = new PixelOffset();
		Size defaultWidth = new ParentSize(1f);

		Vec3Property posProp = new Vec3Property(defaultX, new TopRightPixelOffset(), defaultWidth, "pos", f, (pos) -> {
			Entity e = (Entity) currentComponent;
			e.transform.position.set(pos);
		});
		Vec3Property sclProp = new Vec3Property(defaultX, new TopRightPixelOffset(), defaultWidth, "scl", f, (pos) -> {
			Entity e = (Entity) currentComponent;
			e.transform.scale.set(pos);
		});

		ComponentProperties props = new ComponentProperties(f) {

			@Override
			public void activate() {
				super.activate();
				Entity e = (Entity) currentComponent;
				posProp.setValue(e.transform.position);
				sclProp.setValue(e.transform.scale);
			}
		};
		props.add(posProp);
		props.add(sclProp);
		addPropertiesUI(Entity.class, props);
	}

	protected static void createModelComponentPropertiesUI() {
		ComponentProperties props = new ComponentProperties(f) {

			@Override
			public void activate() {
				super.activate();
			}
		};
		addPropertiesUI(de.kjEngine.scene.model.ModelComponent.class, props);
	}

	public static void addDefaultScene(Scene scene) {
		DirectionalLightComponent sun = new DirectionalLightComponent(Vec3.scale(3f), Vec3.create(0.5f, -1f, 0.5f).normalise());

		DirectionalLightShadowMapCascadeComponent c1 = new DirectionalLightShadowMapCascadeComponent(2048, 0.3f, 1f, 2f);
		c1.name = "lod3";
		sun.add(c1);

		DirectionalLightShadowMapCascadeComponent c2 = new DirectionalLightShadowMapCascadeComponent(2048, 0.1f, 0.3f, 4f);
		c2.name = "lod2";
		sun.add(c2);

		DirectionalLightShadowMapCascadeComponent c3 = new DirectionalLightShadowMapCascadeComponent(4096, 0.02f, 0.1f, 5f);
		c3.name = "lod1";
		sun.add(c3);

		DirectionalLightShadowMapCascadeComponent c4 = new DirectionalLightShadowMapCascadeComponent(4096, 0f, 0.02f, 10f);
		c4.name = "lod0";
		sun.add(c4);

		sun.name = "sun";
		scene.root.add(sun);

		de.kjEngine.scene.model.ModelComponent cubeModel = new de.kjEngine.scene.model.ModelComponent(de.kjEngine.scene.model.Model.getDefaultCube());
		cubeModel.name = "model";
		Entity cube = new Entity(true, Vec3.create(0f, 1f, 0f)).add(cubeModel);
		cube.name = "cube";
		scene.add(cube);

		de.kjEngine.scene.model.ModelComponent planeModel = new de.kjEngine.scene.model.ModelComponent(de.kjEngine.scene.model.Model.getDefaultPlane());
		planeModel.name = "model";
		Entity plane = new Entity(true, Vec3.create(), Vec3.scale(5f)).add(planeModel);
		plane.name = "plane";
		scene.add(plane);
	}

	public static void addEditorCamera(Scene scene) {
		scene.camera = new PerspectiveCameraComponent();
		cameraEntity = new Entity(true).add(scene.camera).add(new SceneComponent<Entity, SceneComponent<Entity, ?>>(Component.EARLY) {

			@Override
			protected void update(float delta) {
				parent.transform.rotation.setIdentity();
				parent.transform.rotation.rotateX(cameraRotation.x);
				parent.transform.rotation.rotateY(cameraRotation.y);
				parent.transform.position.set(cameraPosition).add(Real.sin(-cameraRotation.y) * Real.cos(cameraRotation.x) * cameraZoom, Real.sin(cameraRotation.x) * cameraZoom,
						-Real.cos(-cameraRotation.y) * Real.cos(cameraRotation.x) * cameraZoom);
			}
		});
		scene.add(cameraEntity);
	}

	public static void addPropertiesUI(Class<?> type, Properties ui) {
		propertiesUICache.put(type, ui);
		propertiesPanel.add(ui);
		ui.setActive(false);
	}

	public static void setCurrent(Node node) {
		currentNode = node;
		currentComponent = getComponent(node);

		if (currentPropertiesUI != null) {
			currentPropertiesUI.setActive(false);
		}
		if (currentComponent.getParent() == null) {
			return;
		}
		currentPropertiesUI = propertiesUICache.get(currentComponent.getClass());
		if (currentPropertiesUI == null) {
			createPropertiesUI(currentComponent.getClass());
			currentPropertiesUI = propertiesUICache.get(currentComponent.getClass());
		}
		currentPropertiesUI.setActive(true);
		currentPropertiesUI.activate();
	}

	private static void createPropertiesUI(Class<?> type) {
		ComponentProperties props = new ComponentProperties(f) {

			@Override
			public void activate() {
				super.activate();
			}
		};
		addPropertiesUI(type, props);
	}

	public static Component<?, ?, Scene> getComponent(Node node) {
		Node root = node;
		List<Node> path = new ArrayList<>();
		while (true) {
			if (root.parentNode == null) {
				break;
			}
			path.add(root);
			root = root.parentNode;
		}
		Scene scene = SceneManager.getScene(path.get(path.size() - 1).getText());
		Component<?, ?, Scene> component = scene.root;
		for (int i = path.size() - 3; i >= 0; i--) {
			component = component.get(path.get(i).getText());
		}
		return component;
	}

	public static void addSceneToSceneGraph(Scene scene) {
		Node root = sceneGraph.root.add(scene.name);
		addComponentToSceneGraph(scene.root, root);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void addComponentToSceneGraph(Component e, Node target) {
		if (e == cameraEntity) {
			return;
		}
		Node node = target.add(e.name);
		List<Component> children = e.getAll(Component.class);
		for (Component child : children) {
			addComponentToSceneGraph(child, node);
		}
	}

	public static Entity createEntity(boolean dynamic) {
		Entity e = new Entity(true);
		EntityData data = new EntityData();
		data.dynamic = dynamic;
		e.add(data);
		return e;
	}

	public static void updateSceneGraphSlider() {
		float height = sceneGraph.getLocalTreeHeight();
		if (height < 1f) {
			sceneGraphSliderSensitivity = 0f;
			sceneGraphSlider.buttonUI.setActive(false);
		} else {
			sceneGraphSliderSensitivity = height - 1f;
			sceneGraphSlider.buttonUI.setActive(true);
			sceneGraphSlider.buttonUI.height.value = 1f / height;
			if (lastSceneGraphHeight < 1f) {
				sceneGraphSlider.setValue(1f);
			} else {
				float lastOffset = (lastSceneGraphHeight - 1f) * sceneGraphSlider.getValue();
				sceneGraphSlider.setValue(Real.clamp((lastOffset + height - lastSceneGraphHeight) / sceneGraphSliderSensitivity, 0f, 1f));
			}
		}
		lastSceneGraphHeight = height;
	}
}
