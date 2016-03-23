package highscore.manager.service.datastructure.impl;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

public class ExpandableIntHashMapTest {

	private ExpandableIntHashMap hashMap;
	
	@Before
	public void init() {
		hashMap = new ExpandableIntHashMap(10, 8);
	}
	
	@Test
	public void testPutNotFull() {
		assertEquals(0, hashMap.getSize());
		hashMap.put(1, 10);
		assertEquals(10, hashMap.get(1));
		assertEquals(1, hashMap.getSize());
	}
	
	@Test
	public void testReplaceNotFull() {
		assertEquals(0, hashMap.getSize());
		hashMap.put(1, 10);
		assertEquals(10, hashMap.get(1));
		assertEquals(1, hashMap.getSize());
		hashMap.put(1, 15);
		assertEquals(15, hashMap.get(1));
		assertEquals(1, hashMap.getSize());
	}
	
	@Test
	public void testPutFull() {
		assertEquals(0, hashMap.getSize());
		hashMap.put(1, 10);
		assertEquals(10, hashMap.get(1));
		assertEquals(1, hashMap.getSize());
		hashMap.put(2, 20);
		assertEquals(20, hashMap.get(2));
		assertEquals(2, hashMap.getSize());
		hashMap.put(3, 30);
		assertEquals(30, hashMap.get(3));
		assertEquals(3, hashMap.getSize());
	}
	
	@Test
	public void testReplaceFull() {
		assertEquals(0, hashMap.getSize());
		hashMap.put(1, 10);
		assertEquals(10, hashMap.get(1));
		assertEquals(1, hashMap.getSize());
		hashMap.put(2, 20);
		assertEquals(20, hashMap.get(2));
		assertEquals(2, hashMap.getSize());
		hashMap.put(3, 30);
		assertEquals(30, hashMap.get(3));
		assertEquals(3, hashMap.getSize());
		hashMap.put(1, 100);
		assertEquals(100, hashMap.get(1));
		assertEquals(3, hashMap.getSize());
		hashMap.put(2, 200);
		assertEquals(200, hashMap.get(2));
		assertEquals(3, hashMap.getSize());
		hashMap.put(3, 300);
		assertEquals(300, hashMap.get(3));
		assertEquals(3, hashMap.getSize());
	}
	
	@Test
	public void testRandomPutAndGet() {
		HashMap<Integer, Integer> controlMap = new HashMap<>();
		Random random = new Random();
		int assertinsCount = 1_000_000;
		int assertionsCounter = 0;
		for(int i = 0; i < assertinsCount; i++) {
			int k = random.nextInt(Integer.MAX_VALUE);
			int v = random.nextInt(Integer.MAX_VALUE);
			hashMap.put(k, v);
			controlMap.put(k, v);
			assertEquals(controlMap.size(), hashMap.getSize());
			assertEquals((int)controlMap.get(k), hashMap.get(k));
			assertionsCounter++;
		}
		assertEquals(assertinsCount, assertionsCounter);
	}
}
