package highscore.manager.service.datastructure.impl;

import highscore.manager.service.datastructure.IntHashMap;
import highscore.manager.service.datastructure.IntHashMapFactory;

public class CappedIntHashMapFactory implements IntHashMapFactory {

	private final byte capacity;
	
	public CappedIntHashMapFactory(byte capacity) {
		this.capacity = capacity;
	}
	
	@Override
	public IntHashMap getIntHashMap() {
		return new CappedIntHashMap(capacity);
	}

	@Override
	public int getCapacity() {
		return capacity;
	}

}
