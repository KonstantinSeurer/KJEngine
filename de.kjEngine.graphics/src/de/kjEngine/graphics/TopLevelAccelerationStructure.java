/**
 * 
 */
package de.kjEngine.graphics;

import de.kjEngine.math.Mat4;

/**
 * @author konst
 *
 */
public abstract class TopLevelAccelerationStructure implements Descriptor {

	public static class Entry {
		
		public BottomLevelAccelerationStructure accelerationStructure;
		public Mat4 transform;
		
		public Entry() {
		}
		
		/**
		 * @param accelerationStructure
		 * @param transform
		 */
		public Entry(BottomLevelAccelerationStructure accelerationStructure, Mat4 transform) {
			this.accelerationStructure = accelerationStructure;
			this.transform = transform;
		}
		
		public Entry(Entry e) {
			accelerationStructure = e.accelerationStructure;
			transform = e.transform;
		}
	}

	protected final Entry[] entries;

	public TopLevelAccelerationStructure(int entryCount) {
		entries = new Entry[entryCount];
	}
	
	public int getEntryCount() {
		return entries.length;
	}
	
	public void setEntry(int i, Entry e) {
		entries[i] = e;
	}
	
	public Entry getEntry(int i) {
		return entries[i];
	}

	@Override
	public Type getType() {
		return Type.ACCELERATION_STRUCTURE;
	}
	
	public abstract void update();
}
