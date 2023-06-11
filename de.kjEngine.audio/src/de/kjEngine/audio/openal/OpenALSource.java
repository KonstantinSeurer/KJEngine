/**
 * 
 */
package de.kjEngine.audio.openal;

import org.lwjgl.openal.AL11;

import de.kjEngine.audio.Sound;
import de.kjEngine.audio.Source;

/**
 * @author konst
 *
 */
public class OpenALSource extends Source {

	private int handle;

	public OpenALSource() {
		handle = AL11.alGenSources();
	}
	
	public OpenALSource(int handle) {
		this.handle = handle;
	}

	@Override
	public void queueSound(Sound sound) {
		int buffer = ((OpenALSound) sound).getHandle();
		AL11.alSourceQueueBuffers(handle, buffer);
		AL11.alSourcePlay(handle);
	}

	@Override
	public void dispose() {
		AL11.alDeleteSources(handle);
	}

	public int getHandle() {
		return handle;
	}

	@Override
	public Source deepCopy() {
		return new OpenALSource();
	}

	@Override
	public Source shallowCopy() {
		return new OpenALSource(handle);
	}
}
