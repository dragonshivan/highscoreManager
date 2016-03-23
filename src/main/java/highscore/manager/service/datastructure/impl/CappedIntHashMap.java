package highscore.manager.service.datastructure.impl;

import java.util.Arrays;

import highscore.manager.service.datastructure.IntHashMap;


public class CappedIntHashMap implements IntHashMap {
	
	private final byte capacity;
	
	private byte size;
	
	private final int[][] buckets;
	
	public CappedIntHashMap(byte capacity) {
		this.capacity = capacity;
		buckets = new int[capacity][];
	}
	
	private CappedIntHashMap(byte capacity, int[][] buckets, byte size) {
		this.capacity = capacity;
		this.buckets = buckets;
		this.size = size;
	}
	
	@Override
	public void put(int key, int value) {
		int bucketPos = key  % capacity;
		int[] bucket = buckets[bucketPos];
		if(bucket == null) {
			bucket = new int[2];
			bucket[0] = key;
			bucket[1] = value;
			size++;
			buckets[bucketPos] = bucket;
			return;
		}
		
		int emptyLocationPos = NOT_FOUND;
		for(byte i = 0; i < bucket.length - 1; i+=2) {
			int existingKey = bucket[i];	
			if(existingKey == key) {
				bucket[i] = key;
				bucket[i + 1] = value;
				return;
			} else if(emptyLocationPos == NOT_FOUND && existingKey == EMPTY_LOCATION) {
				emptyLocationPos = i;
			}
		}
		
		if(emptyLocationPos != NOT_FOUND) {
			bucket[emptyLocationPos] = key;
			bucket[emptyLocationPos + 1] = value;
			size++;
			return;
		} 
		if(size == capacity) {
			return;
		}
		
		int newEmptyPos = bucket.length;
		buckets[bucketPos] = Arrays.copyOf(bucket, Math.min(capacity * 2, bucket.length + 2));
		buckets[bucketPos][newEmptyPos] = key;
		buckets[bucketPos][newEmptyPos + 1] = value;
		size++;
	}
	
	@Override
	public int get(int key) {
		int bucketPos = key % capacity;
		int[] bucket = buckets[bucketPos];
		if(bucket == null) {
			return EMPTY_LOCATION;
		}
		for(byte i = 0; i < bucket.length - 1; i+=2) {
			if(bucket[i] == key) {
				return bucket[i + 1];
			}
		}
		return EMPTY_LOCATION;
	}
	
	@Override
	public void remove(int key) {
		int bucketPos = key % capacity;
		int[] bucket = buckets[bucketPos];
		if(bucket == null) {
			return;
		}
		for(byte i = 0; i < bucket.length - 1; i+=2) {
			if(bucket[i] == key) {
				bucket[i] = EMPTY_LOCATION;
				bucket[i + 1] = EMPTY_LOCATION;
				size--;
				return;
			}
		}
	}
	
	@Override
	public void stream(EntryProcessor entryProcessor) {
		for(byte i = 0; i < buckets.length; i++) {
			int[] bucket = buckets[i];
			if(bucket != null) {
				for(byte j = 0; j < bucket.length - 1; j+=2) {
					if(bucket[j] != EMPTY_LOCATION) {
						entryProcessor.process(bucket[j], bucket[j + 1]);
					}
				}
			}
		}
	}
	
	@Override
	public int getSize() {
		return size;
	}
	
	@Override
	public IntHashMap deepCopy() {
		int[][] bucketsCopy = new int[capacity][];
		for(byte i = 0; i < bucketsCopy.length; i++) {
			int[] originalBucket = buckets[i];
			if(originalBucket != null) {
				bucketsCopy[i] = Arrays.copyOf(originalBucket, originalBucket.length);
			}
		}
		return new CappedIntHashMap(capacity, bucketsCopy, size);
	}
	
	private float getLoadFactor() {
		byte bucketsCount = 0;
		for(byte i = 0; i < capacity; i++) {
			if(buckets[i] != null) {
				bucketsCount++;
			}
		}
		return (float)size/bucketsCount;
	}
	
	public String toString() {
		String desc = "Entry count: " + size + "/" + capacity + "\n";
		for(byte i = 0; i < buckets.length; i++) {
			if(buckets[i] != null) {
				desc += "Bucket #" + i + " (entries space = " + buckets[i].length / 2 + "): ";
				for(byte j = 0; j < buckets[i].length - 1; j+=2) {
					desc += "[K=" + buckets[i][j] + " V=" + buckets[i][j + 1] + "]";
				}
				desc += "\n";
			} else {
				desc += "Bucket #" + i + ": null\n";
			}
		}
		desc += "Load factor " + getLoadFactor();
		return desc;
	}
}
