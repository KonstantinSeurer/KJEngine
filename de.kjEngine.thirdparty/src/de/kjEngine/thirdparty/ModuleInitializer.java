/**
 * 
 */
package de.kjEngine.thirdparty;

import javax.imageio.spi.IIORegistry;

import com.realityinteractive.imageio.tga.TGAImageReaderSpi;

/**
 * @author konst
 *
 */
public class ModuleInitializer {

	public static void init() {
		IIORegistry registry = IIORegistry.getDefaultInstance();
		registry.registerServiceProvider(new TGAImageReaderSpi());
	}
}
