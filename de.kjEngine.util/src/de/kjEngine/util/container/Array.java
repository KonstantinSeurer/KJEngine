/**
 * 
 */
package de.kjEngine.util.container;

import java.util.Arrays;
import java.util.Iterator;

/**
 * @author konst
 *
 */
public class Array<T> implements ReadWriteArray<T> {

	private static final Growth DEFAULT_GROWTH = new Growth() {

		private static final int MAX_ARRAY_LENGTH = Integer.MAX_VALUE - 8;

		@Override
		public int newLength(int length, int minGrowthAmount) {
			int newLength = Math.max(minGrowthAmount, length >> 1) + length;
			if (newLength - MAX_ARRAY_LENGTH <= 0) {
				return newLength;
			}
			return hugeLength(length, minGrowthAmount);
		}

		private final int hugeLength(int oldLength, int minGrowth) {
			int minLength = oldLength + minGrowth;
			if (minLength < 0) {
				throw new OutOfMemoryError("Required array length too large");
			}
			if (minLength <= MAX_ARRAY_LENGTH) {
				return MAX_ARRAY_LENGTH;
			}
			return Integer.MAX_VALUE;
		}
	};

	private static final int DEFAULT_CAPACITY = 8;

	private Growth growth;
	private Object[] data;
	private int length;

	public Array() {
		this(DEFAULT_CAPACITY);
	}

	public Array(Iterable<T> src) {
		this();
		for (T e : src) {
			add(e);
		}
	}

	@SuppressWarnings("unchecked")
	public Array(T... data) {
		this(data.length);
		for (int i = 0; i < data.length; i++) {
			add(data[i]);
		}
	}

	public Array(int capacity) {
		this(capacity, DEFAULT_GROWTH);
	}

	public Array(Growth growth) {
		this(DEFAULT_CAPACITY, growth);
	}

	public Array(int capacity, Growth growth) {
		data = new Object[capacity];
		this.growth = growth;
	}

	@Override
	public T add(T e) {
		if (length == data.length) {
			grow(1);
		}
		data[length++] = e;
		return e;
	}

	@Override
	public void add(ReadArray<T> e) {
		if (data.length < length + e.length()) {
			grow(length + e.length() - data.length);
		}
		for (int i = 0; i < e.length(); i++) {
			data[length++] = e.get(i);
		}
	}

	@Override
	public T add(int i, T e) {
		if (length == data.length) {
			grow(1);
		}
		if (i < length) {
			System.arraycopy(data, i, data, i + 1, length - i);
		}
		data[i] = e;
		length++;
		return e;
	}

	@Override
	public void add(int i, ReadArray<T> e) {
		if (data.length < length + e.length()) {
			grow(length + e.length() - data.length);
		}
		if (i < length) {
			System.arraycopy(data, i, data, i + e.length(), length - i);
		}
		for (int j = 0; j < e.length(); j++) {
			data[i + j] = e.get(j);
		}
		length += e.length();
	}

	private final void grow(int amount) {
		data = Arrays.copyOf(data, growth.newLength(data.length, amount));
	}

	@Override
	public void remove(T e) {
		remove(indexOf(e));
	}

	@Override
	public void remove(ReadArray<T> e) {
		for (int i = 0; i < e.length(); i++) {
			remove(e.get(i));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public T remove(int i) {
		Object e = data[i];
		remove(i, 1);
		return (T) e;
	}

	@Override
	public void remove(int i, int count) {
		length -= count;
		if (i < length) {
			System.arraycopy(data, i + count, data, i, length - i);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void removeAll(BooleanFunction<T> remove) {
		for (int i = length - 1; i >= 0; i--) {
			if (remove.get((T) data[i])) {
				remove(i);
			}
		}
	}

	@Override
	public void clear(boolean eraseElements) {
		if (eraseElements) {
			for (int i = 0; i < length; i++) {
				data[i] = null;
			}
		}
		length = 0;
	}

	@Override
	public T set(int i, T e) {
		data[i] = e;
		return e;
	}

	@Override
	public int length() {
		return length;
	}

	@Override
	public boolean isEmpty() {
		return length == 0;
	}

	@Override
	public boolean isNotEmpty() {
		return length > 0;
	}

	@Override
	public boolean contains(T e) {
		for (int i = 0; i < length; i++) {
			if (data[i] == e) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int indexOf(T e) {
		for (int i = 0; i < length; i++) {
			if (data[i] == e) {
				return i;
			}
		}
		return -1;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T get(int i) {
		return (T) data[i];
	}

	@SuppressWarnings("unchecked")
	@Override
	public T get(BooleanFunction<T> find) {
		for (int i = 0; i < length; i++) {
			if (find.get((T) data[i])) {
				return (T) data[i];
			}
		}
		return null;
	}

	@Override
	public ReadWriteArray<T> view(int offset, int length) {
		return new View(offset, length);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T[] get(T[] target) {
		int length = Math.min(this.length, target.length);
		for (int i = 0; i < length; i++) {
			target[i] = (T) data[i];
		}
		return target;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.deepHashCode(data);
		result = prime * result + length;
		return result;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Array other = (Array) obj;
		if (!Arrays.deepEquals(data, other.data))
			return false;
		if (length != other.length)
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("[");
		for (int i = 0; i < length; i++) {
			sb.append(data[i]);
			if (i < length - 1) {
				sb.append(", ");
			}
		}
		sb.append("]");
		return sb.toString();
	}

	private class View implements ReadWriteArray<T> {

		int offset, length;

		View(int offset, int length) {
			this.offset = offset;
			this.length = length;
		}

		@Override
		public T add(T e) {
			Array.this.add(offset + length, e);
			length++;
			return e;
		}

		@Override
		public void add(ReadArray<T> e) {
			Array.this.add(offset + length, e);
			length += e.length();
		}

		@Override
		public T add(int i, T e) {
			Array.this.add(offset + i, e);
			length++;
			return e;
		}

		@Override
		public void add(int i, ReadArray<T> e) {
			Array.this.add(offset + i, e);
			length += e.length();
		}

		@Override
		public void remove(T e) {
			remove(indexOf(e));
			length--;
		}

		@Override
		public void remove(ReadArray<T> e) {
			for (int i = 0; i < e.length(); i++) {
				remove(e.get(i));
			}
		}

		@Override
		public T remove(int i) {
			length--;
			return Array.this.remove(offset + i);
		}

		@Override
		public void remove(int i, int count) {
			Array.this.remove(offset + i, count);
			length -= count;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public void removeAll(BooleanFunction<T> remove) {
			for (int i = length - 1; i >= 0; i--) {
				if (remove.get((T) data[i])) {
					remove(i);
				}
			}
		}

		@Override
		public void clear(boolean eraseElements) {
			remove(0, length);
			length = 0;
		}

		@Override
		public T set(int i, T e) {
			Array.this.set(offset + i, e);
			return e;
		}

		@Override
		public View view(int offset, int length) {
			return new View(this.offset + offset, length);
		}

		@Override
		public int length() {
			return length;
		}

		@Override
		public boolean isEmpty() {
			return length == 0;
		}

		@Override
		public boolean isNotEmpty() {
			return length > 0;
		}

		@Override
		public boolean contains(T e) {
			for (int i = 0; i < length; i++) {
				if (data[offset + i] == e) {
					return true;
				}
			}
			return false;
		}

		@Override
		public int indexOf(T e) {
			for (int i = 0; i < length; i++) {
				if (data[offset + i] == e) {
					return i;
				}
			}
			return -1;
		}

		@Override
		public T get(int i) {
			return Array.this.get(offset + i);
		}

		@SuppressWarnings("unchecked")
		@Override
		public T[] get(T[] target) {
			int length = Math.min(this.length, target.length);
			for (int i = 0; i < length; i++) {
				target[i] = (T) data[offset + i];
			}
			return target;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getEnclosingInstance().hashCode();
			result = prime * result + length;
			result = prime * result + offset;
			return result;
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			View other = (View) obj;
			if (!getEnclosingInstance().equals(other.getEnclosingInstance()))
				return false;
			if (length != other.length)
				return false;
			if (offset != other.offset)
				return false;
			return true;
		}

		@SuppressWarnings("rawtypes")
		private Array getEnclosingInstance() {
			return Array.this;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder("[");
			for (int i = 0; i < length; i++) {
				sb.append(data[offset + i]);
				if (i < length - 1) {
					sb.append(", ");
				}
			}
			sb.append("]");
			return sb.toString();
		}

		@SuppressWarnings("unchecked")
		@Override
		public T get(BooleanFunction<T> find) {
			for (int i = 0; i < length; i++) {
				if (find.get((T) data[offset + i])) {
					return (T) data[offset + i];
				}
			}
			return null;
		}

		@Override
		public Iterator<T> iterator() {
			return new ArrayIterator(this);
		}
	}

	@Override
	public Iterator<T> iterator() {
		return new ArrayIterator(this);
	}

	private class ArrayIterator implements Iterator<T> {

		ReadArray<T> array;
		int i;

		public ArrayIterator(ReadArray<T> array) {
			this.array = array;
		}

		@Override
		public boolean hasNext() {
			return i < array.length();
		}

		@Override
		public T next() {
			return array.get(i++);
		}
	}
}
