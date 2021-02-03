/**
 * 
 */
package de.kjEngine.demos.fluidsim;

import java.util.HashSet;
import java.util.Set;

import de.kjEngine.awt.Gui;
import de.kjEngine.core.ApplicationEventAdapter;
import de.kjEngine.core.Engine;
import de.kjEngine.graphics.Texture2DDataProvider;
import de.kjEngine.io.JarProtocolImplementation;
import de.kjEngine.math.Vec4;
import de.kjEngine.scene.SceneGraphItem.GPUImplementation.ID;
import de.kjEngine.scene.physics2d.GridFluidSimulation;
import de.kjEngine.ui.Window;
import de.kjEngine.ui.Window.RefreshMode;

/**
 * @author konst
 *
 */
public class FluidSim2DDemo {

	public static void main() {
		main(null);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JarProtocolImplementation.registerClassLoader("app", FluidSim2DDemo.class);
		Engine.Settings settings = new Engine.Settings();
		settings.window.refreshMode = RefreshMode.UNLIMITED;
		settings.window.width = 1600;
		settings.window.height = 800;
		settings.application = new ApplicationEventAdapter() {
			
			GridFluidSimulation sim;

			@Override
			public void init() {
				sim = new GridFluidSimulation(512, 512, new Texture2DDataProvider() {
					
					@Override
					public void get(int x, int y, Vec4 target) {
						target.y = -0.1f;
					}
				}, new Texture2DDataProvider() {
					
					@Override
					public void get(int x, int y, Vec4 target) {
						target.x = 0f;
						if (x > 200 && x < 512 - 200 && y > 200) {
							target.x = 1f;
						}
						target.w = 1f;
					}
				});
				
				Gui.addGui(Window.getRootPanel(), 0f, 0f, 1f, 2f, sim.getPressure());
				Gui.addGui(Window.getRootPanel(), 1f, 0f, 1f, 2f, sim.getVelocity());
			}

			@Override
			public void update() {
				sim.update();
			}

			@Override
			public Set<ID> initRenderer() {
				return new HashSet<>();
			}
		};
		Engine.start(settings);
	}
}
