package highscore.manager.IT.mock;

import highscore.manager.service.datastructure.IntHashMap;

public class AspectableIntHashMap implements IntHashMap {
	
	public static final int FAST_THREAD_RECOMMENDED_DELAY_MS = 20;
	
	public static final int SLOW_THREAD_SIMULATED_DELAY_MS = 500;
	
	public enum ThreadNames {
		READ_THREAD,
		WRITE_THREAD;
	}
	
	private final IntHashMap target;
	
	public AspectableIntHashMap(IntHashMap target) {
		this.target = target;
	}

	@Override
	public void put(int key, int value) {
		onPutEnter(key, value);
		try {
			target.put(key, value);
		} finally {
			onPutExit();
		}
	}
	
	public void onPutEnter(int key, int value) {}
	public void onPutExit() {}

	@Override
	public int get(int key) {
		onGetEnter(key);
		try {
			return  target.get(key);
		} finally {
			onGetExit();
		}
	}
	
	public void onGetEnter(int key) {}
	public void onGetExit() {}

	@Override
	public void remove(int key) {
		onRemoveEnter(key);
		try {
			target.remove(key);
		} finally {
			onRemoveExit();
		}
	}
	
	public void onRemoveEnter(int key) {}
	public void onRemoveExit() {}

	@Override
	public void stream(EntryProcessor entryProcessor) {
		onStreamEnter(entryProcessor);
		try {
			target.stream(entryProcessor);
		} finally {
			onStreamExit();
		}
	}
	
	public void onStreamEnter(EntryProcessor entryProcessor) {}
	public void onStreamExit() {}

	@Override
	public int getSize() {
		return target.getSize();
	}
	
	@Override
	public IntHashMap deepCopy() {
		return target.deepCopy();
	}
}