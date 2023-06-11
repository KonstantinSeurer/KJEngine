/**
 * 
 */
package de.kjEngine.audio.openal;

import de.kjEngine.audio.Audio.API;
import de.kjEngine.audio.AudioContext;
import de.kjEngine.audio.Sound;
import de.kjEngine.audio.Source;

/**
 * @author konst
 *
 */
public class OpenALContext extends AudioContext {
	
	private long device;

	public OpenALContext() {
//		device = ALC10.alcOpenDevice((ByteBuffer) null);
//		long context = ALC10.alcCreateContext(device, (int[]) null);
//		ALC10.alcMakeContextCurrent(context);
//		AL.setCurrentProcess(AL.createCapabilities(ALC.createCapabilities(device)));
	}

	@Override
	public API getApi() {
		return API.OPENAL;
	}

	@Override
	public Sound createSound(Sound.Format format, byte[] data, int frequency) {
		return new OpenALSound(format, data, frequency);
	}

	@Override
	public Source createSource() {
		return new OpenALSource();
	}

	@Override
	public void dispose() {
//		ALC10.alcCloseDevice(device);
	}
}
