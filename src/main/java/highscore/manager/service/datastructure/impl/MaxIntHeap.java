package highscore.manager.service.datastructure.impl;

public class MaxIntHeap extends AbstractIntHeap {

	public MaxIntHeap(byte capacity) {
		super(capacity);
	}

	@Override
	protected int compare(int v1, int v2) {
		return v2 - v1;
	}
}
