package highscore.manager.benchmark;

import static highscore.manager.benchmark.BenchmarkUtils.getUsedHeapMb;
import static highscore.manager.benchmark.BenchmarkUtils.randomInt;

import highscore.manager.service.datastructure.impl.ExpandableIntHashMap;


public class ExpandableIntHashMapBenchmark {

	public static void main(String[] args) {
		//-Xmx2g -XX:+UseConcMarkSweepGC -XX:+UseCompressedOops
		// initialCapacity = 10, loadFactorThreashold = 8
		System.out.println("ExpandableIntHashMapBenchmark");
				
		// ~300 Mb (3.3 seconds)
		putBenchmark(10_000_000);
		
//		// ~350 Mb (3.9 seconds)
		putAndGetBenchmark(10_000_000);
		
//		// ~300 Mb (3.6 seconds)
		putAndRemoveBenchmark(10_000_000);
	}
	
	private static void putBenchmark(int repetitions) {
		System.gc();
		ExpandableIntHashMap map = new ExpandableIntHashMap(10, 8);
		long st = System.currentTimeMillis();
		for(int i = 0; i < repetitions; i++) {
			map.put(randomInt(), randomInt());
		}
		long et = System.currentTimeMillis();
		System.out.println("Used heap : " + getUsedHeapMb() + " Mb");
		System.gc();
		System.out.println("Put " + repetitions + " : " + (et - st) + " ms");
	}
	
	private static void putAndGetBenchmark(int repetitions) {
		System.gc();
		ExpandableIntHashMap map = new ExpandableIntHashMap(10, 8);
		long st = System.currentTimeMillis();
		for(int i = 0; i < repetitions; i++) {
			map.put(randomInt(), randomInt());
		}
		for(int i = 0; i < repetitions; i++) {
			map.get(randomInt());
		}
		long et = System.currentTimeMillis();
		System.out.println("Used heap : " + getUsedHeapMb() + " Mb");
		System.gc();
		System.out.println("Put and get " + repetitions + " : " + (et - st) + " ms");
	}
	
	private static void putAndRemoveBenchmark(int repetitions) {
		System.gc();
		ExpandableIntHashMap map = new ExpandableIntHashMap(10, 8);
		long st = System.currentTimeMillis();
		for(int i = 0; i < repetitions; i++) {
			map.put(randomInt(), randomInt());
		}
		for(int i = 0; i < repetitions; i++) {
			map.remove(randomInt());
		}
		long et = System.currentTimeMillis();
		System.out.println("Used heap : " + getUsedHeapMb() + " Mb");
		System.gc();
		System.out.println("Put and remove " + repetitions + " : " + (et - st) + " ms");
	}
}
