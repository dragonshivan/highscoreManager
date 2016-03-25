package highscore.manager.benchmark;

import static highscore.manager.benchmark.BenchmarkUtils.getUsedHeapMb;
import static highscore.manager.benchmark.BenchmarkUtils.randomInteger;
import static highscore.manager.benchmark.BenchmarkUtils.randomLongObject;

import java.util.HashMap;

public class HashMapBenchmark {

	public static void main(String[] args) {
		//-Xmx2g -XX:+UseConcMarkSweepGC -XX:+UseCompressedOops
		// initialCapacity = 10, loadFactor 0.75
		System.out.println("HashMapBenchmark");
				
//		// ~870 Mb (3.8 seconds)
//		putIntBenchmark(10_000_000);
//		
//		// ~1000 Mb (4.6 seconds)
//		putAndGetIntBenchmark(10_000_000);
//		
//		// ~1080 Mb (4.7 seconds)
//		putAndRemoveIntBenchmark(10_000_000);
//		
//		//1001 2.9
//		putLongBenchmark(10_000_000);
//		
//		//1148 4.0
//		putAndGetLongBenchmark(10_000_000);
//		
//		//1149 4.0
//		putAndRemoveLongBenchmark(10_000_000);
		
		//-Xmx2g -XX:+UseConcMarkSweepGC -XX:+UseCompressedOops
		// initialCapacity = 10, loadFactor 3
		
		// 498 9
		putIntBenchmark(10_000_000);
		
		// 609 6.0
		putAndGetIntBenchmark(10_000_000);
		
		// 613 6.0
		putAndRemoveIntBenchmark(10_000_000);
		
		// 653 4.4 
		putLongBenchmark(10_000_000);
		
		// 695 6.6
		putAndGetLongBenchmark(10_000_000);
		
		// 630 6.6
		putAndRemoveLongBenchmark(10_000_000);
	}
	
	private static void putIntBenchmark(int repetitions) {
		System.gc();
		HashMap<Integer, Integer> map = new HashMap<>(10, 3f);
		long st = System.currentTimeMillis();
		for(int i = 0; i < repetitions; i++) {
			map.put(randomInteger(), randomInteger());
		}
		long et = System.currentTimeMillis();
		System.out.println("Used heap : " + getUsedHeapMb() + " Mb");
		System.gc();
		System.out.println("Put int " + repetitions + " : " + (et - st) + " ms");
	}
	
	private static void putAndGetIntBenchmark(int repetitions) {
		System.gc();
		HashMap<Integer, Integer> map = new HashMap<>(10, 3f);
		long st = System.currentTimeMillis();
		for(int i = 0; i < repetitions; i++) {
			map.put(randomInteger(), randomInteger());
		}
		for(int i = 0; i < repetitions; i++) {
			map.get(randomInteger());
		}
		long et = System.currentTimeMillis();
		System.out.println("Used heap : " + getUsedHeapMb() + " Mb");
		System.gc();
		System.out.println("Put and get int " + repetitions + " : " + (et - st) + " ms");
	}
	
	private static void putAndRemoveIntBenchmark(int repetitions) {
		System.gc();
		HashMap<Integer, Integer> map = new HashMap<>(10, 3f);
		long st = System.currentTimeMillis();
		for(int i = 0; i < repetitions; i++) {
			map.put(randomInteger(), randomInteger());
		}
		for(int i = 0; i < repetitions; i++) {
			map.remove(randomInteger());
		}
		long et = System.currentTimeMillis();
		System.out.println("Used heap : " + getUsedHeapMb() + " Mb");
		System.gc();
		System.out.println("Put and remove int " + repetitions + " : " + (et - st) + " ms");
	}
	
	private static void putLongBenchmark(int repetitions) {
		System.gc();
		HashMap<Integer, Long> map = new HashMap<>(10, 3f);
		long st = System.currentTimeMillis();
		for(int i = 0; i < repetitions; i++) {
			map.put(randomInteger(), randomLongObject());
		}
		long et = System.currentTimeMillis();
		System.out.println("Used heap : " + getUsedHeapMb() + " Mb");
		System.gc();
		System.out.println("Put long " + repetitions + " : " + (et - st) + " ms");
	}
	
	private static void putAndGetLongBenchmark(int repetitions) {
		System.gc();
		HashMap<Integer, Long> map = new HashMap<>(10, 3f);
		long st = System.currentTimeMillis();
		for(int i = 0; i < repetitions; i++) {
			map.put(randomInteger(), randomLongObject());
		}
		for(int i = 0; i < repetitions; i++) {
			map.get(randomInteger());
		}
		long et = System.currentTimeMillis();
		System.out.println("Used heap : " + getUsedHeapMb() + " Mb");
		System.gc();
		System.out.println("Put and get long " + repetitions + " : " + (et - st) + " ms");
	}
	
	private static void putAndRemoveLongBenchmark(int repetitions) {
		System.gc();
		HashMap<Integer, Long> map = new HashMap<>(10, 3f);
		long st = System.currentTimeMillis();
		for(int i = 0; i < repetitions; i++) {
			map.put(randomInteger(), randomLongObject());
		}
		for(int i = 0; i < repetitions; i++) {
			map.remove(randomInteger());
		}
		long et = System.currentTimeMillis();
		System.out.println("Used heap : " + getUsedHeapMb() + " Mb");
		System.gc();
		System.out.println("Put and remove long " + repetitions + " : " + (et - st) + " ms");
	}
}
