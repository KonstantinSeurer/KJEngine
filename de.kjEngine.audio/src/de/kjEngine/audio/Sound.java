/**
 * 
 */
package de.kjEngine.audio;

import de.kjEngine.util.Copy;
import de.kjEngine.util.Disposable;

/**
 * @author konst
 *
 */
public abstract class Sound implements Disposable, Copy<Sound> {
	
	public static enum Format {
		MONO8, MONO16, STEREO8, STEREO16
	}
	
	protected Format format;
	protected byte[] data;
	protected int frequency;

	/**
	 * @param format
	 * @param data
	 * @param frequency
	 */
	public Sound(Format format, byte[] data, int frequency) {
		this.format = format;
		this.data = data;
		this.frequency = frequency;
	}

	/**
	 * @return the format
	 */
	public Format getFormat() {
		return format;
	}

	/**
	 * @return the data
	 */
	public byte[] getData() {
		return data;
	}

	/**
	 * @return the frequency
	 */
	public int getFrequency() {
		return frequency;
	}
}
