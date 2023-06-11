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
public abstract class Source implements Disposable, Copy<Source> {

	public Source() {
	}
	
	public abstract void queueSound(Sound sound);
}
