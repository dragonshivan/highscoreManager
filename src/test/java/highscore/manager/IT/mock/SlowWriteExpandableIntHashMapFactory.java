package highscore.manager.IT.mock;

import highscore.manager.service.datastructure.IntHashMap;
import highscore.manager.service.datastructure.IntHashMapFactory;
import highscore.manager.service.datastructure.impl.ExpandableIntHashMap;

public class SlowWriteExpandableIntHashMapFactory implements IntHashMapFactory {

	private final int capacity;
	private final float loadFactorExpansionThreshold;
	
	public SlowWriteExpandableIntHashMapFactory(int capacity, float loadFactorExpansionThreshold) {
		this.capacity = capacity;
		this.loadFactorExpansionThreshold = loadFactorExpansionThreshold;
	}
	
	@Override
	public IntHashMap getIntHashMap() {
		IntHashMap target = new ExpandableIntHashMap(capacity, loadFactorExpansionThreshold);
		return new SlowWriteExpandableIntHashMap(target);
	}

	@Override
	public int getCapacity() {
		return capacity;
	}
	
	public static class SlowWriteExpandableIntHashMap extends AspectableIntHashMap {

		public SlowWriteExpandableIntHashMap(IntHashMap target) {
			super(target);
		}
		
		@Override
		public void onPutEnter(int key, int value) {
			try {
				if(Thread.currentThread().getName().equals(ThreadNames.WRITE_THREAD.name())) {
					Thread.sleep(SLOW_THREAD_SIMULATED_DELAY_MS);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
