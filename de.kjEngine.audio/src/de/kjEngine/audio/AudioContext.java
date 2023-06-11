/**
 * 
 */
package de.kjEngine.audio;

import de.kjEngine.util.Disposable;

/**
 * @author konst
 *
 */
public abstract class AudioContext implements Disposable {

	public AudioContext() {
	}
	
	public abstract Audio.API getApi();
	
	public abstract Sound createSound(Sound.Format format, byte[] data, int frequency);
	
	public abstract Source createSource();
}
