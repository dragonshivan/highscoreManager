package highscore.manager.IT.mock;

import highscore.manager.service.datastructure.IntHashMap;
import highscore.manager.service.datastructure.IntHashMapFactory;
import highscore.manager.service.datastructure.impl.CappedIntHashMap;

public class SlowStreamCappedIntHashMapFactory implements IntHashMapFactory {

	private final byte capacity;
	
	public SlowStreamCappedIntHashMapFactory(byte capacity) {
		this.capacity = capacity;
	}
	
	@Override
	public IntHashMap getIntHashMap() {
		CappedIntHashMap target = new CappedIntHashMap(capacity);
		return new SlowStreamCappedIntHashMap(target);
	}

	@Override
	public int getCapacity() {
		return capacity;
	}
	
	public static class SlowStreamCappedIntHashMap extends AspectableIntHashMap {

		public SlowStreamCappedIntHashMap(IntHashMap target) {
			super(target);
		}
		
		@Override
		public void onStreamEnter(EntryProcessor entryProcessor) {
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
