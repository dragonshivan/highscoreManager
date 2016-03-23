package highscore.manager.IT.mock;

import highscore.manager.service.datastructure.IntHashMap;
import highscore.manager.service.datastructure.IntHashMapFactory;
import highscore.manager.service.datastructure.impl.CappedIntHashMap;

public class SlowWriteCappedIntHashMapFactory implements IntHashMapFactory {

	private final byte capacity;
	
	public SlowWriteCappedIntHashMapFactory(byte capacity) {
		this.capacity = capacity;
	}
	
	@Override
	public IntHashMap getIntHashMap() {
		CappedIntHashMap target = new CappedIntHashMap(capacity);
		return new SlowWriteCappedIntHashMap(target);
	}

	@Override
	public int getCapacity() {
		return capacity;
	}
	
	public static class SlowWriteCappedIntHashMap extends AspectableIntHashMap {

		public SlowWriteCappedIntHashMap(IntHashMap target) {
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
