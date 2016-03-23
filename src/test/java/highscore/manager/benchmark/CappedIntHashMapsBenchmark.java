package highscore.manager.benchmark;

import static highscore.manager.benchmark.BenchmarkUtils.*;

import highscore.manager.service.datastructure.impl.CappedIntHashMap;;

public class CappedIntHashMapsBenchmark {
	
	
	public static void main(String[] args) {
		//-Xmx2g -XX:+UseConcMarkSweepGC -XX:+UseCompressedOops
		System.out.println("CappedIntHashMapsBenchmark");
		
		//~373 Mb (1.1 seconds)
		putBenchmark(1_000_000);
		
		//~370 Mb (1.2 seconds)
		putAndGetBenchmark(1_000_000);
		
		//~374 Mb (1.1 seconds)
		putAndRemoveBenchmark(1_000_000);
	}
	
	private static void putBenchmark(int repetitions) {
		System.gc();
		CappedIntHashMap[] maps = new CappedIntHashMap[repetitions];
		long st = System.currentTimeMillis();
		for(int i = 0; i < repetitions; i++) {
			CappedIntHashMap map = new CappedIntHashMap((byte) 15);
 			maps[i] = map;
			for(int j = 0; j < 15; j++) {
				map.put(randomInt(), randomInt());
			}
		}
		long et = System.currentTimeMillis();
		System.out.println("Used heap : " + getUsedHeapMb() + " Mb");
		System.gc();
		System.out.println("Put " + repetitions + " : " + (et - st) + " ms");
	}
	
	private static void putAndGetBenchmark(int repetitions) {
		System.gc();
		CappedIntHashMap[] maps = new CappedIntHashMap[repetitions];
		long st = System.currentTimeMillis();
		for(int i = 0; i < repetitions; i++) {
			CappedIntHashMap map = new CappedIntHashMap((byte) 15);
			maps[i] = map;
			for(int j = 0; j < 15; j++) {
				map.put(randomInt(), randomInt());
			}
			for(int j = 0; j < 15; j++) {
				map.get(randomInt());
			}
		}
		long et = System.currentTimeMillis();
		System.out.println("Used heap : " + getUsedHeapMb() + " Mb");
		System.gc();
		System.out.println("Put and get " + repetitions + " : " + (et - st) + " ms");
	}
	
	private static void putAndRemoveBenchmark(int repetitions) {
		System.gc();
		CappedIntHashMap[] maps = new CappedIntHashMap[repetitions];
		long st = System.currentTimeMillis();
		for(int i = 0; i < repetitions; i++) {
			CappedIntHashMap map = new CappedIntHashMap((byte) 15);
			maps[i] = map;
			for(int j = 0; j < 15; j++) {
				map.put(randomInt(), randomInt());
			}
			for(int j = 0; j < 15; j++) {
				map.remove(randomInt());
			}
		}
		long et = System.currentTimeMillis();
		System.out.println("Used heap : " + getUsedHeapMb() + " Mb");
		System.gc();
		System.out.println("Put and remove " + repetitions + " : " + (et - st) + " ms");
	}
}
