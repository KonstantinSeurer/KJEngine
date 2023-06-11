/**
 * 
 */
package de.kjEngine.audio.openal;

import org.lwjgl.openal.AL11;
import org.lwjgl.system.MemoryUtil;

import de.kjEngine.audio.Sound;

/**
 * @author konst
 *
 */
public class OpenALSound extends Sound {
	
	private int handle;
	
	public OpenALSound(Sound.Format format, byte[] data, int frequency) {
		super(format, data, frequency);
		handle = AL11.alGenBuffers();
		int alFormat = 0;
		switch (format) {
		case MONO16:
			alFormat = AL11.AL_FORMAT_MONO16;
			break;
		case MONO8:
			alFormat = AL11.AL_FORMAT_MONO8;
			break;
		case STEREO16:
			alFormat = AL11.AL_FORMAT_STEREO16;
			break;
		case STEREO8:
			alFormat = AL11.AL_FORMAT_STEREO8;
			break;
		}
		AL11.alBufferData(handle, alFormat, MemoryUtil.memAlloc(data.length).put(0, data), frequency);
	}
	
	public OpenALSound(Sound.Format format, byte[] data, int frequency, int handle) {
		super(format, data, frequency);
		this.handle = handle;
	}

	@Override
	public void dispose() {
		AL11.alDeleteBuffers(handle);
	}

	public int getHandle() {
		return handle;
	}

	@Override
	public Sound deepCopy() {
		return new OpenALSound(format, data, frequency);
	}

	@Override
	public Sound shallowCopy() {
		return new OpenALSound(format, data, frequency, handle);
	}
}
