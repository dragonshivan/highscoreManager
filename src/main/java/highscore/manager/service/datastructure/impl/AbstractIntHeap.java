package highscore.manager.service.datastructure.impl;

import java.util.Arrays;

import highscore.manager.service.datastructure.IntHeap;

public abstract class AbstractIntHeap implements IntHeap {

	private final byte capacity;
	private final int[] storage;
	private final int[] associatedValues;
	private byte size = 0;
	
	public AbstractIntHeap(byte capacity) {
		this.capacity = capacity;
		storage = new int[capacity];
		Arrays.fill(storage, EMPTY_LOCATION);
		associatedValues = new int[capacity];
		Arrays.fill(associatedValues, EMPTY_LOCATION);
	}

	@Override
	public void push(int value, int associatedValue) {
		if(isFull()) {
			throw new RuntimeException("Heap is full!");
		}
		storage[size] = value;
		associatedValues[size] = associatedValue;
		size++;
		replaceUp();
	}

	@Override
	public int[] pop() {
		if(isEmpty()) {
			throw new RuntimeException("Heap is empty!");
		}
		int top = storage[0];
		int associatedTop = associatedValues[0];
		
		storage[0] = storage[size - 1];
		storage[size - 1] = EMPTY_LOCATION;
    	
    	associatedValues[0] = associatedValues[size - 1];
    	associatedValues[size - 1] = EMPTY_LOCATION;
    	
    	size--;
    	
    	replaceDown();
		
		return new int[]{top, associatedTop};
	}

	@Override
	public int[] peek() {
		if(isEmpty()) {
			throw new RuntimeException("Heap is empty!");
		}
		return new int[]{storage[0], associatedValues[0]};
	}
	
	@Override
	public boolean isEmpty() {
		return size == 0;
	}
	
	@Override
	public boolean isFull() {
		return size == capacity;
	}
	
	public byte getSize() {
		return size;
	}
	
	protected abstract int compare(int v1, int v2);
	
	private byte getLeftChildPos(byte nodePos) {
		byte childPos = (byte) (2 * nodePos + 1);
		if(childPos >= capacity || storage[childPos] == EMPTY_LOCATION) {
			return NOT_FOUND; 
		}
		return childPos;
	}

	private byte getRightChildPos(byte nodePos) {
		byte childPos = (byte) (2 * nodePos + 2);
		if(childPos >= capacity || storage[childPos] == EMPTY_LOCATION) {
			return NOT_FOUND; 
		}
		return childPos;
	}

	private byte getParentPos(byte nodePos) {
		byte parentPos = (byte) ((nodePos - 1) / 2);
		if(parentPos < 0) {
			return NOT_FOUND;
		}
		return parentPos;
	}
	
	private void replaceUp() {
		byte pos = (byte) (size - 1);
        
        while (getParentPos(pos) != NOT_FOUND
                //&& (storage[getParentPos(pos)] > (storage[pos]))) {
        		&& compare(storage[getParentPos(pos)],storage[pos]) > 0) {
            swap(pos, getParentPos(pos));
            pos = getParentPos(pos);
        }        
	}
	
	private void replaceDown() {
		byte pos = 0;
        
        while (getLeftChildPos(pos) != NOT_FOUND) {
            byte smallerChild = getLeftChildPos(pos);
            
            if (getRightChildPos(pos) != NOT_FOUND
//                && storage[getLeftChildPos(pos)] > (storage[getRightChildPos(pos)])) {
            	&& compare(storage[getLeftChildPos(pos)], storage[getRightChildPos(pos)]) > 0) {
                smallerChild = getRightChildPos(pos);
            } 
            
//            if (storage[pos] > storage[smallerChild]) {
            if (compare(storage[pos], storage[smallerChild]) > 0) {
                swap(pos, smallerChild);
            } else {
                break;
            }
            
            pos = smallerChild;
        }       
	}
	
	private void swap(byte posA, byte posB) {
		int valA = storage[posA];
		storage[posA] = storage[posB];
		storage[posB] = valA;
		
		int assocValA = associatedValues[posA];
		associatedValues[posA] = associatedValues[posB];
		associatedValues[posB] = assocValA;
	}
}
