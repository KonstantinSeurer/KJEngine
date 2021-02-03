/**
 * 
 */
package de.kjEngine.core;

import de.kjEngine.audio.AudioContext;
import de.kjEngine.graphics.GraphicsContext;

/**
 * @author konst
 *
 */
public interface ImplementationProvider {

	public GraphicsContext createGraphicsContext();
	
	public AudioContext createAudioContext();
}
