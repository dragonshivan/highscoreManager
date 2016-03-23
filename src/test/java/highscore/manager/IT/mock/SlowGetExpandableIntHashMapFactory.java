package highscore.manager.IT.mock;

import highscore.manager.service.datastructure.IntHashMap;
import highscore.manager.service.datastructure.IntHashMapFactory;
import highscore.manager.service.datastructure.impl.ExpandableIntHashMap;

public class SlowGetExpandableIntHashMapFactory implements IntHashMapFactory {

	private final int capacity;
	private final float loadFactorExpansionThreshold;
	
	public SlowGetExpandableIntHashMapFactory(int capacity, float loadFactorExpansionThreshold) {
		this.capacity = capacity;
		this.loadFactorExpansionThreshold = loadFactorExpansionThreshold;
	}
	
	@Override
	public IntHashMap getIntHashMap() {
		IntHashMap target = new ExpandableIntHashMap(capacity, loadFactorExpansionThreshold);
		return new SlowStreamExpandableIntHashMap(target);
	}

	@Override
	public int getCapacity() {
		return capacity;
	}
	
	public static class SlowStreamExpandableIntHashMap extends AspectableIntHashMap {

		public SlowStreamExpandableIntHashMap(IntHashMap target) {
			super(target);
		}
		
		@Override
		public void onGetEnter(int key) {
			try {
				if(Thread.currentThread().getName().equals(ThreadNames.READ_THREAD.name())) {
					Thread.sleep(SLOW_THREAD_SIMULATED_DELAY_MS);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
