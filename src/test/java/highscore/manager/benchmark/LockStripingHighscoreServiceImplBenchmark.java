package highscore.manager.benchmark;

import static highscore.manager.benchmark.BenchmarkUtils.getUsedHeapMb;
import static highscore.manager.benchmark.BenchmarkUtils.randomConcurrentInt;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import highscore.manager.service.HighscoreService;
import highscore.manager.service.datastructure.IntHeap;
import highscore.manager.service.datastructure.impl.CappedIntHashMapFactory;
import highscore.manager.service.impl.LockStripingHighscoreServiceImpl;

public class LockStripingHighscoreServiceImplBenchmark {

	public static void main(String[] args) throws InterruptedException {
		//-Xmx2g -XX:+UseConcMarkSweepGC -XX:+UseCompressedOops
		//16 threads
		System.out.println("LockStripingHighscoreServiceImpl");
		
		//350	3.9
		readAndUpdateBenchmark(1_000_000);
		
		//465	4.1
		readReadAndUpdateBenchmark(1_000_000);
	}
	
	private static void readAndUpdateBenchmark(int repetitions) throws InterruptedException {
		System.gc();
		HighscoreService<IntHeap> service = new LockStripingHighscoreServiceImpl(new CappedIntHashMapFactory((byte)15), 8);
		ExecutorService executorService = Executors.newFixedThreadPool(16);
		long st = System.currentTimeMillis();
		for(int i = 0; i < repetitions; i++) {
			executorService.execute(() -> service.getSortedHighscores(randomConcurrentInt()));
			executorService.execute(() -> service.update(randomConcurrentInt(), randomConcurrentInt(), randomConcurrentInt()));
		}
		executorService.shutdown();
		executorService.awaitTermination(10, TimeUnit.MINUTES);
		long et = System.currentTimeMillis();
		System.out.println("Used heap : " + getUsedHeapMb() + " Mb");
		System.gc();
		System.out.println("Read and update " + repetitions + " : " + (et - st) + " ms");
	}
	
	private static void readReadAndUpdateBenchmark(int repetitions) throws InterruptedException {
		System.gc();
		HighscoreService<IntHeap> service = new LockStripingHighscoreServiceImpl(new CappedIntHashMapFactory((byte)15), 8);
		ExecutorService executorService = Executors.newFixedThreadPool(16);
		long st = System.currentTimeMillis();
		for(int i = 0; i < repetitions; i++) {
			executorService.execute(() -> service.getSortedHighscores(randomConcurrentInt()));
			executorService.execute(() -> service.getSortedHighscores(randomConcurrentInt()));
			executorService.execute(() -> service.update(randomConcurrentInt(), randomConcurrentInt(), randomConcurrentInt()));
		}
		executorService.shutdown();
		executorService.awaitTermination(10, TimeUnit.MINUTES);
		long et = System.currentTimeMillis();
		System.out.println("Used heap : " + getUsedHeapMb() + " Mb");
		System.gc();
		System.out.println("Read Read and update " + repetitions + " : " + (et - st) + " ms");
	}
}
