package highscore.manager.service.datastructure.impl;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class MaxIntHeapTest {

	private MaxIntHeap heap;
	
	@Before
	public void init() {
		heap = new MaxIntHeap((byte) 5);
	}
	
	@Test
	public void testPushAndPopNotFull() {
		heap.push(5, 1);
		heap.push(2, 1);
		heap.push(2, 1);
		heap.push(3, 1);
		assertEquals(5, heap.pop()[0]);
		assertEquals(3, heap.pop()[0]);
		assertEquals(2, heap.pop()[0]);
		assertEquals(2, heap.pop()[0]);
	}
	
	@Test
	public void testPushAndPeekNotFull() {
		heap.push(3, 1);
		heap.push(2, 1);
		heap.push(5, 1);
		assertEquals(5, heap.peek()[0]);
		heap.pop();
		assertEquals(3, heap.peek()[0]);
		heap.pop();
		assertEquals(2, heap.peek()[0]);
		heap.pop();
	}
	
	@Test
	public void testPushAndPopFull() {
		heap.push(30, 1);
		heap.push(20, 1);
		heap.push(50, 1);
		heap.push(10, 1);
		heap.push(5, 1);
		assertEquals(50, heap.pop()[0]);
		assertEquals(30, heap.pop()[0]);
		assertEquals(20, heap.pop()[0]);
		assertEquals(10, heap.pop()[0]);
		assertEquals(5, heap.pop()[0]);
	}
	
	@Test
	public void testPushAndPeekFull() {
		heap.push(30, 1);
		heap.push(20, 1);
		heap.push(50, 1);
		heap.push(10, 1);
		heap.push(5, 1);
		assertEquals(50, heap.peek()[0]);
		heap.pop();
		assertEquals(30, heap.peek()[0]);
		heap.pop();
		assertEquals(20, heap.peek()[0]);
		heap.pop();
		assertEquals(10, heap.peek()[0]);
		heap.pop();
		assertEquals(5, heap.peek()[0]);
		heap.pop();
	}
	
	@Test
	public void testIsEmptyIsFull() {
		assertTrue(heap.isEmpty());
		assertFalse(heap.isFull());
		heap.push(3, 1);
		assertFalse(heap.isEmpty());
		assertFalse(heap.isFull());
		heap.push(3, 1);
		heap.push(3, 1);
		heap.push(3, 1);
		heap.push(3, 1);
		assertFalse(heap.isEmpty());
		assertTrue(heap.isFull());		
	}
	
	@Test
	public void testPushAndPopAssociatedValue() {
		heap.push(30, 1);
		heap.push(20, 2);
		heap.push(50, 3);
		heap.push(10, 4);
		heap.push(5, 555);
		assertEquals(3, heap.pop()[1]);
		assertEquals(1, heap.pop()[1]);
		assertEquals(2, heap.pop()[1]);
		assertEquals(4, heap.pop()[1]);
		assertEquals(555, heap.pop()[1]);
	}
}
