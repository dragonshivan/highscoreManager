package highscore.manager.service.datastructure;

public interface IntHashMap {
	
	static final byte EMPTY_LOCATION = -1;
	static final byte NOT_FOUND = -2;
	
	void put(int key, int value);
	int get(int key);
	void remove(int key);
	void stream(EntryProcessor entryProcessor);
	int getSize();
	default IntHashMap deepCopy() {
		throw new UnsupportedOperationException();
	}
	
	@FunctionalInterface
	static interface EntryProcessor {
		void process(int key, int value);
	}
}
