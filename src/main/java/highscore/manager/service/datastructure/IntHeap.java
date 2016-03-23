package highscore.manager.service.datastructure;

public interface IntHeap {
	
	static final byte EMPTY_LOCATION = -1;
	static final byte NOT_FOUND = -2;
	
	/**
	 * 
	 * @param value the value that is used for heap ordering
	 * @param associatedValue and associated value for the first value
	 */
	void push(int value, int associatedValue);
	/**
	 * 
	 * @return returns and remove the ordered value (according to heap direction). The array is of fixed size 2, and contains the values that were used in the push(...) call
	 */
	int[] pop();
	/**
	 * 
	 * @return returns (and doesn't remove) the ordered value (according to heap direction). The array is of fixed size 2, and contains the values that were used in the push(...) call
	 */
	int[] peek();	
	boolean isEmpty();
	boolean isFull();
	byte getSize();
	
}
