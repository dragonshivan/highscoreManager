package highscore.manager.service.datastructure.impl;

import highscore.manager.service.datastructure.IntHashMap;
import highscore.manager.service.datastructure.IntHashMapFactory;

public class ExpandableIntHashMapFactory implements IntHashMapFactory {
	
	private final int initialCapacity;
	private final float loadFactorExpansionThreshold;
	
	public ExpandableIntHashMapFactory(int initialCapacity, float loadFactorExpansionThreshold) {
		this.initialCapacity = initialCapacity;
		this.loadFactorExpansionThreshold = loadFactorExpansionThreshold;
	}

	@Override
	public IntHashMap getIntHashMap() {
		return new ExpandableIntHashMap(initialCapacity, loadFactorExpansionThreshold);
	}

	@Override
	public int getCapacity() {
		throw new UnsupportedOperationException("This factory returns an IntHashMap of variable capacity");
	}

}
