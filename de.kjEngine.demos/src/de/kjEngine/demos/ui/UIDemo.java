/**
 * 
 */
package de.kjEngine.demos.ui;

import java.util.Set;

import de.kjEngine.component.Component.UpdateMode;
import de.kjEngine.core.Engine;
import de.kjEngine.core.Engine.Settings.Window.RefreshMode;
import de.kjEngine.core.StateAdapter;
import de.kjEngine.graphics.Color;
import de.kjEngine.io.JarProtocolImplementation;
import de.kjEngine.io.RL;
import de.kjEngine.math.Real;
import de.kjEngine.renderer.Pipeline;
import de.kjEngine.renderer.Renderable.RenderImplementation.ID;
import de.kjEngine.renderer.RenderingContext;
import de.kjEngine.ui.Slider.Orientation;
import de.kjEngine.ui.SplitPanel;
import de.kjEngine.ui.UI;
import de.kjEngine.ui.UIComponent;
import de.kjEngine.ui.UIFactory;
import de.kjEngine.ui.UIScene;
import de.kjEngine.ui.UISceneManager;
import de.kjEngine.ui.Window;
import de.kjEngine.ui.model.StandartMaterial;
import de.kjEngine.ui.transform.ParentSize;
import de.kjEngine.ui.transform.PixelOffset;
import de.kjEngine.ui.transform.PixelSize;
import de.kjEngine.util.Timer;

/**
 * @author konst
 *
 */
public class UIDemo {

	public static void main(String[] args) {
		JarProtocolImplementation.registerClassLoader("app", UIDemo.class);
		Engine.Settings settings = new Engine.Settings();
		settings.window.refreshMode = RefreshMode.UNLIMITED;
		settings.window.resizable = true;
		settings.stateHandlers.add(new StateAdapter() {

			@Override
			public void init() {
				Timer.start();
				
				UIScene scene = new UIScene();
				UIFactory f = UIFactory.createDefault();

				scene.add(f.button(new PixelOffset(60f), new PixelOffset(200f), new PixelSize(150f), new PixelSize(150f), (button) -> System.out.println("pressed 1")));
				scene.add(f.button(new PixelOffset(10f), new PixelOffset(150f), new PixelSize(150f), new PixelSize(150f), (button) -> System.out.println("pressed 2")));
				scene.add(f.label(new PixelOffset(10f), new PixelOffset(40f), new PixelSize(200f), new PixelSize(100f), new StandartMaterial(Color.GRAY_03.getTexture()),
						"lol lol lol lol lol lol lol lol lol lol", true, true, true).add(new UIComponent<UI, UIComponent<UI, ?>>(UpdateMode.EARLY) {

							float t;

							@Override
							protected void update(float delta) {
								super.update(delta);
								parent.width.value = Real.sin(t) * 50f + 150f;
								t += delta;
							}
						}));
				scene.add(f.slider(new PixelOffset(10f), new PixelOffset(10f), new PixelSize(200f), new PixelSize(20f), new StandartMaterial(Color.GRAY_02.getTexture()), Orientation.HORIZONTAL,
						new PixelSize(10f), new ParentSize(1f), (slider) -> System.out.println(slider.getValue())));

				SplitPanel splitPanel = f.splitPanel(new PixelOffset(10f), new PixelOffset(360f), new PixelSize(200f), new PixelSize(100f), new StandartMaterial(Color.GRAY_02.getTexture()),
						Orientation.HORIZONTAL, new PixelSize(5f));
				splitPanel.setValue(0.5f);
				splitPanel.left.add(f.label(new PixelOffset(), new PixelOffset(), new ParentSize(1f), new ParentSize(1f), "ha ha ha ha ha ha ha ha ha ha ha", false, false, true));
				splitPanel.right.add(f.label(new PixelOffset(), new PixelOffset(), new ParentSize(1f), new ParentSize(1f), "ho ho ho ho ho ho ho ho ho ho ho", false, false, true));
				scene.add(splitPanel);

				UISceneManager.addScene("ui", scene);
				UISceneManager.setScene("ui");
				
				Timer.printPassed();
			}

			@Override
			public void initRenderer(Set<ID> requiredImplementations) {
				Pipeline pipeline = Pipeline.create(RL.create("jar://app/de/kjEngine/demos/ui/pipeline.ppl"), Window.getWidth(), Window.getHeight());
				RenderingContext.addPipeline("pipeline", pipeline);
				RenderingContext.setPipeline("pipeline");

				pipeline.getRequiredRenderImplementations(requiredImplementations);
			}
		});
		Engine.start(settings, UIDemo.class.getModule());
	}
}
