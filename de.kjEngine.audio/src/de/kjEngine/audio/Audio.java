/**
 * 
 */
package de.kjEngine.audio;

import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import de.kjEngine.audio.sampling.Sampler;
import de.kjEngine.io.RL;
import de.kjEngine.io.ResourceNotFoundException;
import de.kjEngine.io.UnknownProtocolException;

/**
 * @author konst
 *
 */
public class Audio {

	public static enum API {
		OPENAL
	}

	private static AudioContext context;

	public static void init(AudioContext context) {
		if (Audio.context != null) {
			return;
		}
		Audio.context = context;
	}

	public static Sound createSound(Sound.Format format, byte[] data, int frequency) {
		return context.createSound(format, data, frequency);
	}

	public static Sound createSound(Sampler sampler, float length, int frequency) {
		byte[] data = new byte[(int) (length * frequency)];
		float timeStep = 1f / (float) frequency;
		for (int i = 0; i < data.length; i++) {
			data[i] = (byte) ((sampler.sample(i * timeStep) * 0.5f + 0.5) * 255f);
		}
		return context.createSound(Sound.Format.MONO8, data, frequency);
	}

	public static Sound createSound(RL rl) {
		try {
			AudioInputStream in = AudioSystem.getAudioInputStream(rl.openInputStream());
			int channels = in.getFormat().getChannels();
			int valueSize = in.getFormat().getFrameSize() / channels;
			float sampleRate = in.getFormat().getSampleRate();

			Sound.Format format = null;

			if (channels == 1) {
				if (valueSize == 1) {
					format = Sound.Format.MONO8;
				} else if (valueSize == 2) {
					format = Sound.Format.MONO16;
				}
			} else if (channels == 2) {
				if (valueSize == 1) {
					format = Sound.Format.STEREO8;
				} else if (valueSize == 2) {
					format = Sound.Format.STEREO16;
				}
			}
			
			if (format == null) {
				return null;
			}

			return createSound(format, in.readAllBytes(), (int) sampleRate);
		} catch (UnknownProtocolException e) {
			e.printStackTrace();
		} catch (ResourceNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Source createSource() {
		return context.createSource();
	}

	public static AudioContext getContext() {
		return context;
	}

	public static API getApi() {
		return context.getApi();
	}
}
