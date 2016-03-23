package highscore.manager.service.datastructure.impl;

public class MinIntHeap extends AbstractIntHeap {

	public MinIntHeap(byte capacity) {
		super(capacity);
	}

	@Override
	protected int compare(int v1, int v2) {
		return v1 - v2;
	}
}
