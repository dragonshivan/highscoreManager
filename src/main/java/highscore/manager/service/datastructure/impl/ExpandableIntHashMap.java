package highscore.manager.service.datastructure.impl;

import java.util.Arrays;

import highscore.manager.service.datastructure.IntHashMap;

public class ExpandableIntHashMap implements IntHashMap {
	
	private static final byte BUCKETS_EXPANSION_FACTOR = 2;
	
	private final float loadFactorExpansionThreshold;
	
	private int[][] buckets;
	private int capacity;
	private int size;
	private int nonEmptyBucketCount;
	
	public ExpandableIntHashMap(int initialCapacity, float loadFactorExpansionThreshold) {
		this.loadFactorExpansionThreshold = loadFactorExpansionThreshold;
		this.capacity = initialCapacity;
		buckets = new int[capacity][];
	}
	
	private ExpandableIntHashMap(int newInitialCapacity, ExpandableIntHashMap oldMap) {
		this(newInitialCapacity, oldMap.loadFactorExpansionThreshold);
		oldMap.stream((k, v)->putWithoutResize(k, v));
	}
	
	@Override
	public void put(int key, int value) {
		putWithoutResize(key, value);
		resizeIfNecessary();
	}
	
	private void putWithoutResize(int key, int value) {
		int bucketPos = key  % capacity;
		int[] bucket = buckets[bucketPos];
		if(bucket == null) {
			bucket = new int[2];
			nonEmptyBucketCount++;
			bucket[0] = key;
			bucket[1] = value;
			size++;
			buckets[bucketPos] = bucket;
			return;
		}
		
		int emptyLocationPos = NOT_FOUND;
		for(int i = 0; i < bucket.length - 1; i+=2) {
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
				
		int newEmptyPos = bucket.length;
		buckets[bucketPos] = Arrays.copyOf(bucket, bucket.length + 2);
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
		for(int i = 0; i < bucket.length - 1; i+=2) {
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
		for(int i = 0; i < bucket.length - 1; i+=2) {
			if(bucket[i] == key) {
				bucket[i] = EMPTY_LOCATION;
				bucket[i + 1] = EMPTY_LOCATION;
				if(isBucketEmpty(bucket)) {
					nonEmptyBucketCount--;
				}
				size--;
				return;
			}
		}
	}
	
	@Override
	public void stream(EntryProcessor entryProcessor) {
		for(int i = 0; i < buckets.length; i++) {
			int[] bucket = buckets[i];
			if(bucket != null) {
				for(int j = 0; j < bucket.length - 1; j+=2) {
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
	
	private float getLoadFactor() {
		return (float)size/nonEmptyBucketCount;
	}
	
	private boolean isBucketEmpty(int[] bucket) {
		boolean empty = true;
		for(int i = 0; i < bucket.length; i+=2) {
			if(bucket[i] != EMPTY_LOCATION) {
				empty = false;
			}
		}
		return empty;
	}
	
	public String toString() {
		String desc = "Entry count: " + size + "/" + capacity + "\n";
		for(int i = 0; i < buckets.length; i++) {
			if(buckets[i] != null) {
				desc += "Bucket #" + i + " (entries space = " + buckets[i].length / 2 + "): ";
				for(int j = 0; j < buckets[i].length - 1; j+=2) {
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
	
	private void resizeIfNecessary() {
		if(getLoadFactor() > loadFactorExpansionThreshold) {
			if(capacity == Integer.MAX_VALUE) {
				return;
			}
			int newCapacity = Math.min(Integer.MAX_VALUE, capacity * BUCKETS_EXPANSION_FACTOR);
			ExpandableIntHashMap newMap = new ExpandableIntHashMap(newCapacity, this);
			buckets = newMap.buckets;
			capacity = newMap.capacity;
			size = newMap.size;
			nonEmptyBucketCount = newMap.nonEmptyBucketCount;
		}
	}

}
