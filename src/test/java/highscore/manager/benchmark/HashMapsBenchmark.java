package highscore.manager.benchmark;

import static highscore.manager.benchmark.BenchmarkUtils.*;

import java.util.HashMap;

public class HashMapsBenchmark {
	
	
	public static void main(String[] args) {
		//-Xmx2g -XX:+UseConcMarkSweepGC -XX:+UseCompressedOops
		System.out.println("HashMapsBenchmark");
				
		// ~1200 Mb (2.4 seconds)
		putBenchmark(1_000_000);
		
		// ~1500 Mb (2 seconds)
		putAndGetBenchmark(1_000_000);
		
		// ~1450 Mb (1.8 seconds)
		putAndRemoveBenchmark(1_000_000);
	}
	
	private static void putBenchmark(int repetitions) {
		System.gc();
		HashMap<?, ?>[] maps = new HashMap[repetitions];
		long st = System.currentTimeMillis();
		for(int i = 0; i < repetitions; i++) {
			HashMap<Integer, Integer> map = new HashMap<>(1, 4f);
 			maps[i] = map;
			for(int j = 0; j < 15; j++) {
				map.put(randomInteger(), randomInteger());
			}
		}
		long et = System.currentTimeMillis();
		System.out.println("Used heap : " + getUsedHeapMb() + " Mb");
		System.gc();
		System.out.println("Put " + repetitions + " : " + (et - st) + " ms");
	}
	
	private static void putAndGetBenchmark(int repetitions) {
		System.gc();
		HashMap<?, ?>[] maps = new HashMap[repetitions];
		long st = System.currentTimeMillis();
		for(int i = 0; i < repetitions; i++) {
			HashMap<Integer, Integer> map = new HashMap<>(15);
			maps[i] = map;
			for(int j = 0; j < 15; j++) {
				map.put(randomInteger(), randomInteger());
			}
			for(int j = 0; j < 15; j++) {
				map.get(randomInteger());
			}
		}
		long et = System.currentTimeMillis();
		System.out.println("Used heap : " + getUsedHeapMb() + " Mb");
		System.gc();
		System.out.println("Put and get " + repetitions + " : " + (et - st) + " ms");
	}
	
	private static void putAndRemoveBenchmark(int repetitions) {
		System.gc();
		HashMap<?, ?>[] maps = new HashMap[repetitions];
		long st = System.currentTimeMillis();
		for(int i = 0; i < repetitions; i++) {
			HashMap<Integer, Integer> map = new HashMap<>(15);
			maps[i] = map;
			for(int j = 0; j < 15; j++) {
				map.put(randomInteger(), randomInteger());
			}
			for(int j = 0; j < 15; j++) {
				map.remove(randomInteger());
			}
		}
		long et = System.currentTimeMillis();
		System.out.println("Used heap : " + getUsedHeapMb() + " Mb");
		System.gc();
		System.out.println("Put and remove " + repetitions + " : " + (et - st) + " ms");
	}
}
