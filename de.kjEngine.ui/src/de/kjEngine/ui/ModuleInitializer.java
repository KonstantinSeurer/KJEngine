/**
 * 
 */
package de.kjEngine.ui;

import org.json.JSONObject;

import de.kjEngine.io.JarProtocolImplementation;
import de.kjEngine.renderer.Pipeline;
import de.kjEngine.renderer.Pipeline.Provider;
import de.kjEngine.renderer.Stage;
import de.kjEngine.renderer.Stage.InputProvider;
import de.kjEngine.ui.renderer.UIModelRenderer;

/**
 * @author konst
 *
 */
public class ModuleInitializer {

	public static void init() {
		JarProtocolImplementation.registerClassLoader("ui", ModuleInitializer.class);
		
		Pipeline.registerStageProvider("uiModelRenderer", new Provider() {

			@Override
			public Stage create(InputProvider input, JSONObject obj) {
				return new UIModelRenderer(input);
			}
		});
	}
}
